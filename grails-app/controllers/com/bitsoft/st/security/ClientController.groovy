package com.bitsoft.st.security

import com.bitsoft.st.Client
import grails.converters.JSON

class ClientController {

    ClientService clientService

    def createClient() {
        Map params = request.JSON
        Client client = clientService.createClient(params)
        if (client) {
            render([status: "success", client: client] as JSON)
        } else {
            render([status: "warning", message: "Sorry!"] as JSON)
        }
    }

    def getAvailableEntity() {
        Map params = request.JSON
        Client client = clientService.getAvailableEntity()
        if (client) {
            render([status: "success", client: client] as JSON)
        } else {
            render([status: "warning", message: "Sorry!"] as JSON)
        }
    }
}