package org.rionlabs.tatsu

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import org.rionlabs.tatsu.work.TimerController
import timber.log.Timber

class TatsuApp : Application() {

    lateinit var timerController: TimerController
        private set

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        timerController = TimerController(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(timerController)
    }
}