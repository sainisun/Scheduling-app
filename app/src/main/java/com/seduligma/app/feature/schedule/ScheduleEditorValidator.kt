package com.seduligma.app.feature.schedule

import java.time.Instant
import java.time.ZoneId

enum class ScheduleEditorError(val userMessage: String) {
    TITLE_REQUIRED("Add a schedule title."),
    TITLE_TOO_LONG("Keep the schedule title to 80 characters or fewer."),
    MESSAGE_REQUIRED("Add a message preview."),
    MESSAGE_TOO_LONG("Keep the message preview to 4,000 characters or fewer."),
    TIMEZONE_INVALID("Enter a valid IANA timezone, for example Asia/Kolkata."),
    TIME_MUST_BE_FUTURE("Choose a future date and time."),
}

data class ScheduleEditorInput(
    val title: String,
    val messagePreview: String,
    val scheduledAt: Instant,
    val timezoneId: String,
)

object ScheduleEditorValidator {
    const val MAX_TITLE_LENGTH = 80
    const val MAX_MESSAGE_LENGTH = 4_000

    fun validate(input: ScheduleEditorInput, now: Instant): ScheduleEditorError? = when {
        input.title.trim().isEmpty() -> ScheduleEditorError.TITLE_REQUIRED
        input.title.trim().length > MAX_TITLE_LENGTH -> ScheduleEditorError.TITLE_TOO_LONG
        input.messagePreview.trim().isEmpty() -> ScheduleEditorError.MESSAGE_REQUIRED
        input.messagePreview.trim().length > MAX_MESSAGE_LENGTH -> ScheduleEditorError.MESSAGE_TOO_LONG
        !isValidTimezone(input.timezoneId) -> ScheduleEditorError.TIMEZONE_INVALID
        input.scheduledAt <= now -> ScheduleEditorError.TIME_MUST_BE_FUTURE
        else -> null
    }

    fun isValidTimezone(timezoneId: String): Boolean = runCatching {
        ZoneId.of(timezoneId.trim())
    }.isSuccess
}
