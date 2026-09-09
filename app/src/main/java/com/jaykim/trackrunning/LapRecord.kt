package com.jaykim.trackrunning

import java.io.Serializable

// a single recorded lap on the stopwatch screen.
// lapTime is the split since the previous lap, totalTime is the cumulative elapsed
// time when the lap was recorded - both in the same "10ms tick" unit that
// Helper.intTimeToStr() expects.
data class LapRecord(
    val lapNumber: Int,
    val lapTime: Int,
    val totalTime: Int
) : Serializable
