package com.jaykim.trackrunning.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    entities = arrayOf(RunsEntity::class, PresetEntity::class),
    version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){

    abstract fun getRunsDao() : RunsDao
    abstract fun getPresetDao() : PresetDao


    companion object{
        val databaseName = "db"
        var appDatabase : AppDatabase? = null

        fun getInstance(context: Context) : AppDatabase? {
            if(appDatabase == null){
                // No fallbackToDestructiveMigration(): user data (presets, run history) must
                // survive schema changes. Any future version bump requires an explicit
                // Migration added here, otherwise Room throws instead of silently wiping data.
                appDatabase = Room.databaseBuilder(context,
                    AppDatabase::class.java,
                    databaseName)
                    .build()
            }

            return appDatabase
        }
    }

}