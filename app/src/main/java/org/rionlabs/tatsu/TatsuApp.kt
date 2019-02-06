package org.rionlabs.tatsu

import android.app.Application
import timber.log.Timber

class TatsuApp: Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}