package org.rionlabs.tatsu

import android.app.Application
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import org.rionlabs.tatsu.utils.Utility
import org.rionlabs.tatsu.work.TimerController
import org.rionlabs.tatsu.work.TimerService
import timber.log.Timber

class TatsuApp : Application() {

    lateinit var timerController: TimerController
        private set

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        timerController = TimerController(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(timerController)

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleObserver {

            @OnLifecycleEvent(ON_STOP)
            fun onStop() {
                // App goes background
                if (timerController.isActiveTimerAvailable()) {
                    TimerService.start(this@TatsuApp)
                }
            }

            @OnLifecycleEvent(ON_START)
            fun onStart() {
                // App goes foreground
                TimerService.stop(this@TatsuApp)
            }
        })

        Utility.createNecessaryNotificationChannels(applicationContext)
    }
}