package com.seduligma.app.domain.model

import java.time.Instant

enum class ScheduleEventType {
    CREATED,
    ACTIVATED,
    PAUSED,
    CANCELLED,
    ATTEMPTED,
    OUTCOME_RECORDED,
}

enum class LocalEvidence {
    NONE,
    ALARM_REGISTERED,
    NOTIFICATION_POSTED,
    USER_CONFIRMED,
}

enum class ScheduleReasonCode {
    EXACT_ALARM_DENIED,
    NOTIFICATION_DENIED,
    USER_CANCELLED_CONFIRMATION,
    ALARM_MISSED_AFTER_REBOOT,
    CONCURRENT_EXECUTION_BLOCKED,
}

data class ScheduleEvent(
    val id: String,
    val scheduleId: String,
    val eventType: ScheduleEventType,
    val localEvidence: LocalEvidence,
    val reasonCode: ScheduleReasonCode? = null,
    val occurredAt: Instant,
)
