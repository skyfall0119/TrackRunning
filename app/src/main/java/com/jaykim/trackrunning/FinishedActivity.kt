package com.jaykim.trackrunning



import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaykim.trackrunning.databinding.ActivityFinishedBinding
import com.jaykim.trackrunning.db.AppDatabase
import com.jaykim.trackrunning.db.RunsDao
import com.jaykim.trackrunning.db.RunsEntity

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter



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
    private var runDataMap = mutableMapOf<String, Array<String>>()
    private lateinit var curDate : String
    private lateinit var curTime : String
    private lateinit var totalTime : String
    private lateinit var totalDist : String
    private lateinit var curDay : String
    // true while the DB insert is still in flight; blocks leaving the screen so the
    // record can't be silently dropped by an early back-press
    private var isSaving = true
    private var saveFailed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //view binding
        binding = ActivityFinishedBinding.inflate(layoutInflater)
        setContentView(binding.root)



        initBtn()
        getData()
        initDb()
        calcData()
        initView()


        }

    private fun initBtn() {
        binding.btnFinishedBacktomenu.setOnClickListener{
            finishIfSaved()
        }
    }

    override fun onBackPressed() {
        finishIfSaved()
    }

    // leaving before the save finishes would silently drop the workout record
    private fun finishIfSaved() {
        if (isSaving) {
            Toast.makeText(this, getString(R.string.finished_saving_wait), Toast.LENGTH_SHORT).show()
            return
        }
        finish()
    }

    private fun getData() {
        runData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("runData",ArrayList<SingleRun>()::class.java)!!
        } else {
            intent.getSerializableExtra("runData") as ArrayList<SingleRun>
        }

        curDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yy/MM/dd"))
//        curDay = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE", resources.configuration.locale))
        curTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        totalTime = Helper.getTotalTime(runData)
        totalDist = Helper.getTotalDist(runData)



        curDay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            LocalDate.now().format(DateTimeFormatter
                .ofPattern("EEEE", resources.configuration.locales[0]))

         else
            @Suppress("DEPRECATION")
            LocalDate.now().format(DateTimeFormatter
                .ofPattern("EEEE", resources.configuration.locale))





    }


    private fun initView() {
        adapter = FinishedActivityRvAdapter(runData)
        binding.finishedRv.adapter = adapter
        binding.finishedRv.layoutManager = LinearLayoutManager(this)

        //update the UI text
        binding.finishedTitle.text = "$curDay ${getString(R.string.finished_title_run)}"
        binding.finishedDate.text = "$curDate $curTime"
        binding.tvTotalDist2.text = totalDist
        binding.tvTotalTime2.text = totalTime

        adapter.setOnItemClickListener(object : ActivitiesRvAdapter.onItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                adapter.selectedPos = position
                adapter.notifyDataSetChanged()

                binding.tvFastestLaptime2.text = runDataMap[runData[position].distance]?.get(0)
                binding.tvAvgLaptime2.text = runDataMap[runData[position].distance]?.get(1)
            }
        })
    }

    private fun calcData() {
        val sortData = mutableMapOf<String,ArrayList<Int>>()

        //sort by distance, excluding rest entries (0 distance/time would skew the stats)
        for (singleRun in runData) {
            if (singleRun.isRest) continue
            if (sortData.containsKey(singleRun.distance)) {
                sortData[singleRun.distance]?.add(singleRun.msTime)
            }else{
                val arl = ArrayList<Int>()
                arl.add(singleRun.msTime)
                sortData[singleRun.distance] = arl
            }
        }

        sortData.keys.forEach {key->
            var sum = 0
            var fastest = sortData[key]!![0]
            for (e in sortData[key]!!)  {
                sum += e
                if (e < fastest ) fastest = e
            }
            val avg = sum / sortData[key]!!.size
            runDataMap[key] = arrayOf(Helper.intTimeToStr(fastest),Helper.intTimeToStr(avg))
        }
    }


    //save finished run data to the database with current time
    private fun initDb() {
        Thread{
            try {
                db = AppDatabase.getInstance(this)!!
                runsDao = db.getRunsDao()

                runsDao.insertRuns(RunsEntity(
                    null,curDate, curTime, "$curDay ${getString(R.string.finished_title_run)}",
                    totalDist, totalTime, runData))
            } catch (e: Exception) {
                saveFailed = true
            } finally {
                isSaving = false
                runOnUiThread {
                    if (saveFailed) {
                        Toast.makeText(this, getString(R.string.finished_save_failed), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }.start()
    }

}