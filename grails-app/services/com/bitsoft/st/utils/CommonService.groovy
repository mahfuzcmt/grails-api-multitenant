package com.bitsoft.st.utils

import grails.gorm.multitenancy.CurrentTenant

@CurrentTenant
class CommonService {

    String getShift(Date date) {
        String shift = AppConstant.SHIFT.MORNING
        if (date.format("HH.mm").toDouble() >= 14.00) {
            shift = AppConstant.SHIFT.EVENGING
        }
        return shift
    }

}
