package org.rionlabs.tatsu.data.model

import androidx.room.DatabaseView

@DatabaseView(
    """
    SELECT * FROM 
    (SELECT COUNT(*) AS daySessionCount, SUM(duration_secs) AS dayDurationSecs 
        FROM timers 
        WHERE date('now') == date(end_time) 
        AND type=='WORK' AND state=='FINISHED'
    ),
    (SELECT COUNT(*)  AS weekSessionCount, SUM(duration_secs) AS weekDurationSecs 
        FROM timers 
        WHERE end_time BETWEEN date('now', '-7 days') AND date('now') 
        AND type=='WORK' AND state=='FINISHED'
    )
    """
)
data class StatsMeta(
    val daySessionCount: Int,
    val dayDurationSecs: Int,
    val weekSessionCount: Int,
    val weekDurationSecs: Int
)