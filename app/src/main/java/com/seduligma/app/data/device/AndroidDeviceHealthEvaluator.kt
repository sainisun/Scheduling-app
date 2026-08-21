package com.seduligma.app.data.device

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.seduligma.app.domain.device.DeviceHealthEvaluator
import com.seduligma.app.domain.device.DeviceHealthReport
import com.seduligma.app.domain.device.HealthState
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidDeviceHealthEvaluator @Inject constructor(
    @ApplicationContext private val context: Context,
) : DeviceHealthEvaluator {
    override fun evaluate(): DeviceHealthReport {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val exactAlarmReady = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
        val notificationsReady = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED &&
                NotificationManagerCompat.from(context).areNotificationsEnabled()
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }

        return DeviceHealthReport(
            exactAlarm = if (exactAlarmReady) HealthState.READY else HealthState.ACTION_REQUIRED,
            notifications = if (notificationsReady) HealthState.READY else HealthState.LIMITED,
        )
    }
}
