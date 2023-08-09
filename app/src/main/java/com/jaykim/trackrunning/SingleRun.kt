package com.jaykim.trackrunning



import java.io.Serializable



// singleRun data 저장.

data class SingleRun (
    val isRest : Boolean,
    val distance : String,
    val set : Int,
    val lapTimeMin : String,
    val lapTimeSec : String,
    val lapTimeMillisec : String
):Serializable

//
