package org.rionlabs.tatsu.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "timers")
data class Timer(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "start_time") val startTime: Date,
    @ColumnInfo(name = "end_time") val endTime: Date,
    @ColumnInfo(name = "duration") val duration: Long,
    @ColumnInfo(name = "state") val state: TimerState
) {
    @Ignore
    constructor(startTime: Date, duration: Long) : this(
        0,
        startTime,
        Date(startTime.time + duration * 1000), duration,
        TimerState.IDLE
    )
}