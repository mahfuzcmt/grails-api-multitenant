package com.bitsoft.st.utils

class AppConstant {

    static final String GOOGLE_DRIVER_PREVIEW_BASE_URL = "https://drive.google.com/uc?export=view&id="
    static final String GOOGLE_API_KEY = "AIzaSyAxqegCwIwNRjr6Z2kFJCzwj_s_-zDCOxY"
    static def grailsApplication
    static String tenantId
    static String companyName
    static String developerCompany
    static String complainText
    static String companyAddress
    static String ticketDateFormat
    static Integer hideSalesAmountAfterInSec
    static Integer syncSalesDataInMin


    AppConstant(def grailsApplication){
        this.grailsApplication = grailsApplication
    }
    static String getProdBaseUrl(){
        return this.grailsApplication.config.application.base_url
    }

    static String getDevBaseUrl(){
        return this.grailsApplication.config.application.local_base_url
    }

    static String getPort(){
        return this.grailsApplication.config.server.port
    }

    static void setTenantId(String tenantId){
       this.tenantId = tenantId
    }

    static void setCompanyName(String companyName) {
        this.companyName = companyName
    }

    static void setDeveloperCompany(String developerCompany) {
        this.developerCompany = developerCompany
    }

    static void setComplainText(String complainText) {
        this.complainText = complainText
    }

    static void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress
    }

    static String getTicketDateFormat() {
        return ticketDateFormat
    }

    static void setTicketDateFormat(String ticketDateFormat) {
        this.ticketDateFormat = ticketDateFormat
    }

    static Integer getHideSalesAmountAfterInSec() {
        return hideSalesAmountAfterInSec ?: 10
    }

    static void setHideSalesAmountAfterInSec(Integer hideSalesAmountAfterInSec) {
        this.hideSalesAmountAfterInSec = hideSalesAmountAfterInSec
    }

    static Integer getSyncSalesDataInMin() {
        return syncSalesDataInMin ?: 15
    }

    static void setSyncSalesDataInMin(Integer syncSalesDataInMin) {
        this.syncSalesDataInMin = syncSalesDataInMin
    }

    static String getTenantId(){
        return this.tenantId
    }

    static String getDeveloperCompany(){
        return this.developerCompany
    }
    static String getComplainText(){
        return this.complainText ?: ""
    }

    static String getCompanyAddress(){
        return this.companyAddress ?: ""
    }
    static String getCompanyName(){
        return this.companyName ?: ""
    }

    static getROLE() {
        return [
                ADMIN    : "Admin",
                COLLECTOR: "Collector",
                USER     : "User",
                AGENT    : "Agent",
        ]
    }

    static getSTATUS() {
        return [
                ACTIVE  : "Active",
                INACTIVE: "Inactive",
        ]
    }

    static getCLIENT_CATEGORY() {
        return [
                BUS   : "Bus",
                LAUNCH: "Launch",
        ]
    }

    static getTRANSACTION_TYPE() {
        return [
                ADD  : "add",
                DEDUCT: "deduct",
        ]
    }
    static getTRANSACTION_STATUS() {
        return [
                SUCCESS  : "success",
                FAILED: "failed",
                REVERSED: "reversed",
        ]
    }

    static getREVERSED_TYPE() {
        return [
                AUTO  : "Auto",
                MANUALLY: "Manually",
        ]
    }

    static getTICKET_CHECKING_STATUS() {
        return [
                NOT_CHECKED  : "not checked",
                CHECKED: "checked",
        ]
    }

    static String getBaseUrl(){
        if (grails.util.Environment.current.equals(grails.util.Environment.DEVELOPMENT)) {
            return getDevBaseUrl()
        } else if (grails.util.Environment.current.equals(grails.util.Environment.PRODUCTION)) {
            return getProdBaseUrl()

        }
    }

    static String rootPathOfProd = "${File.separator}var${File.separator}lib${File.separator}tomcat8/webapps${File.separator}"

    static getTRIP_TYPE() {
        return [
                UP  : "Up",
                DOWN: "Down",
        ]
    }


    static getSHIFT() {
        return [
                MORNING  : "Morning",
                EVENGING: "Evening",
        ]
    }

    static getOPERATION_TYPE() {
        return [
                LOGIN  : "Login",
                LOGOUT: "Logout",
                CHANGE_PASSWORD: "Change Password",
        ]
    }

    static getFILE_PARSING_STATUS() {
        return [
                STARTED  : "started",
                PROCESSING: "processing",
                FAILED: "failed",
                COMPLETED: "completed",
        ]
    }

    static getLOG_TYPE() {
        return [
                SUCCESS  : "success",
                ERROR: "error"
        ]
    }


    static getSESSION_ATTRIBUTE() {
        return [
                TENANT_ID                     : "tenantId",
                CATEGORY                      : "category",
                COMPANY_NAME                  : "companyName",
                COMPLAIN_TEXT                 : "complainText",
                COMPANY_ADDRESS               : "companyAddress",
                TICKET_CREDIT                 : "ticketCredit",
                TICKET_DATE_FORMAT            : "ticketDateFormat",
                SYNC_SALES_DATAINMIN          : "syncSalesDataInMin",
                REPORT_PRINT_LIMIT            : "reportPrintLimit",
                HIDE_SALES_AMOUNT_AFTER_IN_SEC: "hideSalesAmountAfterInSec",
        ]
    }

}