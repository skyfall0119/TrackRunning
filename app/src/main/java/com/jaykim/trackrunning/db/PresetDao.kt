package com.jaykim.trackrunning.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface PresetDao {
    //get all
    @Query("SELECT * FROM RunsEntity")
    fun getAllPreset() : List<RunsEntity>

    // insert run
    @Insert
    fun insertPreset(runs : RunsEntity)

    // delete run
    @Delete
    fun deletePreset(runs : RunsEntity)
}