package com.bitsoft.st.utils


import com.bitsoft.st.OpeningReport
import com.bitsoft.st.security.OperationLog
import grails.gorm.multitenancy.CurrentTenant
import groovy.json.JsonSlurper

@CurrentTenant
class EmailOpeningService {

    def saveEmailOpeningInfo(def params, String email = null) {

        def jsonSlurper = new JsonSlurper()
        def geoObject = jsonSlurper.parseText(params.geoInfo)
        OpeningReport openingReport = new OpeningReport()

        openingReport.geoInfoJson = params.geoInfo?.toString()

        openingReport.email = email
        openingReport.isRead = true
        openingReport.device = params.device
        openingReport.deviceName = params.deviceName
        openingReport.os = params.os
        openingReport.browser = params.browser
        openingReport.ip = params.ipAddress

        openingReport.country = geoObject.country
        openingReport.state = geoObject.region
        openingReport.city = geoObject.city
        openingReport.created = new Date()
        openingReport.updated = new Date()
        openingReport.save(flush: true)
        return true
    }
}
