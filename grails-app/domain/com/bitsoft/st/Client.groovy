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
    String subscriptionPackage
    String status = AppConstant.STATUS.ACTIVE

    Integer syncLocInMin = 15
    Integer userLimit = 50

    Date subscriptionStartDate
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
        subscriptionPackage(nullable: true)
        subscriptionStartDate(nullable: true)
    }

    def beforeUpdate = {
        this.updated = new Date()
    }

    def beforeValidate() {
        this.created = this.created ?: new Date()
        this.updated = this.updated ?: new Date()
    }
}
