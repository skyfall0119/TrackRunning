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
                appDatabase = Room.databaseBuilder(context,
                    AppDatabase::class.java,
                    databaseName)
                    .fallbackToDestructiveMigration()
                    .build()
            }

            return appDatabase
        }
    }

}