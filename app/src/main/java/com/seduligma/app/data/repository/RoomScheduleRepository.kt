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

    override suspend fun getSchedule(scheduleId: String): Schedule? =
        scheduleDao.getById(scheduleId)?.toDomain()

    override suspend fun getSchedulesByStates(states: Set<ScheduleState>): List<Schedule> =
        scheduleDao.getByStates(states.map(ScheduleState::name)).map(ScheduleEntity::toDomain)

    override suspend fun createDraft(schedule: Schedule) {
        scheduleDao.upsert(schedule.toEntity())
    }

    override suspend fun updateSchedule(schedule: Schedule) {
        scheduleDao.updateSchedule(
            scheduleId = schedule.id,
            title = schedule.title,
            messagePreview = schedule.messagePreview,
            scheduledAtEpochMs = schedule.scheduledAt.toEpochMilli(),
            timezoneId = schedule.timezoneId,
            recurrence = schedule.recurrence.name,
            state = schedule.state.name,
            updatedAtEpochMs = System.currentTimeMillis(),
        )
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
    state = state.toScheduleState(),
)

private fun String.toScheduleState(): ScheduleState = when (this) {
    // Compatibility for pre-canonical Beta-1 rows written by earlier builds.
    "READY" -> ScheduleState.DRAFT
    "AWAITING_USER" -> ScheduleState.ATTEMPTING
    else -> ScheduleState.valueOf(this)
}

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
