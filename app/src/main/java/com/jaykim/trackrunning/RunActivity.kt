package com.jaykim.trackrunning

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jaykim.trackrunning.databinding.ActivityRunBinding

class RunActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRunBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRunBinding.inflate(layoutInflater)
        setContentView(binding.root)





    }
}