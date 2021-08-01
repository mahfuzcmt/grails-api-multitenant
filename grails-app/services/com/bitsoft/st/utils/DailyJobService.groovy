package com.bitsoft.st.utils

import com.agileorbit.schwartz.SchwartzJob
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException

import static org.quartz.DateBuilder.todayAt
import static org.quartz.DateBuilder.tomorrowAt

@Slf4j
@CompileStatic
class DailyJobService implements SchwartzJob {

    final int HOUR = 00
    final int MINUTE = 00
    final int SECONDS = 01


    void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("=========Trigger at 12:00:01================")
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
