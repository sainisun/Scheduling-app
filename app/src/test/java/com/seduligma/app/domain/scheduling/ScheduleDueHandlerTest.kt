package com.seduligma.app.domain.scheduling

import com.seduligma.app.domain.model.Schedule
import com.seduligma.app.domain.model.ScheduleEvent
import com.seduligma.app.domain.model.ScheduleState
import com.seduligma.app.domain.notification.ManualConfirmationNotificationResult
import com.seduligma.app.domain.notification.ManualConfirmationNotifier
import com.seduligma.app.domain.repository.ScheduleEventRepository
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
        val eventRepository = RecordingScheduleEventRepository()
        val notifier = RecordingManualConfirmationNotifier()
        val handler = ScheduleDueHandler(repository, eventRepository, notifier)

        handler.handleDueSchedule("schedule-1")

        assertEquals(listOf("schedule-1" to ScheduleState.AWAITING_USER), repository.updatedStates)
        assertEquals(listOf("schedule-1"), notifier.notifiedScheduleIds)
        assertEquals(1, eventRepository.recordedEvents.size)
        assertEquals(com.seduligma.app.domain.model.LocalEvidence.NOTIFICATION_POSTED, eventRepository.recordedEvents.single().localEvidence)
    }

    @Test
    fun `notification denial is truthful needs-permission evidence`() = runTest {
        val repository = RecordingScheduleRepository()
        val eventRepository = RecordingScheduleEventRepository()
        val notifier = RecordingManualConfirmationNotifier(ManualConfirmationNotificationResult.NOTIFICATIONS_DISABLED)
        val handler = ScheduleDueHandler(repository, eventRepository, notifier)

        handler.handleDueSchedule("schedule-2")

        assertEquals(listOf("schedule-2" to ScheduleState.NEEDS_PERMISSION), repository.updatedStates)
        assertEquals(com.seduligma.app.domain.model.ScheduleReasonCode.NOTIFICATION_DENIED, eventRepository.recordedEvents.single().reasonCode)
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

private class RecordingManualConfirmationNotifier(
    private val result: ManualConfirmationNotificationResult = ManualConfirmationNotificationResult.POSTED,
) : ManualConfirmationNotifier {
    val notifiedScheduleIds = mutableListOf<String>()
    override fun showScheduleReady(scheduleId: String): ManualConfirmationNotificationResult {
        notifiedScheduleIds += scheduleId
        return result
    }
}

private class RecordingScheduleEventRepository : ScheduleEventRepository {
    val recordedEvents = mutableListOf<ScheduleEvent>()
    override fun observeEvents(): Flow<List<ScheduleEvent>> = emptyFlow()
    override suspend fun recordEvent(event: ScheduleEvent) {
        recordedEvents += event
    }
}
