package org.rionlabs.tatsu.work.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.KoinComponent
import org.koin.core.get
import org.rionlabs.tatsu.work.TimerController
import timber.log.Timber

class NotificationActionReceiver : BroadcastReceiver(), KoinComponent {

    override fun onReceive(context: Context?, intent: Intent?) {
        val timerController = get<TimerController>()
        when (intent?.action) {
            ACTION_START ->
                timerController.resumeTimer()
            ACTION_PAUSE ->
                timerController.pauseTimer()
            ACTION_STOP ->
                timerController.cancelTimer()
            else ->
                Timber.i("No matching action found")
        }
    }

    companion object {
        const val ACTION_START = "org.rionlabs.tatsu.start"
        const val ACTION_PAUSE = "org.rionlabs.tatsu.pause"
        const val ACTION_STOP = "org.rionlabs.tatsu.stop"
    }
}