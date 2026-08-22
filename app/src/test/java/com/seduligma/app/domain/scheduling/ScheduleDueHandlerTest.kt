package com.seduligma.app.domain.scheduling

import com.seduligma.app.domain.model.Schedule
import com.seduligma.app.domain.model.ScheduleState
import com.seduligma.app.domain.notification.ManualConfirmationNotifier
import com.seduligma.app.domain.repository.ScheduleRepository
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ScheduleDueHandlerTest {
    @Test
    fun `due schedule awaits user review before local notification is shown`() = runTest {
        val repository = RecordingScheduleRepository()
        val notifier = RecordingManualConfirmationNotifier()
        val handler = ScheduleDueHandler(repository, notifier)

        handler.handleDueSchedule("schedule-1")

        assertEquals(listOf("schedule-1" to ScheduleState.AWAITING_USER), repository.updatedStates)
        assertEquals(listOf("schedule-1"), notifier.notifiedScheduleIds)
    }
}

private class RecordingScheduleRepository : ScheduleRepository {
    val updatedStates = mutableListOf<Pair<String, ScheduleState>>()

    override fun observeSchedules(): Flow<List<Schedule>> = emptyFlow()
    override suspend fun getSchedulesByStates(states: Set<ScheduleState>): List<Schedule> = emptyList()
    override suspend fun createDraft(schedule: Schedule) = Unit
    override suspend fun updateSchedule(schedule: Schedule) = Unit
    override suspend fun updateState(scheduleId: String, state: ScheduleState) {
        updatedStates += scheduleId to state
    }
}

private class RecordingManualConfirmationNotifier : ManualConfirmationNotifier {
    val notifiedScheduleIds = mutableListOf<String>()
    override fun showScheduleReady(scheduleId: String) {
        notifiedScheduleIds += scheduleId
    }
}
