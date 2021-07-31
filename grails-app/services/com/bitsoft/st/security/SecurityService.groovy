package com.bitsoft.st.security

import com.bitsoft.st.Kounter
import com.bitsoft.st.Role
import com.bitsoft.st.Route
import com.bitsoft.st.SalesTrace
import com.bitsoft.st.TripInfo
import com.bitsoft.st.User
import com.bitsoft.st.UserService
import com.bitsoft.st.route.TripService
import com.bitsoft.st.utils.AppConstant
import com.bitsoft.st.utils.AppUtil
import com.bitsoft.st.utils.DateTimeUtil
import grails.gorm.multitenancy.CurrentTenant
import grails.gorm.transactions.Transactional
import groovy.json.JsonSlurper
import org.apache.commons.lang.RandomStringUtils

@CurrentTenant
@Transactional
class SecurityService {

    UserService userService
    TripService tripService

    Map<Long, String> authTokenMap = [:]

    def generateToken() {
        return RandomStringUtils.random(32, true, true)
    }

    Long getMaxSerialNo(Kounter kounter, User user) {
        SalesTrace.createCriteria().list {
            eq("salesDate", DateTimeUtil.getFormattedDate(new Date(), "dd-MM-yyyy"))
            eq("counter.id", kounter.id)
            eq("user.id", user.id)
        }?.max { it.lastSerialNo }?.lastSerialNo ?: 0
    }

    List getRoutesByUserId(Long userid) {
        List availableRoutes = []
        User user = userService.getUserById(userid)
        if (user && user.respectiveCounterId) {
            Kounter kounter = Kounter.findByIdAndStatus(user.respectiveCounterId, AppConstant.STATUS.ACTIVE)
            if (kounter) {
                kounter.routes.each { Route route ->
                    Map newRoute = [:]
                    if (route.status.equals(AppConstant.STATUS.ACTIVE)) {
                        newRoute.routeId = route.id
                        newRoute.name = route.name
                        newRoute.tripType = route.tripType
                        newRoute.colorCode = route.colorCode
                        newRoute.fromLocation = route.fromLocation
                        newRoute.toLocation = route.toLocation
                        newRoute.fare = route.fare
                        availableRoutes.add(newRoute)
                    }
                }
                availableRoutes
            }
        }
        if(AppUtil.session[AppConstant.SESSION_ATTRIBUTE.CATEGORY].equals(AppConstant.CLIENT_CATEGORY.LAUNCH)){
            availableRoutes = availableRoutes.groupBy {it.tripType}
        }
        return availableRoutes
    }


