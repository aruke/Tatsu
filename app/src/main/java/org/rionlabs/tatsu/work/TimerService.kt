package org.rionlabs.tatsu.work

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.Observer
import org.rionlabs.tatsu.TatsuApp
import org.rionlabs.tatsu.data.model.Timer
import org.rionlabs.tatsu.data.model.TimerState
import org.rionlabs.tatsu.utils.NotificationUI

class TimerService : Service() {

    private lateinit var timerController: TimerController

    private val timerObserver = Observer<Timer> {
        it?.let { timer ->
            NotificationUI.update(this, timer)
            if (timer.state == TimerState.STOPPED) {
                stopForeground(false)
            } else if (timer.state == TimerState.CANCELLED) {
                stopForeground(true)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        timerController = (application as TatsuApp).timerController
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (timerController.isActiveTimerAvailable()) {
            val activeTimer = timerController.getActiveTimer()
            activeTimer.observeForever(timerObserver)
            val timer = activeTimer.value!!
            val notification = NotificationUI.getNotificationFor(this, timer)
            startForeground(NotificationUI.TIMER_NOTIFICATION_ID, notification)
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