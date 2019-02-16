package org.rionlabs.tatsu.utils

import androidx.lifecycle.MutableLiveData
import org.rionlabs.tatsu.data.model.Timer
import org.rionlabs.tatsu.data.model.TimerState

fun Timer.updateLiveData(
    mutableLiveData: MutableLiveData<Timer>,
    duration: Long = this.duration,
    state: TimerState = this.state,
    startTime: Long = this.startTime,
    endTime: Long = this.endTime
) {

    val oldTimer = mutableLiveData.value
    oldTimer?.let {
        mutableLiveData.value = it.copy(duration = duration, state = state, startTime = startTime, endTime = endTime)
    }
}

fun Timer.isActive(): Boolean {
    return state == TimerState.RUNNING || state == TimerState.PAUSED
}