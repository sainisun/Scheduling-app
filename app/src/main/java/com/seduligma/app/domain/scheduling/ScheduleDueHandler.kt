package com.seduligma.app.domain.scheduling

import com.seduligma.app.domain.model.ScheduleState
import com.seduligma.app.domain.model.LocalEvidence
import com.seduligma.app.domain.model.ScheduleEvent
import com.seduligma.app.domain.model.ScheduleEventType
import com.seduligma.app.domain.model.ScheduleReasonCode
import com.seduligma.app.domain.notification.ManualConfirmationNotificationResult
import com.seduligma.app.domain.notification.ManualConfirmationNotifier
import com.seduligma.app.domain.repository.ScheduleEventRepository
import com.seduligma.app.domain.repository.ScheduleRepository
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class ScheduleDueHandler @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val scheduleEventRepository: ScheduleEventRepository,
    private val manualConfirmationNotifier: ManualConfirmationNotifier,
) {
    suspend fun handleDueSchedule(scheduleId: String) {
        val schedule = scheduleRepository.getSchedule(scheduleId) ?: return
        if (schedule.state != ScheduleState.WAITING) return

        when (manualConfirmationNotifier.showScheduleReady(scheduleId)) {
            ManualConfirmationNotificationResult.POSTED -> {
                scheduleRepository.updateState(scheduleId, ScheduleState.ATTEMPTING)
                scheduleEventRepository.recordEvent(
                    ScheduleEvent(
                        id = UUID.randomUUID().toString(),
                        scheduleId = scheduleId,
                        eventType = ScheduleEventType.ATTEMPTED,
                        localEvidence = LocalEvidence.NOTIFICATION_POSTED,
                        occurredAt = Instant.now(),
                    ),
                )
            }
            ManualConfirmationNotificationResult.NOTIFICATIONS_DISABLED -> {
                scheduleRepository.updateState(scheduleId, ScheduleState.NEEDS_PERMISSION)
                scheduleEventRepository.recordEvent(
                    ScheduleEvent(
                        id = UUID.randomUUID().toString(),
                        scheduleId = scheduleId,
                        eventType = ScheduleEventType.OUTCOME_RECORDED,
                        localEvidence = LocalEvidence.NONE,
                        reasonCode = ScheduleReasonCode.NOTIFICATION_DENIED,
                        occurredAt = Instant.now(),
                    ),
                )
            }
        }
    }
}
