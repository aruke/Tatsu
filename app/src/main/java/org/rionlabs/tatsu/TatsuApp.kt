package org.rionlabs.tatsu

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.rionlabs.tatsu.di.AppModule
import org.rionlabs.tatsu.di.DataModule
import org.rionlabs.tatsu.di.ViewModelModule
import org.rionlabs.tatsu.utils.NotificationUtils
import org.rionlabs.tatsu.work.TimerController
import org.rionlabs.tatsu.work.service.TimerService
import timber.log.Timber

class TatsuApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        startKoin {
            androidContext(this@TatsuApp)
            modules(
                AppModule.get(),
                DataModule.get(),
                ViewModelModule.get()
            )
        }

        initializeProcessLifecycle()
        NotificationUtils.createNecessaryNotificationChannels(applicationContext)
    }

    /**
     * Setup [ProcessLifecycleOwner] with instances of [TimerController] and [TimerService].
     */
    private fun initializeProcessLifecycle() {
        val timerController = get<TimerController>()
        ProcessLifecycleOwner.get().lifecycle.addObserver(timerController)
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onStop() {
                // App goes background
                if (timerController.isActiveTimerAvailable()) {
                    TimerService.start(this@TatsuApp)
                }
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onStart() {
                // App goes foreground
                TimerService.stop(this@TatsuApp)
            }
        })
    }
}