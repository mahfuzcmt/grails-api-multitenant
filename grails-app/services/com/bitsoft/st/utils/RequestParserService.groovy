package com.bitsoft.st.utils

import grails.gorm.multitenancy.CurrentTenant


@CurrentTenant
class RequestParserService {

    def parse(def request) {
        Map requestInfo = [:]
        String browserDetails = request.getHeader("User-Agent")
        String userAgent = browserDetails
        String user = userAgent.toLowerCase()

        String device = ""
        String os = ""
        String browser = ""
        String ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR")

        if (userAgent.toLowerCase().indexOf("windows") >= 0) {
            os = "Windows"
        } else if (userAgent.toLowerCase().indexOf("mac") >= 0) {
            os = "Mac"
        } else if (userAgent.toLowerCase().indexOf("x11") >= 0) {
            os = "Unix"
        } else if (userAgent.toLowerCase().indexOf("android") >= 0) {
            os = "Android"
        } else if (userAgent.toLowerCase().indexOf("iphone") >= 0) {
            os = "iPhone"
        } else {
            os = "Other"
        }

        if (user.contains("msie")) {
            String substring = userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0]
            browser = substring.split(" ")[0].replace("MSIE", "IE") + "-" + substring.split(" ")[1]
        } else if (user.contains("safari") && user.contains("version")) {
            browser = (userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0]).split("/")[0] + "-" + (userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1]
        } else if (user.contains("opr") || user.contains("opera")) {
            if (user.contains("opera"))
                browser = (userAgent.substring(userAgent.indexOf("Opera")).split(" ")[0]).split("/")[0] + "-" + (userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1]
            else if (user.contains("opr"))
                browser = ((userAgent.substring(userAgent.indexOf("OPR")).split(" ")[0]).replace("/", "-")).replace("OPR", "Opera")
        } else if (user.contains("chrome")) {
            browser = (userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0]).replace("/", "-")
        } else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1) || (user.indexOf("mozilla/4.7") != -1) || (user.indexOf("mozilla/4.78") != -1) || (user.indexOf("mozilla/4.08") != -1) || (user.indexOf("mozilla/3") != -1)) {

            browser = "Netscape-?"

        } else if (user.contains("firefox")) {
            browser = (userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0]).replace("/", "-")
        } else if (user.contains("rv")) {
            browser = "IE-" + user.substring(user.indexOf("rv") + 3, user.indexOf(")"))
        } else {
            browser = "Other"
        }

        if (!ipAddress) {
            ipAddress = request.getRemoteAddr()
        }

        if(request.getHeader("User-Agent").indexOf("Mobile") != -1) {
            device = "Mobile"
        } else {
            device = "Computer"
        }

        requestInfo.device = device
        requestInfo.os = os
        requestInfo.browser = browser
        requestInfo.ipAddress = ipAddress
        requestInfo.geoInfo = getGeoLocatoin(ipAddress)

        String hostname = request.getRemoteHost()

        String deviceName = null
        String remoteAddress = request.getRemoteAddr()
        try {
            InetAddress inetAddress = InetAddress.getByName(remoteAddress)
            deviceName = inetAddress.getHostName()

            if (deviceName.equalsIgnoreCase("localhost")) {
                deviceName = java.net.InetAddress.getLocalHost().getCanonicalHostName();
            }
        } catch (UnknownHostException e) {

        }
        requestInfo.deviceName = deviceName

        return requestInfo
    }

    def getGeoLocatoin(def ip){
        def get = new URL("http://ip-api.com/json/"+ip).openConnection()
        return get?.getInputStream()?.getText()
    }

}
