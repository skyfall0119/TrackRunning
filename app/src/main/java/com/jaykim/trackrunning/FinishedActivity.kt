package com.jaykim.trackrunning

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaykim.trackrunning.databinding.ActivityFinishedBinding
import com.jaykim.trackrunning.db.AppDatabase
import com.jaykim.trackrunning.db.RunsDao
import com.jaykim.trackrunning.db.RunsEntity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //view binding
        binding = ActivityFinishedBinding.inflate(layoutInflater)
        setContentView(binding.root)




        getRunData()
        initDb()
        initRecyclerView()
        initBtn()
        calcData()


        }

    private fun initBtn() {
        binding.btnFinishedBacktomenu.setOnClickListener{
            finish()
        }
    }

    private fun getRunData() {
        runData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("runData",ArrayList<SingleRun>()::class.java)!!
        } else {
            intent.getSerializableExtra("runData") as ArrayList<SingleRun>
        }
    }

    private fun calcData() {

        //TODO : calculate fastest laptime and average laptime when Item is selected.

        thread {
//            val allRuns : List<RunsEntity> = runsDao.getAllRuns()
//            println("breakPoint databaseCheck: getAllRuns")
//            val mondayRun = allRuns[0].singleWorkout
//            println("breakPoint databaseCheck: retrieve data ${mondayRun.get(0).distance}")
        }



    }

    private fun initRecyclerView() {
        adapter = FinishedActivityRvAdapter(runData)
        binding.finishedRv.adapter = adapter
        binding.finishedRv.layoutManager = LinearLayoutManager(this)
    }


    private fun initDb() {
        Thread{
            db = AppDatabase.getInstance(this)!!
            runsDao = db.getRunsDao()

            //TODO : get today date and save it to the title
//            runsDao.insertRuns(RunsEntity(null,"8/12/23 Running", runData))

            //TODO : delete this testcode
//            val allRuns : List<RunsEntity> = runsDao.getAllRuns()
//            println("breakPoint databaseCheck: getAllRuns - ${allRuns.size}")
//            val mondayRun : ArrayList<SingleRun>  = allRuns[0].singleWorkout
//            println("breakPoint databaseCheck: getAllRuns done ")
//            println("breakPoint: mondayRun ${mondayRun}")
//            println("breakPoint: mondayRun[0] type name ${mondayRun[0].javaClass.typeName}")
//            println("breakPoint: mondayRun[0] type name ${mondayRun[0].javaClass.name}")
//            println("breakPoint databaseCheck: mondayRun distance ${mondayRun[0].distance}")
        }.start()
    }

}