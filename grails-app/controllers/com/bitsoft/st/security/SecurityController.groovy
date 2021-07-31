package com.bitsoft.st.security

import grails.converters.JSON

class SecurityController {

    SecurityService securityService

    static allowedMethods = [login:'POST']

    def login() {
        Map params = request.JSON
        def userInfo = securityService.login(params)
        if(userInfo){
            render([status:"success", data: userInfo] as JSON)
        } else {
            render([status:"warning", message: "Sorry, invalid request!"] as JSON)
        }
    }

    def getRoutesByUserId() {
        Map params = request.JSON
        List availableRoutes = securityService.getRoutesByUserId(params.userId?.toLong())
        if (availableRoutes) {
            render([status: "success", data: availableRoutes] as JSON)
        } else {
            render([status: "warning", message: "Sorry, no data found!"] as JSON)
        }
    }

    def changePassword() {
        Map params = request.JSON
        Long userId = params.userId?.toLong()
        if (securityService.isRequestValid(userId, params.authToken) || 1) {
            try {
                securityService.changePassword(userId, params)
            }
            catch (Exception e) {
                render([status: "error", message: e.message] as JSON)
            }
            render([status: "success", message: "Password changed successfully"] as JSON)
        } else {
            render([status: "warning", message: "Unauthorized access! Please login again"] as JSON)
        }
    }

    def logout() {
        Map params = request.JSON
        def logInfo = securityService.createLogoutLog(params)
        if(logInfo){
            render([status:"success", data: logInfo] as JSON)
        } else {
            render([status:"warning", message: "Sorry, invalid request!"] as JSON)
        }
    }

    def init() {
        Map params = request.JSON
        if (params.key == 'super') {
            Boolean result = securityService.initUser(params)
            if (result) {
                render([status: "success"] as JSON)
            } else {
                render([status: "warning", message: "Sorry!"] as JSON)
            }
        }
        render([status: "error", message: "Permission Declined!"] as JSON)
    }
}