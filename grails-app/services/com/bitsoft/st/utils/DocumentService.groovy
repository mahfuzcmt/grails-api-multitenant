package com.bitsoft.st.utils

import com.bitsoft.st.AppUtilService
import com.bitsoft.st.KounterService
import com.bitsoft.st.Sales
import com.bitsoft.st.utils.JavaShell.ShellRunner
import grails.converters.JSON
import grails.util.Environment
import javax.activation.MimetypesFileTypeMap

class DocumentService {

     AppUtilService appUtilService
     KounterService kounterService

     static String rootPathOfProd =  AppConstant.rootPathOfProd

    Map getMacrosAndTemplateByIdentifier(String identifier) {
        File macroFile
        if (Environment.current.equals(Environment.PRODUCTION)) {
            macroFile = new File("${rootPathOfProd}micros${File.separator}" + identifier + "${File.separator}macro.json")
        }else{
            macroFile = new File("micros${File.separator}" + identifier + "${File.separator}macro.json")
        }
        Properties macros = new Properties()
        JSON.parse(macroFile.text).each {
            macros.setProperty(it.key, "")
        }

        StringBuilder contentBuilder = new StringBuilder()
        String template
        try {
            BufferedReader input
            if (Environment.current.equals(Environment.PRODUCTION)) {
                input = new BufferedReader(new FileReader("/var/lib/tomcat8/webapps/micros/sales-summary/default.html"))
            }else{
                input = new BufferedReader(new FileReader("micros${File.separator}" + identifier + "${File.separator}default.html"))
            }

            while ((template = input.readLine()) != null) {
                contentBuilder.append(template)
            }
            input.close()
        } catch (IOException e) {
            println(e.message)
        }
        String content = contentBuilder.toString()
        return [macros: macros, template: content]
    }

    def prepareReport(String identifier, Map data = [:]) {
        Sales.withNewSession {
            Map refinedMacros = getMacrosAndTemplateByIdentifier(identifier)

            Map saleDetails = [:]
            saleDetails.items = data.summary
            refinedMacros.macros["order_details"] = saleDetails
            refinedMacros.macros["company_name"] = AppUtil.session[AppConstant.SESSION_ATTRIBUTE.COMPANY_NAME]
            refinedMacros.macros["address"] = AppUtil.session[AppConstant.SESSION_ATTRIBUTE.COMPANY_ADDRESS]


            refinedMacros.macros["dateFrom"] = data.dateFrom
            refinedMacros.macros["dateTo"] = data.dateTo

            refinedMacros.macros["totalSales"] = data.totalSales
            refinedMacros.macros["totalRefund"] = data.totalRefund
            refinedMacros.macros["netSales"] = data.netSales

            refinedMacros.macros["printedBy"] = data.printedBy
            refinedMacros.macros["printedDate"] = new Date().format("yyyy-MM-dd hh:m:ss a")

            Map macros = new LinkedHashMap(refinedMacros.macros)

            return getPdfData("report", macros, refinedMacros.template)

        }
    }

    def getPdfData(String attachmentName, Map macros = [:], String htmlContent, Boolean testMode = false) {
        Map attachment = [:]

        String path = new File("pdfs").absolutePath
        if (Environment.current.equals(Environment.PRODUCTION)) {
             path = new File("${rootPathOfProd}pdfs")
        }
        File html = null, pdf = null
        try {
            File exPdf = new File(path + attachmentName + ".pdf")
            File exHtml = new File(path + attachmentName + ".html")
            if(exHtml.exists()){
                exHtml.delete()
            }
            if(exPdf.exists()){
                exPdf.delete()
            }

            html = new File(path + File.separator + attachmentName + ".html")
            pdf = new File(path + File.separator +  attachmentName + ".pdf")

            String resultHtml = EmailTemplateParser.parse(htmlContent, macros, new StringBuilder(), true)
            html << resultHtml

            String exePath = "C:\\Program Files\\wkhtmltopdf\\bin\\wkhtmltopdf.exe"

            if (Environment.current.equals(Environment.PRODUCTION)) {
                exePath = "/usr/local/bin/wkhtmltopdf"
            }
            String cmd = "${exePath} ${html.path} ${pdf.path}"

            ShellRunner.exeCuteCommand(cmd)

            if(pdf.exists()) {
                attachment.name = pdf.name
                attachment.contentType = new MimetypesFileTypeMap().getContentType(pdf)
                attachment.byte = pdf.getBytes()
            }
        } catch (Exception ex) {
            ex.printStackTrace()
        } finally {
            html.delete()
            pdf.delete()
        }
        return attachment
    }
}