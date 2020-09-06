package org.rionlabs.tatsu.di

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.rionlabs.tatsu.data.AppDatabase
import org.rionlabs.tatsu.ui.dialog.feedback.FeedbackViewModel
import org.rionlabs.tatsu.ui.screen.main.stats.StatsViewModel
import org.rionlabs.tatsu.ui.screen.main.timer.TimerViewModel

object ViewModelModule {

    fun get() = module {
        viewModel {
            TimerViewModel(
                timerController = get(),
                settingManager = get()
            )
        }

        viewModel {
            StatsViewModel(
                timerDao = get<AppDatabase>().timerDao()
            )
        }

        viewModel {
            FeedbackViewModel(androidApplication())
        }
    }
}