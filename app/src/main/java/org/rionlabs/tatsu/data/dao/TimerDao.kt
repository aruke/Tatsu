package org.rionlabs.tatsu.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import org.rionlabs.tatsu.data.model.StatsMeta
import org.rionlabs.tatsu.data.model.Timer

@Dao
interface TimerDao {

    @Query("SELECT * from timers WHERE :id=id")
    fun getWith(id: Long): Timer

    @Query("SELECT * from timers ORDER BY start_time DESC")
    fun getAll(): LiveData<List<Timer>>

    @Query("SELECT * from timers WHERE state='RUNNING' OR state='PAUSED'")
    fun getActive(): List<Timer>

    @Insert(onConflict = REPLACE)
    fun insert(timer: Timer): Long

    @Query("SELECT * FROM StatsMeta LIMIT 1")
    fun getStatsMeta(): StatsMeta
}