package com.jaykim.trackrunning


import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
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
    // remaining break time (ms), kept up to date every tick so it can be restored after rotation
    private var remainingBreakMillis = 0L



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRunBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // keep screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        initRecycler(savedInstanceState)
        initBtn()

        // restore an in-progress workout after a configuration change (e.g. rotation)
        savedInstanceState?.let { restoreState(it) }
    }

    // preserve the in-progress workout across configuration changes (e.g. screen rotation).
    // note: this does not survive full process death, only Activity recreation.
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("runData", runData)
        outState.putInt("rvPos", rvPos)
        outState.putInt("time", time)
        outState.putBoolean("isRunning", isRunning)
        outState.putBoolean("duringBreak", duringBreak)
        outState.putLong("remainingBreakMillis", remainingBreakMillis)
    }

    private fun restoreState(savedInstanceState: Bundle) {
        rvPos = savedInstanceState.getInt("rvPos")
        time = savedInstanceState.getInt("time")
        isRunning = savedInstanceState.getBoolean("isRunning")
        duringBreak = savedInstanceState.getBoolean("duringBreak")
        remainingBreakMillis = savedInstanceState.getLong("remainingBreakMillis")

        binding.tvTitle.text = if (duringBreak) getString(R.string.run_btn_break)
            else "${ runData[rvPos].distance } m"

        binding.btnStart.text = when {
            duringBreak -> getString(R.string.run_btn_break)
            isRunning -> getString(R.string.run_btn_record)
            else -> getString(R.string.run_btn_start)
        }

        if (duringBreak) {
            startCountdown(remainingBreakMillis.coerceAtLeast(10))
        } else if (isRunning) {
            runTimer()
        }
    }


    private fun initBtn() {

        val btnStart = binding.btnStart
        val btnStop = binding.btnStop


        btnStart.setOnClickListener {
            if (!duringBreak){ //during break, disable start button
                binding.tvTitle.text = "${ runData[rvPos].distance } m"
                if (isRunning){ //while running.
                    //change btn to start.
                    timer?.cancel()
                    // record the time -- posUpdate
                    binding.btnStart.text = getString(R.string.run_btn_break)
                    rvPosUpdate()

                    breakTimer(runData[rvPos].breakPick)



                    //if last it was the last run. move to  finishedActivity
                    if (runData.size == rvPos+1) finishWorkout()

                } else { //first time starting, after breaktime expires. after pause
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


            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.activity_endDialog))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.activity_delete_yes)) { dialog, id->
                    //end the ru
                    finishWorkout()
                }

                .setNegativeButton(getString(R.string.activity_delete_no)) {dialog, id->
                    dialog.dismiss()
                }
                .create().show()

            return@setOnLongClickListener true
        }

        //pause
        binding.btnStop.setOnClickListener {
            timer?.cancel()
            cdTimer?.cancel()
            duringBreak = false
            btnStart.text = getString(R.string.run_btn_start)
            isRunning = false
        }
    }

    private fun initRecycler(savedInstanceState: Bundle?) {

        // after a rotation, resume from the saved runData (with progress) instead of
        // re-reading the original intent, which would reset all progress made so far
        runData = if (savedInstanceState != null) {
            extractRunData(savedInstanceState)
        } else {
            extractRunData(intent)
        }
        runOnUiThread{
            adapter = RunActivityRvAdapter(runData)
            binding.rv.adapter = adapter
            binding.rv.layoutManager = LinearLayoutManager(this)
            binding.tvTitle.text = "${ runData[rvPos].distance } m"

        }

    }

    private fun extractRunData(intent: Intent): ArrayList<SingleRun> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("runData",ArrayList<SingleRun>()::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("runData") as ArrayList<SingleRun>
        }
    }

    private fun extractRunData(bundle: Bundle): ArrayList<SingleRun> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getSerializable("runData",ArrayList<SingleRun>()::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            bundle.getSerializable("runData") as ArrayList<SingleRun>
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
                    binding.tvMillisecond.text = "." + if (millisec < 10) "0${millisec}" else "$millisec"
                    binding.tvSecond.text = ":" + if (second < 10) "0${second}" else "$second"
                    binding.tvMinute.text = "$minute"

                }
            }
        }
    }

    //break countdown timer. update timer textview
    //when timer ends, check the break - isDone. update the view.
    private fun breakTimer(s : String) {
        // reuse Helper's single source of truth for the break-time labels instead of
        // duplicating the same string->ms mapping here; reject unrecognized labels
        // instead of starting a bogus countdown.
        if (s !in Helper.qsRest) return
        time = Helper.breakToInt(s)

        duringBreak = true
        isRunning = false
        binding.tvTitle.text = "${getString(R.string.run_btn_break)}"

        startCountdown(time.toLong())
    }

    private fun startCountdown(durationMillis: Long) {
        cdTimer = object : CountDownTimer(durationMillis, 10) {
            override fun onTick(p0: Long) {
                remainingBreakMillis = p0

                // convert time to min sec millisec
                val millisec = (p0 / 10) % 100
                val second = ((p0 % 60000) / 100) / 10
                val minute = (p0 / 60000)

                //update UI on UIThread
                runOnUiThread {
                    binding.tvMillisecond.text ="." + if (millisec < 10) "0${millisec}" else "$millisec"
                    binding.tvSecond.text = ":" + if (second < 10) "0${second}" else "$second"
                    binding.tvMinute.text = "$minute"
                }
            }

            override fun onFinish() {
                this.cancel()
                time = 0
                remainingBreakMillis = 0
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
        runData[rvPos].msTime = time
        val millisec = time % 100
        val second = (time % 6000) / 100
        val minute = time / 6000

        runData[rvPos].millisec = "." + if (millisec < 10) "0${millisec}" else "$millisec"
        runData[rvPos].sec = ":" + if (second < 10) "0${second}" else "$second"
        runData[rvPos].min = "$minute"
        runData[rvPos].isDone = true
        adapter.notifyItemChanged(rvPos)



        if (runData.size != rvPos+1) rvPos++


        //move current run to the middle
        binding.rv.scrollToPosition(rvPos)

    }


    private fun finishWorkout(){
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        timer?.cancel()
        cdTimer?.cancel()

        // if a run was actively in progress (not on break), record its partial time
        // before finishing, matching the same recording path as a normal completion.
        if (isRunning && !duringBreak) {
            rvPosUpdate()
        }

        // only carry over laps that were actually completed (or the partial run just
        // recorded above); an unfinished trailing lap/rest must not be saved as a
        // fake "00:00.00" entry.
        val cutoffIndex = runData.indexOfFirst { !it.isDone }
        val finishedRunData = if (cutoffIndex == -1) ArrayList(runData)
            else ArrayList(runData.subList(0, cutoffIndex))

        val intent = Intent(this,FinishedActivity::class.java)
        intent.putExtra("runData", finishedRunData)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        cdTimer?.cancel()
    }

}