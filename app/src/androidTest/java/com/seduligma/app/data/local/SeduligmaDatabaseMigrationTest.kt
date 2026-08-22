package com.seduligma.app.data.local

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SeduligmaDatabaseMigrationTest {
    @Test
    fun migrationFromOneToTwoPreservesSchedulesAndAddsEventHistory() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase(TEST_DATABASE_NAME)
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(TEST_DATABASE_NAME)
                .callback(object : SupportSQLiteOpenHelper.Callback(1) {
                    override fun onCreate(database: SupportSQLiteDatabase) {
                        database.execSQL(
                            """
                            CREATE TABLE schedules (
                                id TEXT NOT NULL,
                                title TEXT NOT NULL,
                                messagePreview TEXT NOT NULL,
                                scheduledAtEpochMs INTEGER NOT NULL,
                                timezoneId TEXT NOT NULL,
                                recurrence TEXT NOT NULL,
                                state TEXT NOT NULL,
                                createdAtEpochMs INTEGER NOT NULL,
                                updatedAtEpochMs INTEGER NOT NULL,
                                PRIMARY KEY(id)
                            )
                            """.trimIndent(),
                        )
                    }

                    override fun onUpgrade(database: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
                })
                .build(),
        )

        try {
            val database = helper.writableDatabase
            database.execSQL(
                """
                INSERT INTO schedules (
                    id, title, messagePreview, scheduledAtEpochMs, timezoneId, recurrence, state, createdAtEpochMs, updatedAtEpochMs
                ) VALUES ('schedule-1', 'Follow up', 'Hello', 1000, 'UTC', 'ONCE', 'WAITING', 500, 500)
                """.trimIndent(),
            )

            SeduligmaDatabase.MIGRATION_1_2.migrate(database)

            database.query("SELECT id, title FROM schedules WHERE id = 'schedule-1'").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals("schedule-1", cursor.getString(0))
                assertEquals("Follow up", cursor.getString(1))
            }
            database.query("SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'schedule_events'").use { cursor ->
                assertTrue(cursor.moveToFirst())
            }
        } finally {
            helper.close()
            context.deleteDatabase(TEST_DATABASE_NAME)
        }
    }

    private companion object {
        const val TEST_DATABASE_NAME = "p1_05_migration_test.db"
    }
}
