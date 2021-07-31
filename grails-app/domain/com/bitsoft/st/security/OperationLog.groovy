package com.bitsoft.st.security

import com.bitsoft.st.Role
import com.bitsoft.st.User
import com.bitsoft.st.utils.AppConstant
import grails.gorm.MultiTenant


class OperationLog implements MultiTenant<OperationLog> {

    Long id
    User user
    String address
    String deviceMac
    String operationType

    String latitude
    String longitude

    Date created

    static constraints = {
        user(nullable: true, blank: true)
        address(nullable: true, blank: true)
        deviceMac(nullable: true, blank: true)
    }

    static mapping = {
        user lazy:false
    }

    def beforeValidate() {
        this.created = this.created ?: new Date()
    }
}