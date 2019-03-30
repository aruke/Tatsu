package org.rionlabs.tatsu.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "timers")
data class Timer(
    @PrimaryKey(autoGenerate = true) val id: Long,
    /**
     * Start time in form of epoch time.
     */
    @ColumnInfo(name = "start_time") val startTime: Long,
    /**
     * End time in form of epoch time.
     */
    @ColumnInfo(name = "end_time") val endTime: Long,
    /**
     * End time in seconds.
     */
    @ColumnInfo(name = "duration") val duration: Long,
    /**
     * Either of [TimerState]
     */
    @ColumnInfo(name = "state") val state: TimerState
) {
    @Ignore
    constructor(startTime: Long, duration: Long) : this(
        0,
        startTime,
        (startTime + duration), duration,
        TimerState.IDLE
    )

    @Ignore
    val seconds = duration % 60

    @Ignore
    val minutes = (duration / 60) % 60

    @Ignore
    val hours = duration / 3600
}