package com.jaykim.trackrunning

import com.jaykim.trackrunning.db.RunsEntity

object Helper {

    //QuickStart NumberPicker items
    val qsDist = arrayOf("100", "200", "400", "800", "1200", "1600", "2000")
    val qsRest = arrayOf("30s", "1m", "1m 30s", "2m", "3m", "4m", "5m")


    //convert breaktime in string to Integer for the calculation
    fun BreakToInt(s:String) : Int {
        val time = when (s) {
            "30s" -> 30000
            "1m" -> 60000
            "1m 30s" -> 90000
            "2m" -> 120000
            "3m" -> 180000
            "4m" -> 240000
            "5m" -> 300000
            else -> 5000
        }
        return time
    }



    fun StringTimeToInt(min : String, sec : String, millisec : String) : Int {
        var time = 0

        println("breakpoint : strTimeToInt min $min")
        println("breakpoint : strTimeToInt sec $sec")
        println("breakpoint : strTimeToInt ms $millisec")

        var minInt = min.toInt()
        var secInt = sec.substring(1,3).toInt()
        var millisecInt = millisec.substring(1,3).toInt()

        time = (minInt * 60000) + (secInt * 1000) + millisecInt * 10

        println("breakpoint : strTimeToInt minInt : $minInt")
        println("breakpoint : strTimeToInt secInt $secInt")
        println("breakpoint : strTimeToInt msInt $millisecInt")


        println("breakpoint : strTimeToInt return $time")

        return time
    }


    fun getTotalTime(runs : ArrayList<SingleRun>) : String{
        var totalTime = 0
        runs.forEach {
            if (!it.isRest) totalTime += StringTimeToInt(it.min, it.sec, it.millisec)
        }


        println("breakpoint : getTotalTime Int $totalTime")
        val minute = (totalTime / 60000)
        val second = (totalTime % 60000) / 1000
        val millisec = (totalTime / 10) % 100

        val minStr = if (minute < 10) "0${minute}" else "$minute"
        val msStr =if (millisec < 10) "0${millisec}" else "$millisec"
        val secStr= if (second < 10) "0${second}" else "$second"

        return "$minStr:$secStr.$msStr"
    }

    fun getTotalDist(runs : ArrayList<SingleRun>) : String{
        var totalDist = 0
        runs.forEach {
            if (!it.isRest) totalDist += it.distance.toInt()
        }

        val s = totalDist.toString()
        val r =
            if (totalDist >= 1000) {
                "${s.substring(0,s.lastIndex-2)},${s.substring(s.lastIndex-2)}"
            }
        else s
        return "$r m"
    }



}