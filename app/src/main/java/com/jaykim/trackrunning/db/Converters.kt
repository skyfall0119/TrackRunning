package com.jaykim.trackrunning.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jaykim.trackrunning.SingleRun
import kotlin.collections.ArrayList


class Converters {

    @TypeConverter
    fun listToJson(runs : ArrayList<SingleRun>) : String?{
        return Gson().toJson(runs)
    }

    @TypeConverter
    fun jsonToList(runs: String) : ArrayList<SingleRun> {
        val myType = object : TypeToken<ArrayList<SingleRun>>(){}.type
        return Gson().fromJson(runs, myType)
    }

}