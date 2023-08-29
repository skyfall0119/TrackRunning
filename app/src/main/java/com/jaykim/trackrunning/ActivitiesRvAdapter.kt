package com.jaykim.trackrunning

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.jaykim.trackrunning.databinding.RvItemActivitiesBinding
import com.jaykim.trackrunning.db.RunsEntity

class ActivitiesRvAdapter(private val list: List<RunsEntity>) : RecyclerView.Adapter<ActivitiesRvAdapter.MyViewHolder>(){


    inner class MyViewHolder (binding : RvItemActivitiesBinding) : RecyclerView.ViewHolder(binding.root){
        val title = binding.activitiesTitle

        // 아이템 클릭시 커스텀 리스너 이벤트 호출
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
        val binding : RvItemActivitiesBinding = RvItemActivitiesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val eachRuns : RunsEntity = list[position]
        holder.title.text = "${eachRuns.date}  ${eachRuns.title}"

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