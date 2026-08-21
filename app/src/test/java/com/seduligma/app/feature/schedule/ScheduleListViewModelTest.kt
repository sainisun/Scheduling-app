package com.seduligma.app.feature.schedule

import com.seduligma.app.domain.model.Schedule
import com.seduligma.app.domain.model.ScheduleState
import com.seduligma.app.domain.repository.ScheduleRepository
import com.seduligma.app.testing.MainDispatcherRule
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
        val viewModel = ScheduleListViewModel(repository)

        viewModel.createDraft()

        advanceUntilIdle()
        assertEquals(1, repository.createdSchedules.size)
        assertEquals(ScheduleState.DRAFT, repository.createdSchedules.single().state)
    }
}

private class FakeScheduleRepository : ScheduleRepository {
    val createdSchedules = mutableListOf<Schedule>()
    private val schedules = MutableStateFlow<List<Schedule>>(emptyList())

    override fun observeSchedules(): Flow<List<Schedule>> = schedules

    override suspend fun createDraft(schedule: Schedule) {
        createdSchedules += schedule
        schedules.value = createdSchedules.toList()
    }

    override suspend fun updateState(scheduleId: String, state: ScheduleState) = Unit
}
