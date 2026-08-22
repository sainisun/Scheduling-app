package com.seduligma.app.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "schedule_events",
    indices = [Index(value = ["scheduleId", "occurredAtEpochMs"])],
)
data class ScheduleEventEntity(
    @PrimaryKey val id: String,
    val scheduleId: String,
    val eventType: String,
    val localEvidence: String,
    val reasonCode: String?,
    val occurredAtEpochMs: Long,
)
