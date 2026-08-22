package com.seduligma.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.seduligma.app.domain.scheduling.ScheduleDueHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScheduleAlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var scheduleDueHandler: ScheduleDueHandler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_SCHEDULE_DUE) return
        val scheduleId = intent.getStringExtra(EXTRA_SCHEDULE_ID) ?: return
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                scheduleDueHandler.handleDueSchedule(scheduleId)
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
