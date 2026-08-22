package com.seduligma.app.data.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.seduligma.app.MainActivity
import com.seduligma.app.domain.notification.ManualConfirmationNotificationResult
import com.seduligma.app.domain.notification.ManualConfirmationNotifier
import com.seduligma.app.receiver.NotificationChannels
import com.seduligma.app.receiver.ScheduleAlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidManualConfirmationNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : ManualConfirmationNotifier {
    override fun showScheduleReady(scheduleId: String): ManualConfirmationNotificationResult {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            return ManualConfirmationNotificationResult.NOTIFICATIONS_DISABLED
        }

        NotificationChannels.ensureCreated(context)
        val reviewIntent = Intent(context, MainActivity::class.java).apply {
            action = ACTION_REVIEW_SCHEDULE
            putExtra(ScheduleAlarmReceiver.EXTRA_SCHEDULE_ID, scheduleId)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val reviewPendingIntent = PendingIntent.getActivity(
            context,
            scheduleId.hashCode(),
            reviewIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        NotificationManagerCompat.from(context).notify(
            scheduleId.hashCode(),
            NotificationCompat.Builder(context, NotificationChannels.SCHEDULE_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Review scheduled message")
                .setContentText("Your scheduled draft is ready. Seduligma will not send anything automatically.")
                .setContentIntent(reviewPendingIntent)
                .addAction(0, "Review schedule", reviewPendingIntent)
                .setAutoCancel(true)
                .build(),
        )
        return ManualConfirmationNotificationResult.POSTED
    }

    companion object {
        const val ACTION_REVIEW_SCHEDULE = "com.seduligma.app.action.REVIEW_SCHEDULE"
    }
}
