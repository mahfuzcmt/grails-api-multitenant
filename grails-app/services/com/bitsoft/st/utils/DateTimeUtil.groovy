package com.bitsoft.st.utils

import java.text.SimpleDateFormat
import java.time.Instant

class DateTimeUtil {

    public static String TICKET_DATE_TIME_FORMAT = "dd-MM-yyyy hh:mm:ss aa"
    public static String INPUT_DATE_FORMAT= "yyyy-MM-dd HH:mm:ss"
    public static String REPORT_INPUT_FORMAT = "yyyy-MM-dd HH:mm:ss"

    def static startOfTheDay(Date startDate = new Date()) {
        try {
            return new SimpleDateFormat(INPUT_DATE_FORMAT).parse(startDate.format("yyyy-MM-dd" + " 00:00:00"))
        } catch (Exception e) {
            return null
        }
    }

    def static endOfTheDay(Date endDate = new Date()) {
        try {
            return new SimpleDateFormat(INPUT_DATE_FORMAT).parse(endDate.format("yyyy-MM-dd" + " 23:59:59"))
        } catch (Exception e) {
            return null
        }
    }

    def static getFormattedDate(Date date, String format = TICKET_DATE_TIME_FORMAT) {
        try {
            return date.format(format)
        } catch (Exception e) {
            return null
        }
    }

    def static getDateFromString(String date, String format = INPUT_DATE_FORMAT) {
        try {
            return new SimpleDateFormat(format).parse(date)
        } catch (Exception e) {
            return null
        }
    }
    def static getDateFromStringForReport(String date) {
        try {
            return getDateFromString(date, REPORT_INPUT_FORMAT)
        } catch (Exception e) {
            return null
        }
    }


}
