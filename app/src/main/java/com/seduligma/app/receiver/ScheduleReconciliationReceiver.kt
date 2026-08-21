package com.seduligma.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.seduligma.app.domain.model.ScheduleState
import com.seduligma.app.domain.repository.ScheduleRepository
import com.seduligma.app.domain.scheduling.AlarmRegistrationResult
import com.seduligma.app.domain.scheduling.ScheduleAlarmRegistrar
import com.seduligma.app.domain.scheduling.ScheduleReconciliationPlanner
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScheduleReconciliationReceiver : BroadcastReceiver() {
    @Inject
    lateinit var scheduleRepository: ScheduleRepository

    @Inject
    lateinit var scheduleAlarmRegistrar: ScheduleAlarmRegistrar

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in RECOVERY_ACTIONS) return
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val schedules = scheduleRepository.getSchedulesByStates(setOf(ScheduleState.WAITING))
                val plan = ScheduleReconciliationPlanner.plan(schedules, Instant.now())
                plan.schedulesToRegister.forEach { schedule ->
                    when (scheduleAlarmRegistrar.register(schedule)) {
                        AlarmRegistrationResult.REGISTERED -> Unit
                        AlarmRegistrationResult.EXACT_ALARM_PERMISSION_REQUIRED ->
                            scheduleRepository.updateState(schedule.id, ScheduleState.NEEDS_PERMISSION)
                    }
                }
                plan.scheduleIdsToMarkUncertain.forEach { scheduleId ->
                    scheduleRepository.updateState(scheduleId, ScheduleState.UNCERTAIN)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        private val RECOVERY_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
        )
    }
}
