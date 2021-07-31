package com.bitsoft.st

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import grails.gorm.transactions.Transactional
import org.springframework.boot.ApplicationArguments
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}