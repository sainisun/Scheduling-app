package com.seduligma.app.domain.scheduling

import com.seduligma.app.domain.model.RecurrenceRule
import java.time.Instant
import java.time.ZoneId

object RecurrenceCalculator {
    /**
     * Returns the next occurrence strictly after [after]. Monthly schedules preserve their
     * original day-of-month where possible and use the target month's last valid day otherwise.
     */
    fun nextOccurrenceAfter(
        initialOccurrence: Instant,
        timezoneId: String,
        recurrence: RecurrenceRule,
        after: Instant,
    ): Instant? {
        if (recurrence == RecurrenceRule.ONCE) return null

        val zone = ZoneId.of(timezoneId)
        val initialLocal = initialOccurrence.atZone(zone)
        var interval = 1L

        while (true) {
            val candidate = when (recurrence) {
                RecurrenceRule.DAILY -> initialLocal.plusDays(interval)
                RecurrenceRule.WEEKLY -> initialLocal.plusWeeks(interval)
                RecurrenceRule.MONTHLY -> initialLocal.plusMonths(interval)
                RecurrenceRule.ONCE -> error("Handled above")
            }.toInstant()

            if (candidate > after) return candidate
            interval += 1
        }
    }
}
