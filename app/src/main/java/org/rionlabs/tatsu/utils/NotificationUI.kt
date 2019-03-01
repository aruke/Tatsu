package org.rionlabs.tatsu.utils

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import org.rionlabs.tatsu.R
import org.rionlabs.tatsu.data.model.Timer
import org.rionlabs.tatsu.data.model.TimerState
import org.rionlabs.tatsu.ui.screen.begin.SplashActivity

object NotificationUI {

    const val TIMER_NOTIFICATION_ID = 23

    fun getNotificationFor(context: Context, timer: Timer): Notification {

        val builder = NotificationCompat.Builder(context, context.getString(R.string.timer_channel_id))

        val title = context.getString(R.string.timer_notification_title, timer.state.toString())
        builder.setContentTitle(title)

        val pendingIntent = Intent(context, SplashActivity::class.java).let { intent ->
            PendingIntent.getActivity(context, 0, intent, 0)
        }
        builder.setContentIntent(pendingIntent)

        val minutes = timer.duration % 60
        val hours = timer.duration / 60
        val message = context.getString(R.string.timer_notification_message, hours, minutes)
        builder.setContentText(message)

        if (timer.state == TimerState.PAUSED) {
            builder.addAction(0, "Resume", null)
        }

        if (timer.state == TimerState.RUNNING) {
            builder.addAction(0, "Pause", null)
        }

        if (timer.state == TimerState.PAUSED || timer.state == TimerState.RUNNING) {
            builder.addAction(0, "Stop", null)
        }

        return builder.setSmallIcon(R.drawable.ic_timer).build()
    }

    fun update(context: Context, timer: Timer) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.activeNotifications.firstOrNull {
                it.id == TIMER_NOTIFICATION_ID
            }?.apply {
                show(context, timer)
            }
        } else {
            show(context, timer)
        }
    }

    private fun show(context: Context, timer: Timer) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = getNotificationFor(context, timer)
        notificationManager.notify(TIMER_NOTIFICATION_ID, notification)
    }
}