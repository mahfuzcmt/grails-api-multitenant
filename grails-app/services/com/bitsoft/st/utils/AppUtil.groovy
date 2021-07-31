package com.bitsoft.st.utils


import grails.gorm.multitenancy.CurrentTenant
import grails.util.Holders
import grails.web.mvc.FlashScope
import grails.web.servlet.mvc.GrailsHttpSession
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@CurrentTenant
class AppUtil {
    private static final ThreadLocal<GrailsWebRequest> requestCache = new ThreadLocal<GrailsWebRequest>()

    static def getConfig(String type, String key = null) {
        return getConfig(type, key, false)
    }

    private static GrailsWebRequest getCurrentRequest() {
        GrailsWebRequest request = requestCache.get()
        if(!request) {
            request = RequestContextHolder.currentRequestAttributes()
            if(!request) {
                requestCache.set(request)
            }
        }
        return request
    }

    static GrailsParameterMap getParams() {
        try {
            currentRequest.params
        } catch (Throwable t) {
            return null
        }
    }

    static GrailsHttpSession getSession() {
        try {
            currentRequest.session
        } catch (Throwable t) {
            return null
        }
    }

    static HttpServletRequest getRequest() {
        try {
            currentRequest.request
        } catch (Throwable t) {
            return null
        }
    }

    static HttpServletResponse getResponse() {
        try {
            currentRequest.response
        } catch (Throwable t) {
            return null
        }
    }

    static FlashScope getFlash() {
        try {
            currentRequest.flashScope
        } catch (Throwable t) {
            return null
        }
    }

    static Locale getLocale() {
        try {
            currentRequest.locale
        } catch (Throwable t) {
            return Locale.default
        }
    }

    static def getBean(String beanIdentifier) {
        try {
            return Holders.grailsApplication.mainContext.getBean(beanIdentifier)
        } catch (Exception e) {
            return null
        }
    }

    static <T> T getBean(Class<T> requiredType) {
        try {
            return Holders.grailsApplication.mainContext.getBean(requiredType)
        } catch (Exception e) {
            return null
        }
    }

}
