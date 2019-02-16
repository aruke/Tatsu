package org.rionlabs.tatsu.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import org.rionlabs.tatsu.data.model.Timer

@Dao
interface TimerDao {

    @Query("SELECT * from timers WHERE :id=id")
    fun getWith(id: Long): Timer

    @Query("SELECT * from timers")
    fun getAll(): List<Timer>

    @Query("SELECT * from timers WHERE state='RUNNING' OR state='PAUSED'")
    fun getActive(): List<Timer>

    @Insert(onConflict = REPLACE)
    fun insert(timer: Timer): Long
}