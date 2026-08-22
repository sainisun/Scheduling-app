package com.seduligma.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleEventDao {
    @Query("SELECT * FROM schedule_events ORDER BY occurredAtEpochMs DESC LIMIT 100")
    fun observeRecent(): Flow<List<ScheduleEventEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(event: ScheduleEventEntity)

    @Query("DELETE FROM schedule_events")
    suspend fun deleteAll()
}
