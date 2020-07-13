package org.rionlabs.tatsu.ui.screen.main.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import org.rionlabs.tatsu.data.model.Timer
import org.rionlabs.tatsu.data.model.TimerState.*
import org.rionlabs.tatsu.data.model.TimerType
import org.rionlabs.tatsu.ui.screen.main.timer.TimerScreenState.*
import org.rionlabs.tatsu.work.SettingsManager
import org.rionlabs.tatsu.work.SilentModeManager
import org.rionlabs.tatsu.work.TimerController
import org.rionlabs.tatsu.work.VibrationsManager
import org.rionlabs.tatsu.work.VibrationsManager.VibeType
import timber.log.Timber
import java.util.*

class TimerViewModel(
    private val timerController: TimerController,
    private val settingManager: SettingsManager,
    private val silentModeManager: SilentModeManager,
    private val vibrationsManager: VibrationsManager
) : ViewModel() {

    private val mTimerData = MutableLiveData<Timer>()
    val timerData: LiveData<Timer> = mTimerData

    private val _timerScreenState = MutableLiveData<TimerScreenState>()
    val timerScreenState: LiveData<TimerScreenState>
        get() = _timerScreenState

    private val metadataObserver = Observer<Timer> {
        it?.let { timer ->
            Timber.d("OnChanged called with NonNull Timer value")
            Timber.d("$timer")

            mTimerData.value = timer

            when (timer.state) {
                IDLE -> {
                    requestStateInternal(WORK_TIMER_IDLE)
                }
                RUNNING -> {
                    // NA
                }
                PAUSED -> {
                    // NA
                }
                FINISHED -> {
                    if (timer.type == TimerType.WORK) {
                        requestStateInternal(WORK_TIMER_FINISHED)
                    } else {
                        requestStateInternal(BREAK_TIMER_FINISHED)
                    }
                }
                CANCELLED -> {
                    requestStateInternal(WORK_TIMER_IDLE)
                }
            }
        }
    }


    init {
        if (timerController.isActiveTimerAvailable()) {
            timerController.getActiveTimer().observeForever(metadataObserver)

        } else {
            resetTimerToWorkIdle()
        }
    }


    fun requestState(timerScreenState: TimerScreenState) {
        if (arrayOf(WORK_TIMER_FINISHED, BREAK_TIMER_FINISHED).contains(timerScreenState)) {
            throw IllegalStateException("Cannot request for $timerScreenState")
        }

        requestStateInternal(timerScreenState)
    }

    private fun requestStateInternal(timerScreenState: TimerScreenState) {
        val currentState = _timerScreenState.value ?: return
        Timber.d("Current state is $currentState, switching to $timerScreenState")

        when (currentState) {
            WORK_TIMER_IDLE -> when (timerScreenState) {
                WORK_TIMER_RUNNING -> {
                    startNewWorkTimer()
                    _timerScreenState.postValue(WORK_TIMER_RUNNING)
                }
                else -> throw IllegalStateException()
            }
            WORK_TIMER_RUNNING -> when (timerScreenState) {
                WORK_TIMER_FINISHED -> {
                    _timerScreenState.postValue(WORK_TIMER_FINISHED)
                    vibrationsManager.vibrate(VibeType.WORK_FINISHED)
                }
                WORK_TIMER_PAUSED -> {
                    pauseTimer()
                    _timerScreenState.postValue(WORK_TIMER_PAUSED)
                }
                else -> throw IllegalStateException()
            }
            WORK_TIMER_PAUSED -> when (timerScreenState) {
                WORK_TIMER_IDLE -> {
                    resetTimerToWorkIdle()
                }
                WORK_TIMER_RUNNING -> {
                    resumeTimer()
                    _timerScreenState.postValue(WORK_TIMER_RUNNING)
                }
                else -> throw IllegalStateException()
            }
            WORK_TIMER_FINISHED -> when (timerScreenState) {
                WORK_TIMER_IDLE -> {
                    resetTimerToWorkIdle()
                }
                BREAK_TIMER_RUNNING -> {
                    startNewBreakTimer()
                    _timerScreenState.postValue(BREAK_TIMER_RUNNING)
                }
                else -> throw IllegalStateException()
            }
            BREAK_TIMER_RUNNING -> when (timerScreenState) {
                BREAK_TIMER_FINISHED -> {
                    _timerScreenState.postValue(BREAK_TIMER_FINISHED)
                    vibrationsManager.vibrate(VibeType.BREAK_FINISHED)
                }
                BREAK_TIMER_PAUSED -> {
                    pauseTimer()
                    _timerScreenState.postValue(BREAK_TIMER_PAUSED)
                }
                else -> throw IllegalStateException()
            }
            BREAK_TIMER_PAUSED -> when (timerScreenState) {
                WORK_TIMER_IDLE -> {
                    resetTimerToWorkIdle()
                }
                BREAK_TIMER_RUNNING -> {
                }
                else -> throw IllegalStateException()
            }
            BREAK_TIMER_FINISHED -> when (timerScreenState) {
                WORK_TIMER_IDLE -> {
                    resetTimerToWorkIdle()
                }
                WORK_TIMER_RUNNING -> {
                    startNewWorkTimer()
                    _timerScreenState.postValue(WORK_TIMER_RUNNING)
                }
                else -> throw IllegalStateException()
            }
        }
    }

    private fun startNewWorkTimer() {
        val duration = settingManager.getWorkTimerInMinutes() * 60L
        timerController.startNewTimer(TimerType.WORK, duration).observeForever(metadataObserver)

        if (settingManager.silentMode) {
            silentModeManager.turnOnSilentMode()
        }
    }

    private fun startNewBreakTimer() {
        val duration = settingManager.getBreakTimerInMinutes() * 60L
        timerController.startNewTimer(TimerType.BREAK, duration).observeForever(metadataObserver)
    }

    private fun pauseTimer() {
        timerController.pauseTimer()

        if (settingManager.silentMode) {
            silentModeManager.turnOffSilentMode()
        }
    }

    private fun resumeTimer() {
        timerController.resumeTimer()

        if (settingManager.silentMode) {
            silentModeManager.turnOnSilentMode()
        }
    }

    private fun cancelTimer() {
        timerController.cancelTimer()
        resetTimerToWorkIdle()

        if (settingManager.silentMode) {
            silentModeManager.turnOffSilentMode()
        }
    }

    private fun resetTimerToWorkIdle() {
        mTimerData.value = Timer(
            Calendar.getInstance(),
            settingManager.getWorkTimerInMinutes() * 60L,
            TimerType.WORK
        )
        _timerScreenState.postValue(WORK_TIMER_IDLE)
    }

    override fun onCleared() {
        if (timerController.isActiveTimerAvailable()) {
            timerController.getActiveTimer().removeObserver(metadataObserver)
        }
    }
}
