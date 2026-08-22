package com.seduligma.app.data.local

import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ScheduleEntity::class, ScheduleEventEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class SeduligmaDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
    abstract fun scheduleEventDao(): ScheduleEventDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS schedule_events (
                        id TEXT NOT NULL,
                        scheduleId TEXT NOT NULL,
                        eventType TEXT NOT NULL,
                        localEvidence TEXT NOT NULL,
                        reasonCode TEXT,
                        occurredAtEpochMs INTEGER NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_schedule_events_scheduleId_occurredAtEpochMs ON schedule_events(scheduleId, occurredAtEpochMs)",
                )
            }
        }
    }
}
