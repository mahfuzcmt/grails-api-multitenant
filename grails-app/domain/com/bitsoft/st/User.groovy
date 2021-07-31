package com.bitsoft.st

import com.bitsoft.st.utils.AppConstant
import grails.gorm.MultiTenant


class User implements MultiTenant<User> {

    Long id
    String fullName
    String userName
    String password
    String contactNo
    String deviceMac
    String shift
    String status = AppConstant.STATUS.ACTIVE

    Boolean isWayBillFeatureEnabled = false

    Long respectiveCounterId = 0

    Date created
    Date updated

    Role role

    static constraints = {
        contactNo(nullable: false, unique: true, blank: false)
        userName(nullable: false, unique: true, blank: false)
        role(nullable: true, blank: true)
        respectiveCounterId(nullable: true, blank: true)
        deviceMac(nullable: true, blank: true)
        shift(nullable: true, blank: true)
    }

    def beforeUpdate = {
        this.updated = new Date()
    }

    def beforeValidate() {
        this.created = this.created ?: new Date()
        this.updated = this.updated ?: new Date()
    }
}