package com.seduligma.app.data.privacy

import androidx.core.app.NotificationManagerCompat
import com.seduligma.app.data.local.ScheduleDao
import com.seduligma.app.data.local.SeduligmaDatabase
import com.seduligma.app.domain.privacy.LocalDataResetter
import com.seduligma.app.domain.scheduling.ScheduleAlarmRegistrar
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomLocalDataResetter @Inject constructor(
    private val database: SeduligmaDatabase,
    private val scheduleDao: ScheduleDao,
    private val scheduleAlarmRegistrar: ScheduleAlarmRegistrar,
    @ApplicationContext private val context: Context,
) : LocalDataResetter {
    override suspend fun eraseLocalData() {
        scheduleDao.getAll().forEach { schedule -> scheduleAlarmRegistrar.cancel(schedule.id) }
        NotificationManagerCompat.from(context).cancelAll()
        database.clearAllTables()
    }
}
