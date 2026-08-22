package com.seduligma.app.domain.scheduling

import com.seduligma.app.domain.model.ScheduleState
import com.seduligma.app.domain.notification.ManualConfirmationNotifier
import com.seduligma.app.domain.repository.ScheduleRepository
import javax.inject.Inject

class ScheduleDueHandler @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val manualConfirmationNotifier: ManualConfirmationNotifier,
) {
    suspend fun handleDueSchedule(scheduleId: String) {
        scheduleRepository.updateState(scheduleId, ScheduleState.AWAITING_USER)
        manualConfirmationNotifier.showScheduleReady(scheduleId)
    }
}
