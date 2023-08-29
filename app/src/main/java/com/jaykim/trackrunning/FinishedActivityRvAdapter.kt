package com.jaykim.trackrunning

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaykim.trackrunning.databinding.RvItemFinishedBinding


class FinishedActivityRvAdapter (private val list : ArrayList<SingleRun>) : RecyclerView.Adapter<FinishedActivityRvAdapter.MyViewHolder>() {


    inner class MyViewHolder (binding : RvItemFinishedBinding) : RecyclerView.ViewHolder(binding.root){
        val finished_rv_distance = binding.finishedRvDistance
        val finished_rv_min = binding.finishedRvMin
        val finished_rv_sec = binding.finishedRvSec
        val finished_rv_millisec = binding.finishedRvMillisec
        val finished_rv_break = binding.finishedRvBreak
        val finished_rv_meter = binding.finishedRvMeter
        val finished_rv_breakTime = binding.finishedRvBreakTime
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding : RvItemFinishedBinding = RvItemFinishedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val runData = list[position]
        if (runData.isRest){
            holder.finished_rv_break.visibility = View.VISIBLE
            holder.finished_rv_breakTime.visibility = View.VISIBLE
            holder.finished_rv_distance.visibility = View.INVISIBLE
            holder.finished_rv_meter.visibility = View.INVISIBLE
            holder.finished_rv_min.visibility = View.INVISIBLE
            holder.finished_rv_sec.visibility = View.INVISIBLE
            holder.finished_rv_millisec.visibility = View.INVISIBLE

            holder.finished_rv_breakTime.text = runData.breakPick


        } else {
            holder.finished_rv_break.visibility = View.INVISIBLE
            holder.finished_rv_breakTime.visibility = View.INVISIBLE
            holder.finished_rv_distance.visibility = View.VISIBLE
            holder.finished_rv_meter.visibility = View.VISIBLE
            holder.finished_rv_min.visibility = View.VISIBLE
            holder.finished_rv_sec.visibility = View.VISIBLE
            holder.finished_rv_millisec.visibility = View.VISIBLE

            holder.finished_rv_distance.text = runData.distance
            holder.finished_rv_min.text = runData.min
            holder.finished_rv_sec.text = runData.sec
            holder.finished_rv_millisec.text = runData.millisec
        }

    }


}