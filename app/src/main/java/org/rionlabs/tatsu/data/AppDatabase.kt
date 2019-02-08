package org.rionlabs.tatsu.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.rionlabs.tatsu.data.dao.TimerDao
import org.rionlabs.tatsu.data.model.Timer

@Database(entities = [Timer::class], version = DBConstants.VERSION)
@TypeConverters(value = [Converters::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun timerDao(): TimerDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            val appContext = context.applicationContext
            if (instance == null) {
                synchronized(AppDatabase::class) {
                    instance = Room
                        .databaseBuilder(appContext, AppDatabase::class.java, DBConstants.NAME)
                        .build()
                }
            }
            return instance!!
        }
    }
}