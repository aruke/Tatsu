package org.rionlabs.tatsu.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "timers")
data class Timer(
    @PrimaryKey(autoGenerate = true) val id: Long,
    /**
     * Start time.
     */
    @ColumnInfo(name = "start_time") val startTime: Calendar,
    /**
     * End time.
     */
    @ColumnInfo(name = "end_time") val endTime: Calendar,
    /**
     * End time in seconds.
     */
    @ColumnInfo(name = "remaining_secs") val remainingSecs: Long,
    /**
     * Non changeable duration in seconds.
     */
    @ColumnInfo(name = "duration_secs") val durationSecs: Long,
    /**
     * Either of [TimerState]
     */
    @ColumnInfo(name = "state") val state: TimerState,
    /**
     * Either of [TimerType]
     */
    @ColumnInfo(name = "type") val type: TimerType
) {
    @Ignore
    constructor(startTime: Calendar, duration: Long, type: TimerType) :
            this(
                0,
                startTime,
                startTime.apply { add(Calendar.SECOND, duration.toInt()) },
                duration,
                duration,
                TimerState.IDLE,
                type
            )

    @Ignore
    val seconds = remainingSecs % 60

    @Ignore
    val minutes = (remainingSecs / 60) % 60

    @Ignore
    val hours = remainingSecs / 3600

    @Ignore
    fun completionPercent(): Float {
        val totalDurationSec = (endTime.timeInMillis - startTime.timeInMillis) / 1000
        return remainingSecs.toFloat() / totalDurationSec * 100
    }
}