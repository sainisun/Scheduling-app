package com.seduligma.app.feature.schedule

import com.seduligma.app.domain.model.Schedule
import com.seduligma.app.domain.model.RecurrenceRule
import com.seduligma.app.domain.model.ScheduleState
import com.seduligma.app.domain.repository.ScheduleRepository
import com.seduligma.app.domain.scheduling.AlarmRegistrationResult
import com.seduligma.app.domain.scheduling.ScheduleAlarmRegistrar
import com.seduligma.app.testing.MainDispatcherRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.Instant

class ScheduleListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `creating a draft delegates to repository`() = runTest {
        val repository = FakeScheduleRepository()
        val viewModel = ScheduleListViewModel(repository, FakeScheduleAlarmRegistrar())

        viewModel.createDraft()

        advanceUntilIdle()
        assertEquals(1, repository.createdSchedules.size)
        assertEquals(ScheduleState.DRAFT, repository.createdSchedules.single().state)
    }

    @Test
    fun `pause and cancel delegate explicit schedule states`() = runTest {
        val repository = FakeScheduleRepository()
        val viewModel = ScheduleListViewModel(repository, FakeScheduleAlarmRegistrar())
        val schedule = Schedule(
            id = "schedule-1",
            title = "Follow up",
            messagePreview = "Test",
            scheduledAt = Instant.now(),
            timezoneId = "UTC",
            recurrence = RecurrenceRule.ONCE,
            state = ScheduleState.WAITING,
        )
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
        val viewModel = ScheduleListViewModel(
            repository,
            FakeScheduleAlarmRegistrar(AlarmRegistrationResult.EXACT_ALARM_PERMISSION_REQUIRED),
        )
        val schedule = Schedule(
            id = "schedule-2",
            title = "Permission check",
            messagePreview = "Test",
            scheduledAt = Instant.now().plusSeconds(3600),
            timezoneId = "UTC",
            recurrence = RecurrenceRule.ONCE,
            state = ScheduleState.DRAFT,
        )
        repository.createDraft(schedule)

        viewModel.activateSchedule(schedule)
        advanceUntilIdle()

        assertEquals(ScheduleState.NEEDS_PERMISSION, repository.schedules.value.single().state)
    }
}

private class FakeScheduleRepository : ScheduleRepository {
    val createdSchedules = mutableListOf<Schedule>()
    val schedules = MutableStateFlow<List<Schedule>>(emptyList())

    override fun observeSchedules(): Flow<List<Schedule>> = schedules

    override suspend fun createDraft(schedule: Schedule) {
        createdSchedules += schedule
        schedules.value = createdSchedules.toList()
    }

    override suspend fun updateState(scheduleId: String, state: ScheduleState) {
        schedules.value = schedules.value.map { schedule ->
            if (schedule.id == scheduleId) schedule.copy(state = state) else schedule
        }
    }
}

private class FakeScheduleAlarmRegistrar(
    private val result: AlarmRegistrationResult = AlarmRegistrationResult.REGISTERED,
) : ScheduleAlarmRegistrar {
    override fun register(schedule: Schedule): AlarmRegistrationResult = result

    override fun cancel(scheduleId: String) = Unit
}
