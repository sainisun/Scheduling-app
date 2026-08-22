package com.seduligma.app.data.repository

import com.seduligma.app.data.local.ScheduleEventDao
import com.seduligma.app.data.local.ScheduleEventEntity
import com.seduligma.app.domain.model.LocalEvidence
import com.seduligma.app.domain.model.ScheduleEvent
import com.seduligma.app.domain.model.ScheduleEventType
import com.seduligma.app.domain.model.ScheduleReasonCode
import com.seduligma.app.domain.repository.ScheduleEventRepository
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomScheduleEventRepository @Inject constructor(
    private val scheduleEventDao: ScheduleEventDao,
) : ScheduleEventRepository {
    override fun observeEvents(): Flow<List<ScheduleEvent>> = scheduleEventDao.observeRecent().map { entities ->
        entities.map(ScheduleEventEntity::toDomain)
    }

    override suspend fun recordEvent(event: ScheduleEvent) {
        scheduleEventDao.insert(event.toEntity())
    }
}

private fun ScheduleEventEntity.toDomain() = ScheduleEvent(
    id = id,
    scheduleId = scheduleId,
    eventType = ScheduleEventType.valueOf(eventType),
    localEvidence = LocalEvidence.valueOf(localEvidence),
    reasonCode = reasonCode?.let(ScheduleReasonCode::valueOf),
    occurredAt = Instant.ofEpochMilli(occurredAtEpochMs),
)

private fun ScheduleEvent.toEntity() = ScheduleEventEntity(
    id = id,
    scheduleId = scheduleId,
    eventType = eventType.name,
    localEvidence = localEvidence.name,
    reasonCode = reasonCode?.name,
    occurredAtEpochMs = occurredAt.toEpochMilli(),
)
