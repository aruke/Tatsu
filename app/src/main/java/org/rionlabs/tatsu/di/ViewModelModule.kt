package org.rionlabs.tatsu.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.rionlabs.tatsu.data.AppDatabase
import org.rionlabs.tatsu.ui.screen.main.MainViewModel

object ViewModelModule {

    fun get() = module {
        viewModel {
            MainViewModel(
                timerController = get(),
                settingManager = get(),
                silentModeManager = get(),
                vibrationsManager = get(),
                timerDao = get<AppDatabase>().timerDao()
            )
        }
    }
}