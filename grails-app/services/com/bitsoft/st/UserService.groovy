package com.bitsoft.st

import com.bitsoft.st.utils.AppConstant
import grails.gorm.multitenancy.CurrentTenant
import grails.gorm.transactions.Transactional

import java.text.DecimalFormat
import java.text.SimpleDateFormat

@CurrentTenant
@Transactional
class UserService {

    AppUtilService appUtilService
    DecimalFormat decimalFormat = new DecimalFormat("#.00")

    def saveUser(Map params) {
        try {
            Role role = null
            if (params.role && !params.role.equals(AppConstant.ROLE.USER)) {
                role = Role.findByName(params.role.toString())
            }
            params.role = role
            User user = new User(params)
            if (user.validate()) {
                user.save()
                if (user.hasErrors()) {
                    appUtilService.printError(user)
                    return false
                } else {
                    return user?.id
                }
            } else {
                appUtilService.printError(user)
                return false
            }
        }
        catch (Exception e) {
            log.error(e.message)
            return false
        }
    }

    @Transactional
    def updateUser(def params) {
        Long id = params?.id?.toLong()
        User user = getUserById(id)
        if (!user) {
            return false
        }
        Role role = null
        if (params.role && !params.role.equals(AppConstant.ROLE.USER)) {
            role = Role.findByName(params.role.toString())
        }
        params.role = role
        user.properties = params
        if (user.validate()) {
            user.save()
            return true
        } else {
            return false
        }
    }

    def loadUsers(def params) {
        List<User> users
        try {
            params.max = params.max ?: -1
            users = User.createCriteria().list(params) {
                if (params?.colName && params?.colValue) {
                    eq(params.colName, params.colValue)
                }
                if(params.respectiveCounterId){
                    eq("respectiveCounterId", params.respectiveCounterId.toLong())
                }
                if (!params.sort) {
                    order("id", "desc")
                }
            }
        } catch (Exception exception) {
            log.error(exception.message)
            return users
        }
        return users
    }

    User getUserById(long Id) {
        def user = User.get(Id)
        return user
    }

    String getUserFullNameByKounterId(Long kounterId) {
        List<User> userList = User.findAllByRespectiveCounterId(kounterId)
        return userList.size() ? userList.first()?.fullName ?: "" : ""
    }

    String getUserNameByKounterId(Long kounterId) {
        List<User> userList = User.findAllByRespectiveCounterId(kounterId)
        return userList.size() ? userList.first()?.userName ?: "" : ""
    }

    String getUserMobileNoByKounterId(Long kounterId) {
        List<User> userList = User.findAllByRespectiveCounterId(kounterId)
        return userList.size() ? userList.first()?.contactNo ?: "" : ""
    }

    Long getUserIdByKounterId(Long kounterId) {
        List<User> userList = User.findAllByRespectiveCounterId(kounterId)
        return userList.size() ? userList.first()?.id ?: 0l : 0l
    }

    /*def changePassword(String oldPassword, String newPassword, String retrievePassword){
        User member = getCurrentMember()
        if (!newPassword || !retrievePassword || !newPassword.equals(retrievePassword)) {
            return AppUtil.infoMessage("Your Entered Password Not Matched.", false)
        } else if (member && !member.password.equals(oldPassword.encodeAsMD5())) {
            return AppUtil.infoMessage("Incorrect Old Password.", false)
        } else {
            member = User.get(member.id)
            member.password = newPassword
            member.save(flush: true)
            setMemberAuthorization(member)
        }
        return AppUtil.infoMessage("Password Changed")
    }*/

    def deleteUser(def id) {
        try {
            def user = User.get(id)
            user.delete()
            return true
        } catch (Exception exception) {
            log.error(exception.message)
            return false
        }
    }
}
