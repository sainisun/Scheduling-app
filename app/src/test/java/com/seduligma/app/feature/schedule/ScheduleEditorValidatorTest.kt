package com.seduligma.app.feature.schedule

import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ScheduleEditorValidatorTest {
    private val future = Instant.parse("2030-01-01T10:00:00Z")
    private val now = Instant.parse("2030-01-01T09:00:00Z")

    @Test
    fun `valid editor input is accepted`() {
        val error = ScheduleEditorValidator.validate(
            ScheduleEditorInput(
                title = "Follow up",
                messagePreview = "Hello from a local draft",
                scheduledAt = future,
                timezoneId = "Asia/Kolkata",
            ),
            now,
        )

        assertNull(error)
    }

    @Test
    fun `validator rejects empty title before other input`() {
        val error = ScheduleEditorValidator.validate(
            ScheduleEditorInput("  ", "Preview", future, "UTC"),
            now,
        )

        assertEquals(ScheduleEditorError.TITLE_REQUIRED, error)
    }

    @Test
    fun `validator rejects invalid IANA timezone`() {
        val error = ScheduleEditorValidator.validate(
            ScheduleEditorInput("Title", "Preview", future, "Not/AZone"),
            now,
        )

        assertEquals(ScheduleEditorError.TIMEZONE_INVALID, error)
    }

    @Test
    fun `validator rejects past execution time`() {
        val error = ScheduleEditorValidator.validate(
            ScheduleEditorInput("Title", "Preview", now, "UTC"),
            now,
        )

        assertEquals(ScheduleEditorError.TIME_MUST_BE_FUTURE, error)
    }

    @Test
    fun `validator enforces title and preview limits`() {
        val titleError = ScheduleEditorValidator.validate(
            ScheduleEditorInput("a".repeat(ScheduleEditorValidator.MAX_TITLE_LENGTH + 1), "Preview", future, "UTC"),
            now,
        )
        val messageError = ScheduleEditorValidator.validate(
            ScheduleEditorInput("Title", "a".repeat(ScheduleEditorValidator.MAX_MESSAGE_LENGTH + 1), future, "UTC"),
            now,
        )

        assertEquals(ScheduleEditorError.TITLE_TOO_LONG, titleError)
        assertEquals(ScheduleEditorError.MESSAGE_TOO_LONG, messageError)
    }
}
