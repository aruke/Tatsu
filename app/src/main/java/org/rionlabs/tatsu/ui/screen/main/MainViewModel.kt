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
import org.rionlabs.tatsu.work.SilentModeManager
import timber.log.Timber

class MainViewModel(val app: Application) : AndroidViewModel(app) {

    private val timerController = (app as TatsuApp).timerController

    private val settingManager = SettingsManager(app)

    private val silentModeManager = SilentModeManager(app)

    private val mTimerData = MutableLiveData<Timer>()
    val timerData: LiveData<Timer> = mTimerData

    private val mStateData = MutableLiveData<TimerState>()
    val stateData: LiveData<TimerState> = mStateData

    private val metadataObserver = Observer<Timer> {
        it?.let { timer ->
            Timber.d("OnChanged called with NonNull Timer value")
            Timber.d("$timer")

            mTimerData.value = timer
            mStateData.value = timer.state

            if (timer.state == IDLE || timer.state == STOPPED || timer.state == CANCELLED) {
                resetTimerData()
            }
        }
    }

    init {
        if (timerController.isActiveTimerAvailable()) {
            timerController.getActiveTimer().observeForever(metadataObserver)
        } else {
            resetTimerData()
        }
    }

    fun startNewTimer() {
        val duration = settingManager.getWorkTimerInMinutes() * 60L
        timerController.startNewTimer(duration).observeForever(metadataObserver)

        if (settingManager.silentMode) {
            silentModeManager.turnOnSilentMode()
        }
    }

    fun pauseTimer() {
        timerController.pauseTimer()

        if (settingManager.silentMode) {
            silentModeManager.turnOffSilentMode()
        }
    }

    fun resumeTimer() {
        timerController.resumeTimer()

        if (settingManager.silentMode) {
            silentModeManager.turnOnSilentMode()
        }
    }

    fun cancelTimer() {
        timerController.cancelTimer()
        resetTimerData()

        if (settingManager.silentMode) {
            silentModeManager.turnOffSilentMode()
        }
    }

    private fun resetTimerData() {
        // TODO Create new Timer object here or handle state in UI side
        //settingManager.getWorkTimerInMinutes().toLong()
        mTimerData.value = Timer(System.currentTimeMillis(), settingManager.getWorkTimerInMinutes() * 60L)
        mStateData.value = IDLE
    }

    override fun onCleared() {
        if (timerController.isActiveTimerAvailable()) {
            timerController.getActiveTimer().removeObserver(metadataObserver)
        }
    }
}
