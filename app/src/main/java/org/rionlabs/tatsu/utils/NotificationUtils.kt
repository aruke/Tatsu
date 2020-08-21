package org.rionlabs.tatsu.utils

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import org.rionlabs.tatsu.R
import org.rionlabs.tatsu.data.model.Timer
import org.rionlabs.tatsu.data.model.TimerState
import org.rionlabs.tatsu.ui.screen.begin.SplashActivity
import org.rionlabs.tatsu.work.receiver.NotificationActionReceiver

object NotificationUtils {

    const val TIMER_NOTIFICATION_ID = 23
    const val WORK_HOURS_START_NOTIFICATION_ID = 24
    const val WORK_HOURS_END_NOTIFICATION_ID = 25

    fun buildForTimer(context: Context, timer: Timer): Notification {

        val title = context.getString(R.string.timer_notification_title, timer.state.toString())

        val pendingIntent = Intent(context, SplashActivity::class.java).let { intent ->
            PendingIntent.getActivity(context, 0, intent, 0)
        }

        val message = with(timer) {
            context.getString(R.string.timer_notification_message, hours, minutes, seconds)
        }

        val builder =
            NotificationCompat.Builder(context, context.getString(R.string.timer_channel_id))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notification_timer)
                .setColor(ContextCompat.getColor(context, R.color.color_primary))
                .setOnlyAlertOnce(true)
                .setShowWhen(false)

        if (timer.state == TimerState.PAUSED) {
            builder.addAction(
                R.drawable.ic_play,
                context.getString(R.string.action_resume),
                NotificationActionReceiver.createPendingBroadcastForResume(context)
            )
        }

        if (timer.state == TimerState.RUNNING) {
            builder.addAction(
                R.drawable.ic_pause,
                context.getString(R.string.action_pause),
                NotificationActionReceiver.createPendingBroadcastForPause(context)
            )
        }

        if (timer.state == TimerState.PAUSED || timer.state == TimerState.RUNNING) {
            builder.addAction(
                R.drawable.ic_stop,
                context.getString(R.string.action_stop),
                NotificationActionReceiver.createPendingBroadcastForStop(context)
            )
        }

        return builder.build()
    }

    fun updateTimerNotification(context: Context, timer: Timer) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = buildForTimer(context, timer)
        notificationManager.notify(TIMER_NOTIFICATION_ID, notification)
    }

    fun buildForStartWork(context: Context): Notification {
        val pendingIntent = Intent(context, SplashActivity::class.java).let { intent ->
            PendingIntent.getActivity(context, 0, intent, 0)
        }
        val builder =
            NotificationCompat.Builder(context, context.getString(R.string.work_hours_channel_id))
                .setContentTitle(context.getString(R.string.work_hours_start_notification_title))
                .setContentText(context.getString(R.string.work_hours_start_notification_message))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(ContextCompat.getColor(context, R.color.color_primary))
                .setAutoCancel(true)
        return builder.setSmallIcon(R.drawable.ic_notifications).build()
    }

    fun buildForEndWork(context: Context): Notification {
        val pendingIntent = Intent(context, SplashActivity::class.java).let { intent ->
            PendingIntent.getActivity(context, 0, intent, 0)
        }
        val builder =
            NotificationCompat.Builder(context, context.getString(R.string.work_hours_channel_id))
                .setContentTitle(context.getString(R.string.work_hours_end_notification_title))
                .setContentText(context.getString(R.string.work_hours_end_notification_message))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
        return builder.setSmallIcon(R.drawable.ic_notifications).build()
    }

    /**
     * Creates notification channels above Android O.
     */
    @TargetApi(Build.VERSION_CODES.O)
    fun createNecessaryNotificationChannels(context: Context) {
        // Target SDK check
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        // Service notification for timer
        val timerNotificationChannel = NotificationChannel(
            context.getString(R.string.timer_channel_id),
            context.getString(R.string.timer_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.timer_channel_description)
        }

        // Reminders for work hours
        val workHoursNotificationChannel = NotificationChannel(
            context.getString(R.string.work_hours_channel_id),
            context.getString(R.string.work_hours_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.work_hours_channel_description)
        }

        // Create channels
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            createNotificationChannel(timerNotificationChannel)
            createNotificationChannel(workHoursNotificationChannel)
        }
    }
}