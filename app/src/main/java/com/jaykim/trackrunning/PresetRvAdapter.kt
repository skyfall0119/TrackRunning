package com.jaykim.trackrunning

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaykim.trackrunning.databinding.RvItemActivitiesBinding
import com.jaykim.trackrunning.db.PresetEntity

class PresetRvAdapter (private val list : List<PresetEntity>) : RecyclerView.Adapter<PresetRvAdapter.MyViewHolder>(){

    inner class MyViewHolder (binding : RvItemActivitiesBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.activitiesTitle
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
        val binding : RvItemActivitiesBinding =
            RvItemActivitiesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val preset : PresetEntity = list[position]
        holder.title.text = preset.title
    }



    //custom listener
    interface onItemClickListener {
        fun onItemClick(view : View, position: Int)
    }
    //객체 저장 변수
    private lateinit var mOnItemClickListener: onItemClickListener
    //객체 전달 메서드
    fun setOnItemClickListener(onItemClickListener: onItemClickListener){
        mOnItemClickListener = onItemClickListener
    }


}