package com.seduligma.app.feature.schedule

import com.seduligma.app.domain.model.RecurrenceRule
import com.seduligma.app.domain.model.Schedule
import com.seduligma.app.domain.model.ScheduleState
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class ScheduleListFiltersTest {
    private val schedules = listOf(
        schedule("1", "Paused check-in", "One", "2030-01-03T09:00:00Z", ScheduleState.PAUSED),
        schedule("2", "Send report", "Two", "2030-01-01T09:00:00Z", ScheduleState.WAITING),
        schedule("3", "Review issue", "Three", "2030-01-02T09:00:00Z", ScheduleState.UNCERTAIN),
        schedule("4", "Completed note", "Four", "2030-01-04T09:00:00Z", ScheduleState.COMPLETED),
    )

    @Test
    fun `upcoming filter returns active local schedule states in time order`() {
        val result = ScheduleListFilters.apply(schedules, "", ScheduleListFilter.UPCOMING)

        assertEquals(listOf("2"), result.map(Schedule::id))
    }

    @Test
    fun `action-required filter includes uncertain schedules`() {
        val result = ScheduleListFilters.apply(schedules, "", ScheduleListFilter.ACTION_REQUIRED)

        assertEquals(listOf("3"), result.map(Schedule::id))
    }

    @Test
    fun `search matches title and sorts results by scheduled time`() {
        val result = ScheduleListFilters.apply(schedules, "report", ScheduleListFilter.ALL)

        assertEquals(listOf("2"), result.map(Schedule::id))
    }

    @Test
    fun `all filter sorts independently of repository insertion order`() {
        val result = ScheduleListFilters.apply(schedules.reversed(), "", ScheduleListFilter.ALL)

        assertEquals(listOf("2", "3", "1", "4"), result.map(Schedule::id))
    }

    private fun schedule(
        id: String,
        title: String,
        preview: String,
        at: String,
        state: ScheduleState,
    ) = Schedule(
        id = id,
        title = title,
        messagePreview = preview,
        scheduledAt = Instant.parse(at),
        timezoneId = "UTC",
        recurrence = RecurrenceRule.ONCE,
        state = state,
    )
}
