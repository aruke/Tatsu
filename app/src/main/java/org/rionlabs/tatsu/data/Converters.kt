package org.rionlabs.tatsu.data

import androidx.room.TypeConverter
import org.rionlabs.tatsu.data.model.TimerState
import org.rionlabs.tatsu.data.model.TimerType
import java.text.SimpleDateFormat
import java.util.*

class Converters {

    companion object {

        // https://www.sqlite.org/lang_datefunc.html
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US)

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

        @TypeConverter
        @JvmStatic
        fun toString(calender: Calendar): String {
            return dateFormat.format(Date(calender.timeInMillis))
        }

        @TypeConverter
        @JvmStatic
        fun toDate(string: String): Calendar {
            val calender = Calendar.getInstance()
            calender.time = dateFormat.parse(string)!!
            return calender
        }
    }
}