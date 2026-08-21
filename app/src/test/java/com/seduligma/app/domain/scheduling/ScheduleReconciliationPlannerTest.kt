package com.seduligma.app.domain.scheduling

import com.seduligma.app.domain.model.RecurrenceRule
import com.seduligma.app.domain.model.Schedule
import com.seduligma.app.domain.model.ScheduleState
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class ScheduleReconciliationPlannerTest {
    @Test
    fun `future schedules are re-registered and overdue schedules become uncertain`() {
        val now = Instant.parse("2026-06-01T10:00:00Z")
        val overdue = schedule(id = "overdue", at = Instant.parse("2026-06-01T09:00:00Z"))
        val future = schedule(id = "future", at = Instant.parse("2026-06-01T11:00:00Z"))

        val plan = ScheduleReconciliationPlanner.plan(listOf(overdue, future), now)

        assertEquals(listOf("future"), plan.schedulesToRegister.map(Schedule::id))
        assertEquals(listOf("overdue"), plan.scheduleIdsToMarkUncertain)
    }

    private fun schedule(id: String, at: Instant) = Schedule(
        id = id,
        title = id,
        messagePreview = id,
        scheduledAt = at,
        timezoneId = "UTC",
        recurrence = RecurrenceRule.ONCE,
        state = ScheduleState.WAITING,
    )
}
