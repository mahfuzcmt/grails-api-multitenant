package com.bitsoft.st

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?" {
            constraints {
                // apply constraints here
            }
        }

        //public API
        '/login'(controller: 'security', action: 'login')
        '/open'(controller: 'opening', action: 'open')

        "/"(controller: 'application', action:'index')
        "500"(view: '/error')
        "404"(view: '/notFound')
    }


    static String toCamelCase( String text, boolean capitalized = false ) {
        text = text.replaceAll( "(_)([A-Za-z0-9])", { Object[] it -> it[2].toUpperCase() } )
        return capitalized ? capitalize(text) : text
    }
}
