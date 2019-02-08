package org.rionlabs.tatsu.data

import androidx.room.TypeConverter
import org.rionlabs.tatsu.data.model.TimerState

class Converters {

    companion object {

        @TypeConverter
        @JvmStatic
        fun toInt(timerState: TimerState): Int {
            return when (timerState) {
                TimerState.IDLE -> 0
                TimerState.RUNNING -> 1
                TimerState.FINISHED -> 2
                TimerState.CANCELLED -> 3
            }
        }

        @TypeConverter
        @JvmStatic
        fun toTimerState(int: Int): TimerState {
            return when (int) {
                0 -> TimerState.IDLE
                1 -> TimerState.RUNNING
                2 -> TimerState.FINISHED
                3 -> TimerState.CANCELLED
                else -> {
                    throw IllegalStateException()
                }
            }
        }
    }
}