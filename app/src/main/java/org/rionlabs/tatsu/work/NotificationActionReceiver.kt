package org.rionlabs.tatsu.work

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.rionlabs.tatsu.TatsuApp
import timber.log.Timber

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val timerController = (context?.applicationContext as? TatsuApp)?.timerController
        timerController?.let {
            when (intent?.action) {
                ACTION_START ->
                    it.resumeTimer()
                ACTION_PAUSE ->
                    it.pauseTimer()
                ACTION_STOP ->
                    it.cancelTimer()
                else ->
                    Timber.i("No matching action found")
            }
        }
    }

    companion object {
        const val ACTION_START = "org.rionlabs.tatsu.start"
        const val ACTION_PAUSE = "org.rionlabs.tatsu.pause"
        const val ACTION_STOP = "org.rionlabs.tatsu.stop"
    }
}