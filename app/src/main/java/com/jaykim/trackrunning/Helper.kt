package com.jaykim.trackrunning


object Helper {

    //QuickStart NumberPicker items
    val qsDist = arrayOf("100", "200", "400", "800", "1200", "1600", "2000")
    val qsRest = arrayOf("30s", "1m", "1m 30s", "2m", "3m", "4m", "5m")


    //convert breakTime in string to Integer for the calculation
    fun breakToInt(s:String) : Int {
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



    fun stringTimeToInt(min : String, sec : String, ms : String) : Int {

        val minInt = min.toInt()
        val secInt = sec.substring(1,3).toInt()
        val msInt = ms.substring(1,3).toInt()

        return ((minInt*60000) + (secInt*1000) + msInt*10)
    }


    fun getTotalTime(runs : ArrayList<SingleRun>) : String{
        var totalTime = 0
        runs.forEach {
            if (!it.isRest) totalTime += stringTimeToInt(it.min, it.sec, it.millisec)
        }


        println("breakpoint : getTotalTime Int $totalTime")
        val minute = (totalTime / 60000)
        val second = (totalTime % 60000) / 1000
        val ms = (totalTime / 10) % 100

        val minStr = if (minute < 10) "0${minute}" else "$minute"
        val msStr =if (ms < 10) "0${ms}" else "$ms"
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