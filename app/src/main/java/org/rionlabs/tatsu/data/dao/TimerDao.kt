package org.rionlabs.tatsu.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import org.rionlabs.tatsu.data.model.Timer

@Dao
interface TimerDao {

    @Query("SELECT * from timers")
    fun getAll(): List<Timer>

    @Insert(onConflict = REPLACE)
    fun insert(weatherData: Timer)
}