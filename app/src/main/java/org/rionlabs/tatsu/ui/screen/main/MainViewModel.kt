package org.rionlabs.tatsu.ui.screen.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import org.rionlabs.tatsu.TatsuApp
import org.rionlabs.tatsu.data.model.Timer
import org.rionlabs.tatsu.data.model.TimerState
import org.rionlabs.tatsu.data.model.TimerState.*
import org.rionlabs.tatsu.work.SettingsManager
import timber.log.Timber

class MainViewModel(val app: Application) : AndroidViewModel(app) {

    private val timerController = (app as TatsuApp).timerController

    private val settingManager = SettingsManager(app)

    private val durationData = MutableLiveData<Long>()

    private val stateData = MutableLiveData<TimerState>()

    private val metadataObserver = Observer<Timer> {
        it?.let { timer ->
            Timber.d("OnChanged called with NonNull Timer value")
            Timber.d("$timer")

            durationData.value = timer.duration
            stateData.value = timer.state

            if (timer.state ==
                IDLE || timer.state == STOPPED || timer.state == CANCELLED
            ) {
                durationData.value = settingManager.getWorkTimerInMinutes().toLong()
                stateData.value = IDLE
            }
        }
    }

    init {
        if (timerController.isActiveTimerAvailable()) {
            timerController.getActiveTimer().observeForever(metadataObserver)
        } else {
            durationData.value = settingManager.getWorkTimerInMinutes().toLong()
            stateData.value = IDLE
        }
    }

    fun startNewTimer() {
        val duration = settingManager.getWorkTimerInMinutes().toLong() * 60
        timerController.startNewTimer(duration).observeForever(metadataObserver)
    }

    fun pauseTimer() {
        timerController.pauseTimer()
    }

    fun resumeTimer() {
        timerController.resumeTimer()
    }

    fun cancelTimer() {
        timerController.cancelTimer()
        durationData.value = settingManager.getWorkTimerInMinutes().toLong()
        stateData.value = IDLE
    }

    fun getDuration(): LiveData<Long> {
        return durationData
    }

    fun getTimerState(): LiveData<TimerState> {
        return stateData
    }

    override fun onCleared() {

    }
}
