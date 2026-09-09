package com.jaykim.trackrunning.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface PresetDao {
    //get all
    @Query("SELECT * FROM PresetEntity ORDER BY id ASC")
    fun getAllPreset() : List<PresetEntity>

    // get single preset by id
    @Query("SELECT * FROM PresetEntity WHERE id = :id")
    fun getPresetById(id : Int) : PresetEntity?

    // insert run
    @Insert
    fun insertPreset(preset : PresetEntity)

    // delete run
    @Delete
    fun deletePreset(preset : PresetEntity)


    @Update
    fun updatePreset(preset : PresetEntity)
}