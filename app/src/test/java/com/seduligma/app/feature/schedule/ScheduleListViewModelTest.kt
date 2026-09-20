package com.seduligma.app.feature.schedule

import com.seduligma.app.domain.device.DeviceHealthEvaluator
import com.seduligma.app.domain.device.DeviceHealthReport
import com.seduligma.app.domain.device.HealthState
import com.seduligma.app.domain.model.RecurrenceRule
import com.seduligma.app.domain.model.LocalEvidence
import com.seduligma.app.domain.model.Schedule
import com.seduligma.app.domain.model.ScheduleEvent
import com.seduligma.app.domain.model.ScheduleEventType
import com.seduligma.app.domain.model.ScheduleReasonCode
import com.seduligma.app.domain.model.ScheduleState
import com.seduligma.app.domain.privacy.LocalDataResetter
import com.seduligma.app.domain.repository.ScheduleEventRepository
import com.seduligma.app.domain.repository.ScheduleRepository
import com.seduligma.app.domain.scheduling.AlarmRegistrationResult
import com.seduligma.app.domain.scheduling.ScheduleAlarmRegistrar
import com.seduligma.app.testing.MainDispatcherRule
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ScheduleListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `creating a draft delegates to repository`() = runTest {
        val repository = FakeScheduleRepository()
        val events = FakeScheduleEventRepository()
        val viewModel = viewModel(repository, events)

        viewModel.createDraft()

        advanceUntilIdle()
        assertEquals(1, repository.createdSchedules.size)
        assertEquals(ScheduleState.DRAFT, repository.createdSchedules.single().state)
        assertEquals(ScheduleEventType.CREATED, events.events.value.single().eventType)
        assertEquals(LocalEvidence.NONE, events.events.value.single().localEvidence)
    }

    @Test
    fun `pause and cancel delegate explicit schedule states`() = runTest {
        val repository = FakeScheduleRepository()
        val viewModel = viewModel(repository)
        val schedule = schedule(id = "schedule-1", state = ScheduleState.WAITING)
        repository.createDraft(schedule)

        viewModel.pauseSchedule(schedule.id)
        advanceUntilIdle()
        assertEquals(ScheduleState.PAUSED, repository.schedules.value.single().state)

        viewModel.cancelSchedule(schedule.id)
        advanceUntilIdle()
        assertEquals(ScheduleState.CANCELLED, repository.schedules.value.single().state)
    }

    @Test
    fun `activating schedule reflects exact alarm permission result`() = runTest {
        val repository = FakeScheduleRepository()
        val events = FakeScheduleEventRepository()
        val viewModel = viewModel(
            repository,
            events,
            alarmRegistrar = FakeScheduleAlarmRegistrar(AlarmRegistrationResult.EXACT_ALARM_PERMISSION_REQUIRED),
        )
        val schedule = schedule(id = "schedule-2", state = ScheduleState.DRAFT)
        repository.createDraft(schedule)

        viewModel.activateSchedule(schedule)
        advanceUntilIdle()

        assertEquals(ScheduleState.NEEDS_PERMISSION, repository.schedules.value.single().state)
        assertEquals(ScheduleReasonCode.EXACT_ALARM_DENIED, events.events.value.single().reasonCode)
    }

    @Test
    fun `erasing local data delegates only to the explicit privacy resetter`() = runTest {
        val repository = FakeScheduleRepository()
        val resetter = FakeLocalDataResetter()
        val viewModel = viewModel(repository, localDataResetter = resetter)

        viewModel.eraseLocalData()

        advanceUntilIdle()
        assertEquals(1, resetter.eraseRequests)
    }

    @Test
    fun `confirming one-time schedule records user evidence and completes`() = runTest {
        val repository = FakeScheduleRepository()
        val events = FakeScheduleEventRepository()
        val viewModel = viewModel(repository, events)
        val schedule = schedule(id = "schedule-3", state = ScheduleState.ATTEMPTING)
        repository.createDraft(schedule)

        viewModel.confirmSchedule(schedule)
        advanceUntilIdle()

        assertEquals(ScheduleState.COMPLETED, repository.schedules.value.single().state)
        assertEquals(LocalEvidence.USER_CONFIRMED, events.events.value.single().localEvidence)
    }

    private fun viewModel(
        repository: FakeScheduleRepository,
        eventRepository: FakeScheduleEventRepository = FakeScheduleEventRepository(),
        alarmRegistrar: FakeScheduleAlarmRegistrar = FakeScheduleAlarmRegistrar(),
        localDataResetter: FakeLocalDataResetter = FakeLocalDataResetter(),
    ) = ScheduleListViewModel(
        repository,
        eventRepository,
        localDataResetter,
        alarmRegistrar,
        FakeDeviceHealthEvaluator(),
    )

    private fun schedule(id: String, state: ScheduleState) = Schedule(
        id = id,
        title = "Follow up",
        messagePreview = "Test",
        scheduledAt = Instant.now().plusSeconds(3_600),
        timezoneId = "UTC",
        recurrence = RecurrenceRule.ONCE,
        state = state,
    )
}

private class FakeScheduleRepository : ScheduleRepository {
    val createdSchedules = mutableListOf<Schedule>()
    val schedules = MutableStateFlow<List<Schedule>>(emptyList())

    override fun observeSchedules(): Flow<List<Schedule>> = schedules

    override suspend fun getSchedule(scheduleId: String): Schedule? =
        schedules.value.firstOrNull { it.id == scheduleId }

    override suspend fun getSchedulesByStates(states: Set<ScheduleState>): List<Schedule> =
        schedules.value.filter { it.state in states }

    override suspend fun createDraft(schedule: Schedule) {
        createdSchedules += schedule
        schedules.value = createdSchedules.toList()
    }

    override suspend fun updateSchedule(schedule: Schedule) {
        schedules.value = schedules.value.map { current -> if (current.id == schedule.id) schedule else current }
    }

    override suspend fun updateState(scheduleId: String, state: ScheduleState) {
        schedules.value = schedules.value.map { schedule ->
            if (schedule.id == scheduleId) schedule.copy(state = state) else schedule
        }
    }
}

private class FakeScheduleEventRepository : ScheduleEventRepository {
    val events = MutableStateFlow<List<ScheduleEvent>>(emptyList())

    override fun observeEvents(): Flow<List<ScheduleEvent>> = events

    override suspend fun recordEvent(event: ScheduleEvent) {
        events.value = listOf(event) + events.value
    }
}

private class FakeScheduleAlarmRegistrar(
    private val result: AlarmRegistrationResult = AlarmRegistrationResult.REGISTERED,
) : ScheduleAlarmRegistrar {
    override fun register(schedule: Schedule): AlarmRegistrationResult = result
    override fun cancel(scheduleId: String) = Unit
}

private class FakeLocalDataResetter : LocalDataResetter {
    var eraseRequests = 0

    override suspend fun eraseLocalData() {
        eraseRequests += 1
    }
}

private class FakeDeviceHealthEvaluator : DeviceHealthEvaluator {
    override fun evaluate(): DeviceHealthReport = DeviceHealthReport(
        exactAlarm = HealthState.READY,
        notifications = HealthState.READY,
    )
}
