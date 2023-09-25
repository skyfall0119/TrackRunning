package com.jaykim.trackrunning

import android.app.AlertDialog
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import androidx.core.content.ContextCompat
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

            holder.layout.setBackgroundColor(Color.LTGRAY)

        }
        else {

            holder.layout.setBackgroundColor(Color.WHITE)
        }


        holder.layout.setOnClickListener {
            selectedPos = position
            notifyDataSetChanged()
        }

        // on long click, show the run list
        holder.layout.setOnLongClickListener{view->


            val list = ArrayList<HashMap<String, String>>()
            preset.SingleWorkout.forEach {
                var map = HashMap<String,String>()
                if(it.isRest) {
                    map["list1"] = "${view.context.getString(R.string.preset_add_break)}"
                    map["list2"] = "${it.breakPick}"
                }
                else {
                    map["list1"] = "${view.context.getString(R.string.preset_add_run)}"
                    map["list2"] = "${it.distance} m"

                }
                list.add(map)
            }

            var keys = arrayOf("list1", "list2")
            var ids = intArrayOf(R.id.preset_add_dialog1,R.id.preset_add_dialog2)
            var adapter = SimpleAdapter(view.context,list,R.layout.dialog_preset,keys,ids)




            AlertDialog.Builder(view.context)
                .setTitle(holder.title.text)
                .setAdapter(adapter,null)
//                .setItems(list,null)
                .show()

            return@setOnLongClickListener true
        }
    }

}