package com.jaykim.trackrunning.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jaykim.trackrunning.SingleRun
import java.util.ArrayList


@Entity
data class RunsEntity(
    @PrimaryKey(autoGenerate = true) var id : Int? = null,
    @ColumnInfo(name="date") var date : String,
    @ColumnInfo(name="time") var time : String,
    @ColumnInfo(name="title") var title : String,
    @ColumnInfo(name="totalDist") var totalDist : String,
    @ColumnInfo(name="totalTime") var totalTime : String,
    @ColumnInfo(name="singleWorkout") var singleWorkout : ArrayList<SingleRun>
)