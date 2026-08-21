package com.seduligma.app.domain.repository

import com.seduligma.app.domain.model.Schedule
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun observeSchedules(): Flow<List<Schedule>>

    suspend fun createDraft(schedule: Schedule)

    suspend fun updateState(scheduleId: String, state: com.seduligma.app.domain.model.ScheduleState)
}
