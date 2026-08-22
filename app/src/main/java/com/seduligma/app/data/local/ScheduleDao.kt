package com.seduligma.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedules ORDER BY scheduledAtEpochMs ASC")
    fun observeAll(): Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM schedules")
    suspend fun getAll(): List<ScheduleEntity>

    @Query("SELECT * FROM schedules WHERE state IN (:states) ORDER BY scheduledAtEpochMs ASC")
    suspend fun getByStates(states: List<String>): List<ScheduleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(schedule: ScheduleEntity)

    @Query(
        """
        UPDATE schedules
        SET title = :title,
            messagePreview = :messagePreview,
            scheduledAtEpochMs = :scheduledAtEpochMs,
            timezoneId = :timezoneId,
            recurrence = :recurrence,
            state = :state,
            updatedAtEpochMs = :updatedAtEpochMs
        WHERE id = :scheduleId
        """,
    )
    suspend fun updateSchedule(
        scheduleId: String,
        title: String,
        messagePreview: String,
        scheduledAtEpochMs: Long,
        timezoneId: String,
        recurrence: String,
        state: String,
        updatedAtEpochMs: Long,
    )

    @Query("UPDATE schedules SET state = :state, updatedAtEpochMs = :updatedAtEpochMs WHERE id = :scheduleId")
    suspend fun updateState(scheduleId: String, state: String, updatedAtEpochMs: Long)

    @Query("DELETE FROM schedules")
    suspend fun deleteAll()
}
