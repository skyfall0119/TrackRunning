package com.jaykim.trackrunning

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaykim.trackrunning.databinding.RvItemRunBinding

class RunActivityRvAdapter (private val list : ArrayList<SingleRun>) :
    RecyclerView.Adapter<RunActivityRvAdapter.MyViewHolder>(){



    inner class MyViewHolder (binding : RvItemRunBinding) : RecyclerView.ViewHolder(binding.root) {
        val run_rv_distance = binding.runRvDistance
        val run_rv_min = binding.runRvMin
        val run_rv_sec = binding.runRvSec
        val run_rv_millisec = binding.runRvMillisec
        val run_rv_break = binding.runRvBreak
        val run_rv_meter = binding.runRvMeter
        val run_rv_breakTime = binding.runRvBreakTime
        val root = binding.root
        val runLayout = binding.runRvRunLayout
        val breakLayout = binding.runRvBreakLayout

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding : RvItemRunBinding = RvItemRunBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val runData = list[position]
        if (runData.isRest){
            holder.runLayout.visibility = View.INVISIBLE
            holder.breakLayout.visibility = View.VISIBLE
            holder.run_rv_breakTime.text = runData.breakPick


            if (runData.isDone) {
                holder.breakLayout.setBackgroundColor(Color.GRAY)
                holder.run_rv_break.setTextColor(Color.WHITE)
                holder.run_rv_breakTime.setTextColor(Color.WHITE)
            }


        } else {
            holder.runLayout.visibility = View.VISIBLE
            holder.breakLayout.visibility = View.INVISIBLE
            holder.run_rv_distance.text = runData.distance
            holder.run_rv_min.text = runData.min
            holder.run_rv_sec.text = runData.sec
            holder.run_rv_millisec.text = runData.millisec

            if (runData.isDone) {

                holder.runLayout.setBackgroundColor(Color.GRAY)
                holder.run_rv_distance.setTextColor(Color.WHITE)
                holder.run_rv_meter.setTextColor(Color.WHITE)
                holder.run_rv_min.setTextColor(Color.WHITE)
                holder.run_rv_sec.setTextColor(Color.WHITE)
                holder.run_rv_millisec.setTextColor(Color.WHITE)

            }
        }

    }
}