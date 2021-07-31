package com.bitsoft.st.utils

import com.agileorbit.schwartz.SchwartzJob
import com.bitsoft.st.Client
import grails.util.Environment
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.mortbay.log.Log
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException

import static org.quartz.DateBuilder.todayAt
import static org.quartz.DateBuilder.tomorrowAt

@Slf4j
@CompileStatic
class SalesCollectionJobService implements SchwartzJob {

    final int HOUR = 00
    final int MINUTE = 00
    final int SECONDS = 01


    void execute(JobExecutionContext context) throws JobExecutionException {
        Client.createCriteria().list {
            eq("status", "Active")
        }.each { Client client ->
            if(!client.tenantId.equals("iqbal-ent")){
                try {
                    String baseUrl = "http://localhost:8080"
                    if(Environment.current.equals(Environment.PRODUCTION)){
                        baseUrl = AppConstant.getProdBaseUrl()
                    }
                    String url = "${baseUrl}/collection/autoCollect?tenantId=${client.tenantId}"
                    String response = url.toURL().text
                    Log.debug(response)
                } catch (Exception e) {
                    log.error(e.message)
                }
            }
        }
    }

    Date dailyDate() {
        Date startAt = todayAt(HOUR, MINUTE, SECONDS)
        if (startAt.before(new Date())) {
            return tomorrowAt(HOUR, MINUTE, SECONDS)
        }
        startAt
    }

    void buildTriggers() {
        Date startAt = dailyDate()
        triggers << factory('Daily email job')
                .startAt(startAt)
                .intervalInDays(1)
                .build()

    }
}
