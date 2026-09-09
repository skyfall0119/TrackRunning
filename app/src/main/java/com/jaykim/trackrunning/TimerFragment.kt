package com.jaykim.trackrunning

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaykim.trackrunning.databinding.FragmentTimerBinding
import java.util.Timer
import kotlin.concurrent.timer

// simple stand-alone stopwatch: start/pause, lap recording, reset.
// independent of the interval workout timer in RunActivity.
class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: LapRvAdapter
    // newest lap first, so it renders at the top of the list
    private val laps = ArrayList<LapRecord>()

    // elapsed time in the same "10ms tick" unit Helper.intTimeToStr() expects
    private var elapsedTicks = 0
    private var isRunning = false
    private var timer: Timer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)

        initRecycler()
        initBtn()
        updateDisplay()
        updateEmptyState()
        updateButtonsForState()

        // restore an in-progress stopwatch after a configuration change (e.g. rotation)
        savedInstanceState?.let { restoreState(it) }

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("elapsedTicks", elapsedTicks)
        outState.putBoolean("isRunning", isRunning)
        outState.putSerializable("laps", laps)
    }

    private fun restoreState(savedInstanceState: Bundle) {
        elapsedTicks = savedInstanceState.getInt("elapsedTicks")
        isRunning = savedInstanceState.getBoolean("isRunning")

        val restoredLaps = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            savedInstanceState.getSerializable("laps", ArrayList::class.java) as ArrayList<LapRecord>?
        } else {
            @Suppress("DEPRECATION", "UNCHECKED_CAST")
            savedInstanceState.getSerializable("laps") as? ArrayList<LapRecord>
        }
        if (restoredLaps != null) {
            laps.clear()
            laps.addAll(restoredLaps)
            adapter.notifyDataSetChanged()
        }

        updateDisplay()
        updateEmptyState()
        updateButtonsForState()

        if (isRunning) {
            runTimer()
        }
    }

    private fun initRecycler() {
        adapter = LapRvAdapter(laps)
        binding.timerRvLaps.adapter = adapter
        binding.timerRvLaps.layoutManager = LinearLayoutManager(context)
    }

    private fun initBtn() {
        binding.btnTimerStartPause.setOnClickListener {
            if (isRunning) pauseTimer() else runTimer()
        }

        binding.btnTimerLapReset.setOnClickListener {
            if (isRunning) {
                recordLap()
            } else if (elapsedTicks > 0) {
                resetTimer()
            }
        }
    }

    private fun runTimer() {
        isRunning = true
        updateButtonsForState()

        timer = timer(period = 10) {
            elapsedTicks++

            activity?.runOnUiThread {
                if (_binding == null) return@runOnUiThread
                if (isRunning) updateDisplay()
            }
        }
    }

    private fun pauseTimer() {
        timer?.cancel()
        isRunning = false
        updateButtonsForState()
    }

    private fun recordLap() {
        val previousTotal = laps.firstOrNull()?.totalTime ?: 0
        val lap = LapRecord(
            lapNumber = laps.size + 1,
            lapTime = elapsedTicks - previousTotal,
            totalTime = elapsedTicks
        )
        laps.add(0, lap)
        adapter.notifyItemInserted(0)
        binding.timerRvLaps.scrollToPosition(0)
        updateEmptyState()
        // the current-lap clock is derived from laps.firstOrNull(), so refresh it
        // immediately instead of waiting for the next tick to show it reset to 0
        updateDisplay()
    }

    private fun resetTimer() {
        elapsedTicks = 0
        laps.clear()
        adapter.notifyDataSetChanged()
        updateDisplay()
        updateEmptyState()
        updateButtonsForState()
    }

    private fun updateDisplay() {
        if (_binding == null) return

        // total time: keeps increasing regardless of laps
        renderTime(elapsedTicks, binding.tvTimerMinute, binding.tvTimerSecond, binding.tvTimerMillisecond)

        // current lap time: elapsed since the last recorded lap (or since start, if none yet)
        val lapStartTicks = laps.firstOrNull()?.totalTime ?: 0
        renderTime(elapsedTicks - lapStartTicks, binding.tvTimerLapMinute, binding.tvTimerLapSecond, binding.tvTimerLapMillisecond)
    }

    private fun renderTime(ticks: Int, minuteView: TextView, secondView: TextView, millisecondView: TextView) {
        val millisec = ticks % 100
        val second = (ticks % 6000) / 100
        val minute = ticks / 6000

        millisecondView.text = "." + if (millisec < 10) "0$millisec" else "$millisec"
        secondView.text = ":" + if (second < 10) "0$second" else "$second"
        minuteView.text = "$minute"
    }

    private fun updateButtonsForState() {
        binding.btnTimerStartPause.text =
            if (isRunning) getString(R.string.timer_btn_pause) else getString(R.string.timer_btn_start)

        binding.btnTimerLapReset.isEnabled = isRunning || elapsedTicks > 0
        binding.btnTimerLapReset.text =
            if (isRunning) getString(R.string.timer_btn_lap) else getString(R.string.timer_btn_reset)
    }

    private fun updateEmptyState() {
        binding.timerNoLaps.visibility = if (laps.isEmpty()) View.VISIBLE else View.GONE
        binding.timerLapHeader.visibility = if (laps.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        _binding = null
    }
}
