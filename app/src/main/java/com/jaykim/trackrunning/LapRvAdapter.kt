package com.jaykim.trackrunning

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaykim.trackrunning.databinding.RvItemLapBinding

class LapRvAdapter(private val laps: List<LapRecord>) : RecyclerView.Adapter<LapRvAdapter.MyViewHolder>() {

    inner class MyViewHolder(binding: RvItemLapBinding) : RecyclerView.ViewHolder(binding.root) {
        val lapNumber = binding.lapRvNumber
        val lapTime = binding.lapRvLapTime
        val totalTime = binding.lapRvTotalTime
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RvItemLapBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = laps.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val lap = laps[position]
        holder.lapNumber.text = holder.itemView.context.getString(R.string.timer_lap_label, lap.lapNumber)
        holder.lapTime.text = Helper.intTimeToStr(lap.lapTime)
        holder.totalTime.text = Helper.intTimeToStr(lap.totalTime)
    }
}
