package org.rionlabs.tatsu.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.rionlabs.tatsu.data.AppDatabase
import org.rionlabs.tatsu.ui.screen.main.MainViewModel
import org.rionlabs.tatsu.ui.screen.main.stats.StatsViewModel

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

        viewModel {
            StatsViewModel(
                timerDao = get<AppDatabase>().timerDao()
            )
        }
    }
}