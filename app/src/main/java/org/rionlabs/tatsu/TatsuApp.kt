package org.rionlabs.tatsu

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.rionlabs.tatsu.di.AppModule
import org.rionlabs.tatsu.di.DataModule
import org.rionlabs.tatsu.di.TimerModule
import org.rionlabs.tatsu.di.ViewModelModule
import org.rionlabs.tatsu.utils.NotificationUtils
import timber.log.Timber

class TatsuApp : Application() {

    val coroutineScope = CoroutineScope(context = Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        startKoin {
            androidContext(this@TatsuApp)
            modules(
                AppModule.get(),
                DataModule.get(),
                TimerModule.get(),
                ViewModelModule.get()
            )
        }

        NotificationUtils.createNecessaryNotificationChannels(applicationContext)
    }

    override fun onTerminate() {
        super.onTerminate()
        coroutineScope.cancel("App Terminated")
    }
}