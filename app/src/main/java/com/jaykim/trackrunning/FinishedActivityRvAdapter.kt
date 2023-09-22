package com.jaykim.trackrunning

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaykim.trackrunning.databinding.RvItemFinishedBinding


class FinishedActivityRvAdapter (private val list : ArrayList<SingleRun>) : RecyclerView.Adapter<FinishedActivityRvAdapter.MyViewHolder>() {

    var selectedPos = -1

    inner class MyViewHolder (binding : RvItemFinishedBinding) : RecyclerView.ViewHolder(binding.root){
        val finished_rv_distance = binding.finishedRvDistance
        val finished_rv_min = binding.finishedRvMin
        val finished_rv_sec = binding.finishedRvSec
        val finished_rv_millisec = binding.finishedRvMillisec
        val finished_rv_breakTime = binding.finishedRvBreakTime
        val finished_rv_breakLayout = binding.finishedRvBreakLayout
        val finished_rv_runLayout = binding.finishedRvRunLayout
        val finished_rv_rootLayout = binding.finishedRvRootLayout



        init{
            itemView.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION && mOnItemClickListener != null){
                    mOnItemClickListener.onItemClick(itemView,pos)
                }
            }
        }
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

            holder.finished_rv_breakLayout.visibility = View.VISIBLE
            holder.finished_rv_runLayout.visibility = View.INVISIBLE
            holder.finished_rv_breakTime.text = runData.breakPick

            if (selectedPos == position)
                holder.finished_rv_breakLayout.setBackgroundColor(Color.LTGRAY)
             else holder.finished_rv_breakLayout.setBackgroundColor(Color.WHITE)

        } else {

            holder.finished_rv_breakLayout.visibility = View.INVISIBLE
            holder.finished_rv_runLayout.visibility = View.VISIBLE

            holder.finished_rv_distance.text = runData.distance
            holder.finished_rv_min.text = runData.min
            holder.finished_rv_sec.text = runData.sec
            holder.finished_rv_millisec.text = runData.millisec

            if (selectedPos == position)
                holder.finished_rv_runLayout.setBackgroundColor(Color.LTGRAY)
             else holder.finished_rv_runLayout.setBackgroundColor(Color.WHITE)

        }


//        holder.finished_rv_rootLayout.setOnClickListener {
//            selectedPos = position
//            notifyDataSetChanged()
//        }


    }



    interface onItemClickListener {
        fun onItemClick(view : View, position: Int)
    }
    //객체 저장 변수
    private lateinit var mOnItemClickListener: ActivitiesRvAdapter.onItemClickListener
    //객체 전달 메서드
    fun setOnItemClickListener(onItemClickListener: ActivitiesRvAdapter.onItemClickListener){
        mOnItemClickListener = onItemClickListener
    }


}