    def getUserPublicInfo(User user) {
        def userInfo = [:]

        if (user) {
            userInfo.id = user.id
            userInfo.menuJSON = user.role?.menuJson
            userInfo.role = user.role?.name
            userInfo.fullName = user.fullName
            userInfo.userName = user.userName
            userInfo.contact = user.contactNo
            userInfo.isWayBillFeatureEnabled = user.isWayBillFeatureEnabled
            if (!user.role) {
                Kounter kounter = Kounter.findByIdAndStatus(user.respectiveCounterId, AppConstant.STATUS.ACTIVE)
                if (kounter) {
                    def availableRoutes = []
                    def availableUpRoutes = []
                    def availableDownRoutes = []
                    kounter.routes.each { Route route ->
                        Map newRoute = [:]
                        if (route.status.equals(AppConstant.STATUS.ACTIVE)) {
                            newRoute.routeId = route.id
                            newRoute.name = route.name
                            newRoute.tripType = route.tripType
                            newRoute.colorCode = route.colorCode
                            newRoute.fromLocation = route.fromLocation
                            newRoute.toLocation = route.toLocation
                            newRoute.fare = route.fare
                            availableRoutes.add(newRoute)
                            if (route.tripType.equals(AppConstant.TRIP_TYPE.UP)) {
                                availableUpRoutes.add(newRoute)
                            } else {
                                availableDownRoutes.add(newRoute)
                            }
                        }
                    }
                    TripInfo tripInfo = tripService.getCurrentTrip()
                    userInfo.currentTripNo = 1
                    if (tripInfo) {
                        userInfo.currentTripNo = tripInfo.tripNo
                        userInfo.tripDate = tripInfo.startDate
                    }
                    userInfo.counterId = kounter.id
                    userInfo.tripType = kounter.tripType
                    userInfo.serialNo = getMaxSerialNo(kounter, user)
                    userInfo.availableRoutes = availableRoutes
                    userInfo.availableUpRoutes = availableUpRoutes
                    userInfo.availableDownRoutes = availableDownRoutes
                    userInfo.companyName = AppUtil.session[AppConstant.SESSION_ATTRIBUTE.COMPANY_NAME]
                    userInfo.companyAddress = AppUtil.session[AppConstant.SESSION_ATTRIBUTE.COMPANY_ADDRESS]
                    userInfo.creditText = AppUtil.session[AppConstant.SESSION_ATTRIBUTE.TICKET_CREDIT]
                    userInfo.complainText = AppUtil.session[AppConstant.SESSION_ATTRIBUTE.COMPLAIN_TEXT]
                    userInfo.ticketDateFormat = AppUtil.session[AppConstant.SESSION_ATTRIBUTE.TICKET_DATE_FORMAT]
                    userInfo.hideSalesAmountAfterInSec = AppUtil.session[AppConstant.SESSION_ATTRIBUTE.HIDE_SALES_AMOUNT_AFTER_IN_SEC]
                    userInfo.syncSalesDataInMin = AppUtil.session[AppConstant.SESSION_ATTRIBUTE.SYNC_SALES_DATAINMIN]
                    userInfo.reportPrintLimit = AppUtil.session[AppConstant.SESSION_ATTRIBUTE.REPORT_PRINT_LIMIT]
                }
            }

            String token = generateToken()
            authTokenMap.put(user.id, token)
            userInfo.authToken = token

        }
        return userInfo
    }

    def login(Map params) {
        def userInfo = [:]
        try {
            User user = User.findByUserNameAndPasswordAndStatus(params.username, params.password, AppConstant.STATUS.ACTIVE)
            if (user && !user.role && user.deviceMac) {
                if (!user.deviceMac.equalsIgnoreCase(params.deviceMac)) {
                    return null
                }
            }
            createLoginLog(user, params)
            return getUserPublicInfo(user)
        }
        catch (Exception e) {
            log.error("params : ${params}, error : ${e.message}, tenant: ${AppUtil.session[AppConstant.SESSION_ATTRIBUTE.TENANT_ID]}")
            return userInfo
        }
    }

    def createLoginLog(User user, Map params) {
        params.operationType = AppConstant.OPERATION_TYPE.LOGIN
        params.userId = user.id
        createLog(params)
    }

    def createLogoutLog(Map params) {
        params.operationType = AppConstant.OPERATION_TYPE.LOGOUT
        createLog(params)
    }

    def createLog(Map params) {
        OperationLog operationLog = new OperationLog()
        try {
            if (params.userId) {
                operationLog.user = userService.getUserById(params.userId.toLong())
            }
            if (params.latitude && params.longitude) {
                operationLog.address = getAddressByLatAndLng(params.latitude, params.longitude)
                operationLog.latitude = params.latitude
                operationLog.longitude = params.longitude
            }
            operationLog.deviceMac = params.deviceMac
            operationLog.operationType = params.operationType ?: AppConstant.OPERATION_TYPE.LOGIN

            operationLog.save()
        }
        catch (Exception e) {
            log.error(e.message)
            return false
        }
        return operationLog
    }


    void changePassword(Long userId, Map params) {
        User user = User.findByIdAndPassword(userId, params.oldPassword?.toString())
        if (user) {
            user.password = params.newPassword
            user.save(flush: true)
            if (user.hasErrors()) {
                throw new RuntimeException("Sorry, something went wrong!")
            }
            params.operationType = AppConstant.OPERATION_TYPE.CHANGE_PASSWORD
            params.userId = userId
            createLog(params)
        } else {
            throw new RuntimeException("Wrong old password!")
        }
    }


    Boolean isRequestValid(Long userId = null, String token = null) {
        String systemToken = authTokenMap.get(userId)
        if (systemToken && systemToken.equals(token) || (grails.util.Environment.DEVELOPMENT)) {
            return true
        }
        return false
    }

