package com.jaykim.trackrunning


import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaykim.trackrunning.databinding.ActivityFinishedBinding
import com.jaykim.trackrunning.db.AppDatabase
import com.jaykim.trackrunning.db.RunsDao
import com.jaykim.trackrunning.db.RunsEntity
import java.time.DayOfWeek

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import kotlin.concurrent.thread


// shows the finished screen after RunActivity.
// get run data from RunActivity (intent) and show it on the recyclerView.
// save the run data to run database.
// button to go back to main menu.
class FinishedActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFinishedBinding

    private lateinit var db : AppDatabase
    private lateinit var runsDao : RunsDao
    private lateinit var adapter : FinishedActivityRvAdapter
    private lateinit var runData : ArrayList<SingleRun>
    private lateinit var curDate : String
    private lateinit var curTime : String
    private lateinit var totalTime : String
    private lateinit var totalDist : String
    private lateinit var curDay : DayOfWeek

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //view binding
        binding = ActivityFinishedBinding.inflate(layoutInflater)
        setContentView(binding.root)



        initBtn()
        getData()
        initDb()
        initView()


        }

    private fun initBtn() {
        binding.btnFinishedBacktomenu.setOnClickListener{
            finish()
        }
    }

    override fun onBackPressed() {
        finish()
    }

    private fun getData() {
        runData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("runData",ArrayList<SingleRun>()::class.java)!!
        } else {
            intent.getSerializableExtra("runData") as ArrayList<SingleRun>
        }

        curDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yy/MM/dd"))
        curTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        totalTime = Helper.getTotalTime(runData)
        totalDist = Helper.getTotalDist(runData)

        curDay = LocalDate.now().dayOfWeek
    }


    private fun initView() {
        adapter = FinishedActivityRvAdapter(runData)
        binding.finishedRv.adapter = adapter
        binding.finishedRv.layoutManager = LinearLayoutManager(this)

        //update the UI text
        binding.finishedTitle.text = "$curDay RUN"
        binding.finishedDate.text = "$curDate $curTime"
        binding.tvTotalDist2.text = totalDist
        binding.tvTotalTime2.text = totalTime
    }


    //save finished run data to the database with current time
    private fun initDb() {
        Thread{
            db = AppDatabase.getInstance(this)!!
            runsDao = db.getRunsDao()

            runsDao.insertRuns(RunsEntity(
                null,curDate, curTime, "$curDay RUN", totalDist, totalTime, runData))

        }.start()
    }

}