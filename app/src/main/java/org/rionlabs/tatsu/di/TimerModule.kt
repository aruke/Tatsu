package org.rionlabs.tatsu.di

import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import org.rionlabs.tatsu.TatsuApp
import org.rionlabs.tatsu.data.AppDatabase
import org.rionlabs.tatsu.work.TimerController

object TimerModule {

    fun get() = module {
        single {
            TimerController(
                coroutineScope = (androidApplication() as TatsuApp).coroutineScope,
                timerDao = get<AppDatabase>().timerDao()
            )
        }
    }
}