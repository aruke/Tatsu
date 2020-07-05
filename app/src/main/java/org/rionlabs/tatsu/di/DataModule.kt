package org.rionlabs.tatsu.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.rionlabs.tatsu.data.AppDatabase

object DataModule {

    fun get() = module {
        single {
            AppDatabase.getInstance(androidContext())
        }
    }
}