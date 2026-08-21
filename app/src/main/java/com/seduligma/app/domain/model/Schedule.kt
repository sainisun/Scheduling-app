package com.seduligma.app.domain.model

import java.time.Instant

enum class ScheduleState {
    DRAFT,
    NEEDS_PERMISSION,
    READY,
    WAITING,
    AWAITING_USER,
    ATTEMPTING,
    COMPLETED,
    FAILED,
    UNCERTAIN,
    BLOCKED,
    PAUSED,
    CANCELLED,
}

enum class RecurrenceRule { ONCE, DAILY, WEEKLY, MONTHLY }

data class Schedule(
    val id: String,
    val title: String,
    val messagePreview: String,
    val scheduledAt: Instant,
    val timezoneId: String,
    val recurrence: RecurrenceRule,
    val state: ScheduleState,
)
