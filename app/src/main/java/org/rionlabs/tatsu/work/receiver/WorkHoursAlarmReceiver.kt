package org.rionlabs.tatsu.work.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import org.koin.core.KoinComponent
import org.koin.core.get
import org.rionlabs.tatsu.utils.NotificationUtils
import org.rionlabs.tatsu.work.SettingsManager
import timber.log.Timber

class WorkHoursAlarmReceiver : BroadcastReceiver(), KoinComponent {

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("Pending Intent Received with action ${intent.action}")

        val settingsManager: SettingsManager = get()
        if (!settingsManager.workHourNotificationsEnabled) {
            Timber.i("Notifications for work-hours are disabled")
            return
        }

        val notification = when (intent.action) {
            ACTION_SHOW_START_WORK_NOTIFICATION ->
                NotificationUtils.buildForStartWork(context)
            ACTION_SHOW_END_WORK_NOTIFICATION ->
                NotificationUtils.buildForEndWork(context)
            else ->
                throw IllegalStateException("Unknown action")
        }

        val notificationId = when (intent.action) {
            ACTION_SHOW_START_WORK_NOTIFICATION ->
                NotificationUtils.WORK_HOURS_START_NOTIFICATION_ID
            ACTION_SHOW_END_WORK_NOTIFICATION ->
                NotificationUtils.WORK_HOURS_END_NOTIFICATION_ID
            else ->
                throw IllegalStateException("Unknown action")
        }

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, notification)
        }
    }

    companion object {
        const val ACTION_SHOW_START_WORK_NOTIFICATION = "org.rionlabs.tatsu.start_work"
        const val ACTION_SHOW_END_WORK_NOTIFICATION = "org.rionlabs.tatsu.end_work"
    }
}