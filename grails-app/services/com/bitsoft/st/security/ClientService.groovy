package com.bitsoft.st.security

import com.bitsoft.st.Client
import com.bitsoft.st.utils.AppConstant

import java.time.Instant

class ClientService {

    Client getClientById(Long id) {
        return Client.get(id)
    }

    Client getAvailableEntity() {
        List<Client> clientList = Client.createCriteria().list {
            isNull("name")
        }
        if (clientList) {
            return clientList.first()
        }
        return null
    }

    Client createClient(params) {
        Client client = params.id ? getClientById(params.id.toLong()) : new Client()

        client.name = params.name
        client.tenantId = params.tenantId
        client.contactNo = params.contactNo
        client.status = params.status ?: AppConstant.STATUS.ACTIVE
        client.subscriptionPackage = params.subscriptionPackage ?: "Standard"
        client.subscriptionStartDate = params.subscriptionStartDate ?: new Date()
        client.save()
        if (!client.hasErrors()) {
            return client
        }
        return null
    }

}
