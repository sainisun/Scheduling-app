package com.seduligma.app.domain.scheduling

import com.seduligma.app.domain.model.Schedule

enum class AlarmRegistrationResult {
    REGISTERED,
    EXACT_ALARM_PERMISSION_REQUIRED,
}

interface ScheduleAlarmRegistrar {
    fun register(schedule: Schedule): AlarmRegistrationResult

    fun cancel(scheduleId: String)
}
