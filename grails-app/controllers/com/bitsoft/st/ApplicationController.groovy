package com.bitsoft.st

import grails.converters.JSON
import grails.core.GrailsApplication
import grails.plugins.*

class ApplicationController implements PluginManagerAware {

    GrailsApplication grailsApplication
    GrailsPluginManager pluginManager

    def index() {
        render([message: "Unauthorized Access!"] as JSON)
    }
}
