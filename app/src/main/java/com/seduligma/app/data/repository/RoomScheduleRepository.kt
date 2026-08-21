package com.seduligma.app.data.repository

import com.seduligma.app.data.local.ScheduleDao
import com.seduligma.app.data.local.ScheduleEntity
import com.seduligma.app.domain.model.RecurrenceRule
import com.seduligma.app.domain.model.Schedule
import com.seduligma.app.domain.model.ScheduleState
import com.seduligma.app.domain.repository.ScheduleRepository
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomScheduleRepository @Inject constructor(
    private val scheduleDao: ScheduleDao,
) : ScheduleRepository {
    override fun observeSchedules(): Flow<List<Schedule>> =
        scheduleDao.observeAll().map { schedules -> schedules.map(ScheduleEntity::toDomain) }

    override suspend fun getSchedulesByStates(states: Set<ScheduleState>): List<Schedule> =
        scheduleDao.getByStates(states.map(ScheduleState::name)).map(ScheduleEntity::toDomain)

    override suspend fun createDraft(schedule: Schedule) {
        scheduleDao.upsert(schedule.toEntity())
    }

    override suspend fun updateState(scheduleId: String, state: ScheduleState) {
        scheduleDao.updateState(
            scheduleId = scheduleId,
            state = state.name,
            updatedAtEpochMs = System.currentTimeMillis(),
        )
    }
}

private fun ScheduleEntity.toDomain(): Schedule = Schedule(
    id = id,
    title = title,
    messagePreview = messagePreview,
    scheduledAt = Instant.ofEpochMilli(scheduledAtEpochMs),
    timezoneId = timezoneId,
    recurrence = RecurrenceRule.valueOf(recurrence),
    state = ScheduleState.valueOf(state),
)

private fun Schedule.toEntity(): ScheduleEntity {
    val now = System.currentTimeMillis()
    return ScheduleEntity(
        id = id,
        title = title,
        messagePreview = messagePreview,
        scheduledAtEpochMs = scheduledAt.toEpochMilli(),
        timezoneId = timezoneId,
        recurrence = recurrence.name,
        state = state.name,
        createdAtEpochMs = now,
        updatedAtEpochMs = now,
    )
}
