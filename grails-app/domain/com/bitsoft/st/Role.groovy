package com.bitsoft.st

import grails.gorm.MultiTenant


class Role implements MultiTenant<Role> {

    Long id
    String name
    String menuJson
    String status

    Date created
    Date updated


    Collection<User> users = []

    static hasMany = [users: User]

    static constraints = {
        menuJson column: "menuJson", sqlType: "varchar(5000)"
    }

    def beforeUpdate = {
        this.updated = new Date()
    }

    def beforeValidate() {
        this.created = this.created ?: new Date()
        this.updated = this.updated ?: new Date()
        this.status = this.status ?: "Active"
    }
}