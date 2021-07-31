package com.bitsoft.st

import com.bitsoft.st.utils.AppConstant

class Client {

    Long id
    String name
    String tenantId
    String contactNo
    String address
    String description
    String complainText
    String creditText
    String ticketDateFormat = "dd-MM-yyyy hh:mm:ss aa"
    String status = AppConstant.STATUS.ACTIVE
    String category = AppConstant.CLIENT_CATEGORY.BUS

    Integer hideSalesAmountAfterInSec = 10
    Integer syncSalesDataInMin = 15
    Integer reportPrintLimit = 6

    Date created
    Date updated

    static constraints = {
        name(nullable: true)
        tenantId (nullable: true)
        contactNo(nullable: true)
        address(nullable: true)
        description(nullable: true)
        complainText(nullable: true)
        creditText(nullable: true)
        status(nullable: true)
    }

    def beforeUpdate = {
        this.updated = new Date()
    }

    def beforeValidate() {
        this.created = this.created ?: new Date()
        this.updated = this.updated ?: new Date()
    }
}
