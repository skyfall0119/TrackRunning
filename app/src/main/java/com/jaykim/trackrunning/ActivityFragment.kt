package com.jaykim.trackrunning

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jaykim.trackrunning.databinding.FragmentActivitiesBinding
import com.jaykim.trackrunning.databinding.FragmentActivityBinding


class ActivityFragment : Fragment() {


    private var _binding: FragmentActivityBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentActivityBinding.inflate(inflater, container, false)
        val view = binding.root

        //get position.
        val args = this.arguments
        val inputData = args?.getInt("position")
        println("breakPoint : got the arguments : $inputData")

        //TODO : viewModel 사용해서 룸데이터베이스에 접속, 맞는 포지션 데이터 받아오기



        initBtn()

        return view


    }

    private fun initBtn() {
        binding.btnActivityBack.setOnClickListener{

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container,ActivitiesFragment())
            transaction.commit()
        }
    }


}