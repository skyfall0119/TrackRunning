package com.jaykim.trackrunning



import java.io.Serializable



// singleRun data 저장.

data class SingleRun (
    val isRest : Boolean,
    val distance : String,
//    val set : Int,
    val breakPick : String,
    var min : String = "0",
    var sec : String = ":00",
    var millisec : String = ".00",
    var isDone : Boolean = false
):Serializable

