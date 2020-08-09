package org.rionlabs.tatsu.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.res.Resources
import android.os.Build
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

fun AlarmManager.setExactCompat(type: Int, triggerAtMillis: Long, operation: PendingIntent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        setExactAndAllowWhileIdle(type, triggerAtMillis, operation)
    } else {
        setExact(type, triggerAtMillis, operation)
    }
}