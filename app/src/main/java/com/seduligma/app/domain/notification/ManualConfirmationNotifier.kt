package com.seduligma.app.domain.notification

/**
 * Shows a local notification that asks the user to review a due local schedule.
 * It must never send a message, collect a credential, or claim delivery evidence.
 */
enum class ManualConfirmationNotificationResult { POSTED, NOTIFICATIONS_DISABLED }

interface ManualConfirmationNotifier {
    fun showScheduleReady(scheduleId: String): ManualConfirmationNotificationResult
}
