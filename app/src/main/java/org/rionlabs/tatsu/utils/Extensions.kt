package org.rionlabs.tatsu.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.res.Resources
import android.os.Build
import androidx.lifecycle.MutableLiveData
import org.rionlabs.tatsu.data.model.Timer
import org.rionlabs.tatsu.data.model.TimerState
import java.util.*

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