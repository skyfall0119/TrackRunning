package com.jaykim.trackrunning

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaykim.trackrunning.databinding.RunRvItemBinding

class RunActivityRvAdapter (private val list : ArrayList<SingleRun>) :
    RecyclerView.Adapter<RunActivityRvAdapter.MyViewHolder>(){



    inner class MyViewHolder (binding : RunRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val run_rv_distance = binding.runRvDistance
        val run_rv_min = binding.runRvMin
        val run_rv_sec = binding.runRvSec
        val run_rv_millisec = binding.runRvMillisec
        val run_rv_break = binding.runRvBreak
        val run_rv_meter = binding.runRvMeter
        val run_rv_breakTime = binding.runRvBreakTime
        val root = binding.root

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding : RunRvItemBinding = RunRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)

    }

    override fun getItemCount(): Int {

        return list.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val runData = list[position]

        if (runData.isRest){
            holder.run_rv_break.visibility = View.VISIBLE
            holder.run_rv_breakTime.visibility = View.VISIBLE
            holder.run_rv_distance.visibility = View.INVISIBLE
            holder.run_rv_meter.visibility = View.INVISIBLE
        } else {
            holder.run_rv_break.visibility = View.INVISIBLE
            holder.run_rv_breakTime.visibility = View.INVISIBLE
            holder.run_rv_distance.visibility = View.VISIBLE
            holder.run_rv_meter.visibility = View.VISIBLE
        }
        holder.run_rv_distance.text = runData.distance
        holder.run_rv_min.text = runData.lapTimeMin
        holder.run_rv_sec.text = runData.lapTimeSec
        holder.run_rv_millisec.text = runData.lapTimeMillisec

    }
}