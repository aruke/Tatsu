package org.rionlabs.tatsu.utils

import org.rionlabs.tatsu.data.model.Timer
import org.rionlabs.tatsu.data.model.TimerType
import java.util.*

object Utility {

    private const val DELAY_SPEED = 0.1

    fun durationInMillis(durationInSecs: Long): Long {
        return durationInSecs * 1000 * DELAY_SPEED.toLong()
    }

    const val DELAY_INTERVAL_MILLIS = 100L

    val NONE = Timer(Calendar.getInstance(), 0, TimerType.WORK)
}
