package com.jaykim.trackrunning

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaykim.trackrunning.databinding.ActivityRunBinding
import java.util.Timer
import kotlin.concurrent.timer

class RunActivity : AppCompatActivity(){

    private lateinit var binding : ActivityRunBinding
    private lateinit var adapter :RunActivityRvAdapter
    private lateinit var runData : ArrayList<SingleRun>
    private var isRunning = false
    private var duringBreak = false
    private var rvPos = 0
    private var timer : Timer? = null
    private var cdTimer : CountDownTimer? = null
    private var time = 0


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRunBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initRecycler()
        initBtn()


    }


    private fun initBtn() {

        val btnStart = binding.btnStart
        val btnStop = binding.btnStop


        btnStart.setOnClickListener {
            if (!duringBreak){ //during break, disable start button
                if (isRunning){ //while running.
                    //change btn to start.
                    Log.d("ButtonCheck", "rvPos : $rvPos  //  1. while running")
                    timer?.cancel()
                    // record the time -- posUpdate
                    binding.btnStart.text = getString(R.string.run_btn_break)
                    rvPosUpdate()
                    breakTimer(runData[rvPos].breakPick)



                    //if last it was the last run. move to  finishedActivity
                    if (runData.size == rvPos+1) finishWorkout()

                } else { //first time starting, after break. after pause
                    // start the timer.
                    runTimer()
                    //change btn to record
                    btnStart.text = getString(R.string.run_btn_record)
                    isRunning = true

                }
            }
        }
        //long click. pop dialog. ask if want to finish the workout.
        //if yes, exit out. save RunData to runDataBase
        btnStop.setOnLongClickListener {

            //TODO : popup dialog, ask if user wants to end.

            finishWorkout()

            return@setOnLongClickListener true
        }

        //pause
        binding.btnStop.setOnClickListener {
            timer?.cancel()
            btnStart.text = getString(R.string.run_btn_start)
            isRunning = false
        }
    }

    private fun initRecycler() {

        runData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("runData",ArrayList<SingleRun>()::class.java)!!
        } else {
            intent.getSerializableExtra("runData") as ArrayList<SingleRun>
        }
        runOnUiThread{
            adapter = RunActivityRvAdapter(runData)
            binding.rv.adapter = adapter
            binding.rv.layoutManager = LinearLayoutManager(this)
        }

    }






    //run timer. update timer textview
    private fun runTimer() {

        timer = timer(period = 10){
            time++  // increase in every 0.01sec

            // convert time to min sec millisec
            val millisec = time % 100
            val second = (time % 6000) / 100
            val minute = time / 6000

            //update UI on UIThread
            runOnUiThread {
                if(isRunning) {
                    binding.tvMillisecond.text = "." + if (millisec < 10) "0${millisec}" else "${millisec}"
                    binding.tvSecond.text = ":" + if (second < 10) "0${second}" else "$second"
                    binding.tvMinute.text = "$minute"

                }
            }
        }
    }

    //break countdown timer. update timer textview
    //when timer ends, check the break - isDone. update the view.
    private fun breakTimer(s : String) {
        time = when (s) {
            "30s" -> 30000
            "1m" -> 60000
            "1m 30s" -> 90000
            "2m" -> 120000
            "3m" -> 180000
            "4m" -> 240000
            "5m" -> 300000
            else -> return
        }

        duringBreak = true
        isRunning = false

        //for test
        time = 5000


        cdTimer = object : CountDownTimer(time.toLong(), 10) {
            override fun onTick(p0: Long) {
                // convert time to min sec millisec
                val millisec = (p0 / 10) % 100
                val second = ((p0 % 60000) / 100) / 10
                val minute = (p0 / 60000)

                //update UI on UIThread
                runOnUiThread {
                    binding.tvMillisecond.text ="." + if (millisec < 10) "0${millisec}" else "${millisec}"
                    binding.tvSecond.text = ":" + if (second < 10) "0${second}" else "$second"
                    binding.tvMinute.text = "$minute"
                }
            }

            override fun onFinish() {
                this.cancel()
                time = 0
                binding.tvMillisecond.text = ".00"
                binding.tvSecond.text = ":00"
                binding.tvMinute.text = "0"

                runData[rvPos].isDone = true
                adapter.notifyItemChanged(rvPos)
                duringBreak = false
                isRunning = false
                if (runData.size != rvPos+1) rvPos++
                if (rvPos >= 2) binding.rv.scrollToPosition(rvPos)
                binding.btnStart.text = getString(R.string.run_btn_start)

            }

        }.start()
    }


    //(when button start, stop is pressed)
    // update the number shown on the view.
//    increase pos when called.
    private fun rvPosUpdate(){

        // 끝난 런 시간 기록해서 집어넣음.
        val millisec = time % 100
        val second = (time % 6000) / 100
        val minute = time / 6000

        runData[rvPos].millisec = "." + if (millisec < 10) "0${millisec}" else "${millisec}"
        runData[rvPos].sec = ":" + if (second < 10) "0${second}" else "$second"
        runData[rvPos].min = "$minute"
        runData[rvPos].isDone = true
        adapter.notifyItemChanged(rvPos)



        if (runData.size != rvPos+1) rvPos++
        //move current run to the middle
        if (rvPos >= 2) binding.rv.scrollToPosition(rvPos)
    }


    private fun finishWorkout(){
        val intent = Intent(this,FinishedActivity::class.java)
        intent.putExtra("runData", runData)
        startActivity(intent)
        finish()
        // TODO : delete current activity from stack.

    }


}