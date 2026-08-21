package com.seduligma.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val messagePreview: String,
    val scheduledAtEpochMs: Long,
    val timezoneId: String,
    val recurrence: String,
    val state: String,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long,
)
