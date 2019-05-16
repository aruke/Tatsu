package org.rionlabs.tatsu.data

import androidx.room.TypeConverter
import org.rionlabs.tatsu.data.model.TimerState
import org.rionlabs.tatsu.data.model.TimerType

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

        @TypeConverter
        @JvmStatic
        fun toString(timerType: TimerType): String {
            return timerType.name
        }

        @TypeConverter
        @JvmStatic
        fun toTimerType(string: String): TimerType {
            return TimerType.valueOf(string)
        }
    }
}