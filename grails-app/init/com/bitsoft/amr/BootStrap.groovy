package com.bitsoft.st

import com.bitsoft.st.utils.AppConstant

class BootStrap {


    def grailsApplication

    def init = { servletContext ->
       new AppConstant(grailsApplication)
    }

    def destroy = {
    }
}