    String getAddressByLatAndLng(String lat, String lon) {
        try {
            String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=${lat},${lon}&key=${AppConstant.GOOGLE_API_KEY}"
            String response = url.toURL().text
            Map responseObj = new JsonSlurper().parseText(response)
            if (responseObj && responseObj.results) {
                return responseObj.results[0].formatted_address
            }
        } catch (Exception e) {
            log.error(e.message)
        }
    }

    String getRoleNameByUser(User user) {
        if (user.role) {
            return Role.get(user.role.id)?.name
        }
        return null
    }

    Boolean initUser(Map params){
        Role collectorRole = new Role()
        Role adminRole = new Role()
        if(!Role.list()){
            adminRole.menuJson = "{\"children\":[{\"id\":\"webMenu\",\"url\":\"webMenu\",\"text\":\"Web Menu\",\"class\":\"fa fa-dashboard\",\"enable\":true,\"children\":[{\"id\":\"dashboard\",\"url\":\"dashboard\",\"text\":\"Dashboard\",\"class\":\"fa fa-dashboard\",\"enable\":true,\"children\":[]},{\"id\":\"userlist\",\"url\":\"userlist\",\"text\":\"Users\",\"class\":\"fa fa-user-plus\",\"enable\":true,\"children\":[]},{\"id\":\"report\",\"url\":\"report\",\"text\":\"Report\",\"class\":\"fa fa-file\",\"enable\":true,\"children\":[{\"id\":\"activityReport\",\"url\":\"activityReport\",\"text\":\"Activity Report\",\"class\":\"fa fa-history\",\"enable\":true,\"children\":[]},{\"id\":\"salesreport\",\"url\":\"salesreport\",\"text\":\"Sales Report\",\"class\":\"fa fa-bar-chart\",\"enable\":true,\"children\":[]},{\"id\":\"salessummary\",\"url\":\"salessummary\",\"text\":\"Sales Summary\",\"class\":\"fa fa-list-alt\",\"enable\":true,\"children\":[]},{\"id\":\"waybill\",\"url\":\"waybill\",\"text\":\"Way Bill\",\"class\":\"fa fa-forward\",\"enable\":true,\"children\":[]},{\"id\":\"ticketCount\",\"url\":\"ticketCount\",\"text\":\"Ticket Count\",\"class\":\"fa fa-calculator\",\"enable\":true,\"children\":[]},{\"id\":\"collectionReport\",\"url\":\"collectionReport\",\"text\":\"Collection Report\",\"class\":\"fa fa-university\",\"enable\":true,\"children\":[]}]},{\"id\":\"settings\",\"url\":\"settings\",\"text\":\"Settings\",\"class\":\"fas fa-cogs\",\"enable\":true,\"children\":[{\"id\":\"Counterlist\",\"url\":\"counterlist\",\"text\":\"Counter\",\"class\":\"fas fa-bus\",\"enable\":true,\"children\":[]},{\"id\":\"routelist\",\"url\":\"routelist\",\"text\":\"Route\",\"class\":\"fas fa-road\",\"enable\":true,\"children\":[]},{\"id\":\"carlist\",\"url\":\"carlist\",\"text\":\"Car\",\"class\":\"fas fa-car\",\"enable\":true,\"children\":[]},{\"id\":\"refund\",\"url\":\"refund\",\"text\":\"Refund\",\"class\":\"fa fa-undo\",\"enable\":true,\"children\":[]},{\"id\":\"manualSales\",\"url\":\"manualSales\",\"text\":\"Manual Sales\",\"class\":\"fa fa-signal\",\"enable\":true,\"children\":[]}]}]}]}"
            adminRole.name = "Admin"
            adminRole.beforeValidate()
            adminRole.save()
            collectorRole.menuJson = "N/A"
            collectorRole.name = "Collector"
            collectorRole.beforeValidate()
            collectorRole.save()
        }
        if(!User.list()){
            User user = new User()
            user.role = adminRole
            user.fullName = params.fullName
            user.contactNo = params.contactNo
            user.userName = params.userName
            user.password = user.userName
            user.beforeValidate()
            user.save()
        }
    }
}
