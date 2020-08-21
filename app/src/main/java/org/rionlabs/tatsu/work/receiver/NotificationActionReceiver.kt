package org.rionlabs.tatsu.work.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.KoinComponent
import org.rionlabs.tatsu.work.service.TimerService
import timber.log.Timber

class NotificationActionReceiver : BroadcastReceiver(), KoinComponent {

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: kotlin.run {
            Timber.i("onReceive: Context is Null")
            return
        }

        when (intent?.action) {
            ACTION_RESUME ->
                TimerService.resumeTimer(context)
            ACTION_PAUSE ->
                TimerService.pauseTimer(context)
            ACTION_STOP ->
                TimerService.stopTimer(context)
            else ->
                Timber.i("No matching action found in receiver NotificationActionReceiver")
        }
    }

    companion object {
        private const val ACTION_RESUME = "org.rionlabs.tatsu.ACTION_RESUME"
        private const val ACTION_PAUSE = "org.rionlabs.tatsu.ACTION_PAUSE"
        private const val ACTION_STOP = "org.rionlabs.tatsu.ACTION_STOP"

        fun createPendingBroadcastForResume(context: Context): PendingIntent =
            pendingIntentForAction(context, ACTION_RESUME)

        fun createPendingBroadcastForPause(context: Context): PendingIntent =
            pendingIntentForAction(context, ACTION_PAUSE)

        fun createPendingBroadcastForStop(context: Context): PendingIntent =
            pendingIntentForAction(context, ACTION_STOP)

        private fun pendingIntentForAction(context: Context, intentAction: String) =
            PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, NotificationActionReceiver::class.java).apply {
                    action = intentAction
                },
                PendingIntent.FLAG_UPDATE_CURRENT
            )
    }
}