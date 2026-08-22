package com.seduligma.app.domain.repository

import com.seduligma.app.domain.model.ScheduleEvent
import kotlinx.coroutines.flow.Flow

interface ScheduleEventRepository {
    fun observeEvents(): Flow<List<ScheduleEvent>>
    suspend fun recordEvent(event: ScheduleEvent)
}
