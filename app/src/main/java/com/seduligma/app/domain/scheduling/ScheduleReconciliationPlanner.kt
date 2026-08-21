package com.seduligma.app.domain.scheduling

import com.seduligma.app.domain.model.Schedule
import java.time.Instant

data class ScheduleReconciliationPlan(
    val schedulesToRegister: List<Schedule>,
    val scheduleIdsToMarkUncertain: List<String>,
)

object ScheduleReconciliationPlanner {
    fun plan(schedules: List<Schedule>, now: Instant): ScheduleReconciliationPlan =
        ScheduleReconciliationPlan(
            schedulesToRegister = schedules.filter { it.scheduledAt > now },
            scheduleIdsToMarkUncertain = schedules.filter { it.scheduledAt <= now }.map(Schedule::id),
        )
}
