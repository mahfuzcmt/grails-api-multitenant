package com.bitsoft.st

import com.bitsoft.st.utils.AppConstant
import org.grails.datastore.mapping.multitenancy.web.SessionTenantResolver


class ApplicationInterceptor {

    ApplicationInterceptor() {
        matchAll()
    }

    boolean before() {
        String tenantId = request.JSON.tenantId ?: params.tenantId
        Client client = Client.findByTenantIdAndStatus(tenantId, "Active")
        request.JSON.remove("tenantId")
        params.remove("tenantId")
        if (client) {
            session.setAttribute(AppConstant.SESSION_ATTRIBUTE.CATEGORY, client.category)
            session.setAttribute(AppConstant.SESSION_ATTRIBUTE.COMPANY_NAME, client.name)
            session.setAttribute(AppConstant.SESSION_ATTRIBUTE.COMPLAIN_TEXT, client.complainText)
            session.setAttribute(AppConstant.SESSION_ATTRIBUTE.COMPANY_ADDRESS, client.address)
            session.setAttribute(AppConstant.SESSION_ATTRIBUTE.TICKET_CREDIT, client.creditText)
            session.setAttribute(AppConstant.SESSION_ATTRIBUTE.TICKET_DATE_FORMAT, client.ticketDateFormat)
            session.setAttribute(AppConstant.SESSION_ATTRIBUTE.HIDE_SALES_AMOUNT_AFTER_IN_SEC, client.hideSalesAmountAfterInSec)
            session.setAttribute(AppConstant.SESSION_ATTRIBUTE.SYNC_SALES_DATAINMIN, client.syncSalesDataInMin)
            session.setAttribute(AppConstant.SESSION_ATTRIBUTE.REPORT_PRINT_LIMIT, client.reportPrintLimit)
            session.setAttribute(AppConstant.SESSION_ATTRIBUTE.TENANT_ID, client.tenantId.toLowerCase())

            session.setAttribute(SessionTenantResolver.ATTRIBUTE, client.tenantId.toLowerCase())
            return true
        } else {
            return false
        }
    }

    boolean after() {true }
}
