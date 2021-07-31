package com.bitsoft.st

import java.text.DecimalFormat

class AppUtilService {

    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00")

    def printError(def domain){
        domain.getErrors().each {
            log.error(it.getFieldError().toString())
        }
    }
}
