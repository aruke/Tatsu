package org.rionlabs.tatsu.ui.screen.main.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.rionlabs.tatsu.data.model.Timer
import org.rionlabs.tatsu.data.model.TimerState.FINISHED
import org.rionlabs.tatsu.data.model.TimerType
import org.rionlabs.tatsu.utils.Utility
import org.rionlabs.tatsu.work.SettingsManager
import org.rionlabs.tatsu.work.TimerController
import timber.log.Timber

class TimerViewModel(
    private val timerController: TimerController,
    private val settingManager: SettingsManager
) : ViewModel() {

    private val _viewStateData = MutableLiveData<ViewState>()
    val viewStateData: LiveData<ViewState>
        get() = _viewStateData

    init {
        Timber.d("TimerViewModel.init() called")
        viewModelScope.launch {
            timerController.observeActiveTimer().collect {
                // Case when no timers are available
                if (it == Utility.NONE) {
                    val durationInSecs = settingManager.getWorkTimerInMinutes() * 60L
                    timerController.createNewTimer(TimerType.WORK, durationInSecs)
                    return@collect
                }
                _viewStateData.postValue(ViewState(it))
            }
        }
    }

    /*fun requestState(timerScreenState: TimerScreenState) {
        if (arrayOf(WORK_TIMER_FINISHED, BREAK_TIMER_FINISHED).contains(timerScreenState)) {
            throw IllegalStateException("Cannot request for $timerScreenState")
        }

        requestStateInternal(timerScreenState)
    }*/

    /*private fun requestStateInternal(timerScreenState: TimerScreenState) {
        val currentState = _timerScreenState.value ?: run {
            // Current state is null, no need to consider it
            when (timerScreenState) {
                WORK_TIMER_IDLE -> {
                    resetTimerToWorkIdle()
                }
                else -> {
                    _timerScreenState.postValue(timerScreenState)
                }
            }

            return
        }

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
                WORK_TIMER_IDLE -> {
                    pauseTimer()
                    _timerScreenState.postValue(WORK_TIMER_IDLE)
                }
                else -> throw IllegalStateException()
            }
            WORK_TIMER_IDLE -> when (timerScreenState) {
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
                BREAK_TIMER_IDLE -> {
                    pauseTimer()
                    _timerScreenState.postValue(BREAK_TIMER_IDLE)
                }
                else -> throw IllegalStateException()
            }
            BREAK_TIMER_IDLE -> when (timerScreenState) {
                WORK_TIMER_IDLE -> {
                    resetTimerToWorkIdle()
                }
                BREAK_TIMER_RUNNING -> {
                    resumeTimer()
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
    }*/

    //    private fun resetTimerToWorkIdle() {
//        mTimerData.value = Timer(
//            Calendar.getInstance(),
//            settingManager.getWorkTimerInMinutes() * 60L,
//            TimerType.WORK
//        )
//        _timerScreenState.postValue(WORK_TIMER_IDLE)
//    }

    override fun onCleared() {

    }

    data class ViewState(val timer: Timer) {

        val chipBlinking: Boolean = timer.isPaused

        val chipTextResId: Int = org.rionlabs.tatsu.R.string.app_name

        val workFinishedDialogShown: Boolean =
            (timer.type == TimerType.WORK && timer.state == FINISHED)

        val breakFinishedDialogShown: Boolean =
            (timer.type == TimerType.BREAK && timer.state == FINISHED)
    }
}
