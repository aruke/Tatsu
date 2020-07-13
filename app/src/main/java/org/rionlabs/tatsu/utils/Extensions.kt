package org.rionlabs.tatsu.utils

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import org.rionlabs.tatsu.data.model.Timer
import org.rionlabs.tatsu.data.model.TimerState
import java.util.*

/**
 * Updates the [mutableLiveData] with options, only if the original value is NonNull
 */
fun Timer.updateLiveData(
    mutableLiveData: MutableLiveData<Timer>,
    remainingSecs: Long = this.remainingSecs,
    state: TimerState = this.state,
    startTime: Calendar = this.startTime,
    endTime: Calendar = this.endTime
) {

    val oldTimer = mutableLiveData.value
    oldTimer?.let {
        mutableLiveData.value =
            it.copy(
                remainingSecs = remainingSecs,
                state = state,
                startTime = startTime,
                endTime = endTime
            )
    }
}

fun Timer.isActive(): Boolean {
    return state == TimerState.RUNNING || state == TimerState.PAUSED
}

val Int.dp: Float
    get() = (this * Resources.getSystem().displayMetrics.density)

val Int.px: Float
    get() = (this / Resources.getSystem().displayMetrics.density)