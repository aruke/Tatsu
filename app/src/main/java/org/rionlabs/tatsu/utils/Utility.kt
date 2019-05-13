package org.rionlabs.tatsu.utils

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import org.rionlabs.tatsu.R
import timber.log.Timber

object Utility {

    fun waitForOneSecond() {
        try {
            Thread.sleep(1000)
        } catch (ie: InterruptedException) {
            Timber.i("Timer InterruptedException.")
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun createNecessaryNotificationChannels(context: Context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val timerNotificationChannel = NotificationChannel(
                context.getString(R.string.timer_channel_id),
                context.getString(R.string.timer_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.timer_channel_description)
        }

        val workHoursNotificationChannel = NotificationChannel(
                context.getString(R.string.work_hours_channel_id),
                context.getString(R.string.work_hours_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.work_hours_channel_description)
        }

        val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(timerNotificationChannel)
        notificationManager.createNotificationChannel(workHoursNotificationChannel)
    }
}