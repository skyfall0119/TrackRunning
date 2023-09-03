package com.jaykim.trackrunning

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaykim.trackrunning.databinding.RvItemPresetRunningBinding
import com.jaykim.trackrunning.db.PresetEntity

class RunningRvAdapter (private val list : List<PresetEntity>) : RecyclerView.Adapter<RunningRvAdapter.MyViewHolder>(){

    var selectedPos = -1

    inner class MyViewHolder (binding : RvItemPresetRunningBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.runningPresetTitle
        val layout = binding.runningPresetLayout



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding : RvItemPresetRunningBinding =
            RvItemPresetRunningBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val preset : PresetEntity = list[position]


        holder.title.text = preset.title

        if (selectedPos == position) {
            holder.title.setTextColor(Color.WHITE)
            holder.layout.setBackgroundColor(Color.GRAY)

        }
        else {
            holder.title.setTextColor(Color.BLACK)
            holder.layout.setBackgroundColor(Color.WHITE)
        }


        holder.layout.setOnClickListener {
            selectedPos = position
            notifyDataSetChanged()
        }

        // on long click, show the run list
        holder.layout.setOnLongClickListener{view->

            val itemsList = ArrayList<String>()
            preset.SingleWorkout.forEach {
                if(it.isRest) itemsList.add("Break   ${it.breakPick}")
                else itemsList.add("Run   ${it.distance} m")
            }

            AlertDialog.Builder(view.context)
                .setTitle(holder.title.text)
                .setItems(itemsList.toTypedArray(),null)
                .show()

            return@setOnLongClickListener true
        }
    }

}