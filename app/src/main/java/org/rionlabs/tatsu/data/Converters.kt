package org.rionlabs.tatsu.data

import androidx.room.TypeConverter
import org.rionlabs.tatsu.data.model.TimerState

class Converters {

    companion object {

        @TypeConverter
        @JvmStatic
        fun toString(timerState: TimerState): String {
            return timerState.name
        }

        @TypeConverter
        @JvmStatic
        fun toTimerState(string: String): TimerState {
            return TimerState.valueOf(string)
        }
    }
}