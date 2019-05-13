package org.rionlabs.tatsu.work

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import org.rionlabs.tatsu.utils.NotificationUI
import org.rionlabs.tatsu.utils.NotificationUI.WORK_HOURS_NOTIFICATION_ID
import timber.log.Timber

class WorkTimeAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("Pending Intent Received with action ${intent.action}")
        Toast.makeText(context, intent.action, Toast.LENGTH_LONG).show()

        val notification = when (intent.action) {
            ACTION_SHOW_START_WORK_NOTIFICATION ->
                NotificationUI.getForStartWork(context)
            ACTION_SHOW_END_WORK_NOTIFICATION ->
                NotificationUI.getForEndWork(context)
            else ->
                throw IllegalStateException("Unknown action")
        }

        with(NotificationManagerCompat.from(context)) {
            notify(WORK_HOURS_NOTIFICATION_ID, notification)
        }
    }

    companion object {
        const val ACTION_SHOW_START_WORK_NOTIFICATION = "org.rionlabs.tatsu.start_work"
        const val ACTION_SHOW_END_WORK_NOTIFICATION = "org.rionlabs.tatsu.end_work"
    }
}