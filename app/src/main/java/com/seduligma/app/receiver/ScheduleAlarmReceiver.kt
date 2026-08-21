package com.seduligma.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.seduligma.app.domain.model.ScheduleState
import com.seduligma.app.domain.repository.ScheduleRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScheduleAlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var scheduleRepository: ScheduleRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_SCHEDULE_DUE) return
        val scheduleId = intent.getStringExtra(EXTRA_SCHEDULE_ID) ?: return
        val pendingResult = goAsync()

        NotificationChannels.ensureCreated(context)
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            NotificationManagerCompat.from(context).notify(
                scheduleId.hashCode(),
                NotificationCompat.Builder(context, NotificationChannels.SCHEDULE_CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Scheduled action is ready")
                    .setContentText("Open Seduligma to review the scheduled item.")
                    .setAutoCancel(true)
                    .build(),
            )
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                scheduleRepository.updateState(scheduleId, ScheduleState.AWAITING_USER)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_SCHEDULE_DUE = "com.seduligma.app.action.SCHEDULE_DUE"
        const val EXTRA_SCHEDULE_ID = "schedule_id"
    }
}
