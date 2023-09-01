package com.jaykim.trackrunning

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaykim.trackrunning.databinding.RvItemPresetAddBinding

class PresetAddRvAdapter (private val list : List<SingleRun>): RecyclerView.Adapter<PresetAddRvAdapter.MyViewHolder>(){

    inner class MyViewHolder (binding : RvItemPresetAddBinding) : RecyclerView.ViewHolder(binding.root)
    {
        val runLayout = binding.presetRvRunlayout
        val restLayout = binding.presetRvRestlayout
        val runDist = binding.presetRvDistance
        val breakTime = binding.presetRvBreakTime

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding : RvItemPresetAddBinding =
            RvItemPresetAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val runData = list[position]
        if (runData.isRest){
            holder.runLayout.visibility = View.INVISIBLE
            holder.restLayout.visibility = View.VISIBLE
            holder.breakTime.text = runData.breakPick

        } else {
            holder.runLayout.visibility = View.VISIBLE
            holder.restLayout.visibility = View.INVISIBLE
            holder.runDist.text = runData.distance
        }
    }


}