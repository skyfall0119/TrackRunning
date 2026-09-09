package com.jaykim.trackrunning.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RunsDao {

    //get all
    @Query("SELECT * FROM RunsEntity ORDER BY id ASC")
    fun getAllRuns() : List<RunsEntity>

    // get single run by id
    @Query("SELECT * FROM RunsEntity WHERE id = :id")
    fun getRunById(id : Int) : RunsEntity?

    // insert run
    @Insert
    fun insertRuns(runs : RunsEntity)

    // delete run
    @Delete
    fun deleteRuns(runs : RunsEntity)
}