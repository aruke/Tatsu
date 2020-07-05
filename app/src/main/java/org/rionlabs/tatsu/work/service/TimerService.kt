package org.rionlabs.tatsu.work.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.Observer
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.rionlabs.tatsu.data.model.Timer
import org.rionlabs.tatsu.data.model.TimerState
import org.rionlabs.tatsu.utils.NotificationUtils
import org.rionlabs.tatsu.work.TimerController

class TimerService : Service(), KoinComponent {

    private val timerController: TimerController by inject()

    private val timerObserver = Observer<Timer> {
        it?.let { timer ->
            NotificationUtils.updateTimerNotification(this, timer)
            if (timer.state == TimerState.FINISHED) {
                stopForeground(false)
            } else if (timer.state == TimerState.CANCELLED) {
                stopForeground(true)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (timerController.isActiveTimerAvailable()) {
            val activeTimer = timerController.getActiveTimer()
            activeTimer.observeForever(timerObserver)
            val timer = activeTimer.value!!
            val notification = NotificationUtils.buildForTimer(this, timer)
            startForeground(NotificationUtils.TIMER_NOTIFICATION_ID, notification)
        }
        return START_NOT_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        if (timerController.isActiveTimerAvailable()) {
            val activeTimer = timerController.getActiveTimer()
            activeTimer.removeObserver(timerObserver)
        }
        return super.stopService(name)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {

        fun start(context: Context) {
            context.startService(Intent(context, TimerService::class.java))
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, TimerService::class.java))
        }
    }
}