package com.seduligma.app.domain.scheduling

import com.seduligma.app.domain.model.RecurrenceRule
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RecurrenceCalculatorTest {
    @Test
    fun `one-time schedule has no next occurrence`() {
        val initial = Instant.parse("2026-01-01T09:00:00Z")

        assertNull(
            RecurrenceCalculator.nextOccurrenceAfter(
                initialOccurrence = initial,
                timezoneId = "UTC",
                recurrence = RecurrenceRule.ONCE,
                after = initial,
            ),
        )
    }

    @Test
    fun `daily schedule returns first occurrence strictly after reference time`() {
        val initial = Instant.parse("2026-01-01T09:00:00Z")

        assertEquals(
            Instant.parse("2026-01-03T09:00:00Z"),
            RecurrenceCalculator.nextOccurrenceAfter(
                initialOccurrence = initial,
                timezoneId = "UTC",
                recurrence = RecurrenceRule.DAILY,
                after = Instant.parse("2026-01-02T12:00:00Z"),
            ),
        )
    }

    @Test
    fun `monthly schedule uses last valid day when original day is unavailable`() {
        val initial = Instant.parse("2026-01-31T10:00:00Z")

        assertEquals(
            Instant.parse("2026-02-28T10:00:00Z"),
            RecurrenceCalculator.nextOccurrenceAfter(
                initialOccurrence = initial,
                timezoneId = "UTC",
                recurrence = RecurrenceRule.MONTHLY,
                after = Instant.parse("2026-02-01T00:00:00Z"),
            ),
        )
    }
}
