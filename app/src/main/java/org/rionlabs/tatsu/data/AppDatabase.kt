package org.rionlabs.tatsu.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.rionlabs.tatsu.data.dao.TimerDao
import org.rionlabs.tatsu.data.model.Timer

@Database(entities = [Timer::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun timerDao(): TimerDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                synchronized(AppDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "tatsu.db"
                    ).build()
                }
            }
            return instance!!
        }
    }
}