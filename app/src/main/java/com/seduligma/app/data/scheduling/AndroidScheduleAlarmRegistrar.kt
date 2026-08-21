package com.seduligma.app.data.scheduling

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.seduligma.app.domain.model.Schedule
import com.seduligma.app.domain.scheduling.AlarmRegistrationResult
import com.seduligma.app.domain.scheduling.ScheduleAlarmRegistrar
import com.seduligma.app.receiver.ScheduleAlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidScheduleAlarmRegistrar @Inject constructor(
    @ApplicationContext private val context: Context,
) : ScheduleAlarmRegistrar {
    private val alarmManager: AlarmManager = context.getSystemService(AlarmManager::class.java)

    override fun register(schedule: Schedule): AlarmRegistrationResult {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            return AlarmRegistrationResult.EXACT_ALARM_PERMISSION_REQUIRED
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            schedule.scheduledAt.toEpochMilli(),
            pendingIntent(schedule.id),
        )
        return AlarmRegistrationResult.REGISTERED
    }

    override fun cancel(scheduleId: String) {
        alarmManager.cancel(pendingIntent(scheduleId))
    }

    private fun pendingIntent(scheduleId: String): PendingIntent {
        val intent = Intent(context, ScheduleAlarmReceiver::class.java)
            .setAction(ScheduleAlarmReceiver.ACTION_SCHEDULE_DUE)
            .putExtra(ScheduleAlarmReceiver.EXTRA_SCHEDULE_ID, scheduleId)
        return PendingIntent.getBroadcast(
            context,
            scheduleId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
