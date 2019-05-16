package org.rionlabs.tatsu.ui.screen.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import org.rionlabs.tatsu.TatsuApp
import org.rionlabs.tatsu.data.model.Timer
import org.rionlabs.tatsu.data.model.TimerState.CANCELLED
import org.rionlabs.tatsu.data.model.TimerState.IDLE
import org.rionlabs.tatsu.data.model.TimerType
import org.rionlabs.tatsu.work.PreferenceManager
import org.rionlabs.tatsu.work.SettingsManager
import org.rionlabs.tatsu.work.SilentModeManager
import timber.log.Timber

class MainViewModel(val app: Application) : AndroidViewModel(app) {

    private val timerController = (app as TatsuApp).timerController

    private val settingManager = SettingsManager(app)

    private val silentModeManager = SilentModeManager(app)

    private val preferenceManager = PreferenceManager(app)

    private val mTimerData = MutableLiveData<Timer>()
    val timerData: LiveData<Timer> = mTimerData

    var timerType: TimerType = TimerType.WORK
        get() = preferenceManager.getTimerType()
        set(value) {
            field = value
            preferenceManager.setTimerType(value)
        }

    private val metadataObserver = Observer<Timer> {
        it?.let { timer ->
            Timber.d("OnChanged called with NonNull Timer value")
            Timber.d("$timer")

            mTimerData.value = timer

            if (timer.state == IDLE || timer.state == CANCELLED) {
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

    fun startNewWorkTimer() {
        val duration = settingManager.getWorkTimerInMinutes() * 60L
        timerController.startNewTimer(TimerType.WORK, duration).observeForever(metadataObserver)

        if (settingManager.silentMode) {
            silentModeManager.turnOnSilentMode()
        }
    }

    fun startNewBreakTimer() {
        val duration = settingManager.getBreakTimerInMinutes() * 60L
        timerController.startNewTimer(TimerType.BREAK, duration).observeForever(metadataObserver)
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

    fun resetTimerData() {
        val timerMinutes = when (timerType) {
            TimerType.WORK ->
                settingManager.getWorkTimerInMinutes()
            TimerType.BREAK ->
                settingManager.getBreakTimerInMinutes()
        }
        mTimerData.value = Timer(System.currentTimeMillis(), timerMinutes * 60L, timerType)
    }

    override fun onCleared() {
        if (timerController.isActiveTimerAvailable()) {
            timerController.getActiveTimer().removeObserver(metadataObserver)
        }
    }
}
