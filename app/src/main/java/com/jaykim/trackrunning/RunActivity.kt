package com.jaykim.trackrunning

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaykim.trackrunning.databinding.ActivityRunBinding

class RunActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRunBinding
    private lateinit var adapter :RunActivityRvAdapter
    private lateinit var runData : ArrayList<SingleRun>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRunBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // backThread 에서 작업해야될듯?
        initRecycler()

    }

    private fun initRecycler() {



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            runData = intent.getSerializableExtra("runData",ArrayList<SingleRun>()::class.java)!!
        } else {
            runData = intent.getSerializableExtra("runData") as ArrayList<SingleRun>
        }


        adapter = RunActivityRvAdapter(runData)
        binding.rv.adapter = adapter
        binding.rv.layoutManager = LinearLayoutManager(this)
    }



}