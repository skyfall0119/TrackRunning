package com.jaykim.trackrunning

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import com.jaykim.trackrunning.databinding.FragmentRunningBinding

class RunningFragment : Fragment() {


    private var _binding: FragmentRunningBinding? = null
    private val binding get() = _binding!!
    var isQs = true

    private lateinit var listDistance: MutableList<String>
    private lateinit var listLaps  : MutableList<Integer>
    private lateinit var listRest : MutableList<String>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRunningBinding.inflate(inflater, container, false)
        val view = binding.root


        radioSet()
        initPicker()
        initBtn()


        return view
    }

    private fun radioSet() {
        val radioGroup = binding.radiogroupRunning

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.radio_qs->{  // to quickstart frame
                    if(!isQs){
                        isQs = true
                        //if current is not quickstart, switch to quickstart frame
                        binding.tvRadioTitle.text = getString(R.string.running_radio_qs)
                        binding.frameQs.visibility = View.VISIBLE
                        binding.frameRv.visibility = View.INVISIBLE
                    }

                }

                R.id.radio_preset->{ // to preset frame
                    if(isQs){
                        isQs = false
                        //if current is quickstart. switch to preset frame
                        binding.tvRadioTitle.text = getString(R.string.running_radio_pre)
                        binding.frameQs.visibility = View.INVISIBLE
                        binding.frameRv.visibility = View.VISIBLE
                    }

                }
            }

        }

    }


    // quickstart 에 numberpicker 세팅. 빠르게 설정해서 런.
    private fun initPicker() {

        val npDistance = binding.qsNpDistance
        val npLaps = binding.qsNpLaps
        val npRest = binding.qsNpRest

        // Quick Start selections
        val npItemDist = arrayOf("100", "200", "400", "800", "1200", "1600", "2000")
        val npItemRest = arrayOf("30s", "1m", "1m 30s", "2m", "3m", "4m", "5m")


        // distance numberpicker
        npDistance.minValue = 0
        npDistance.maxValue = npItemDist.size-1
        npDistance.displayedValues = npItemDist
        npDistance.wrapSelectorWheel = false
        npDistance.textSize = 100f
        npDistance.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        npDistance.isVerticalFadingEdgeEnabled = true


        //laps numberpicker
        npLaps.minValue = 0
        npLaps.maxValue = 15
        npLaps.wrapSelectorWheel = false
        npLaps.textSize = 100f
        npLaps.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        //rest numberpicker
        npRest.minValue = 0
        npRest.maxValue = npItemDist.size-1
        npRest.displayedValues = npItemRest
        npRest.wrapSelectorWheel = false
        npRest.textSize = 100f
        npRest.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

    }

    private fun initBtn() {

        binding.btnRunningStart.setOnClickListener {
            activity?.let{
                val intent = Intent(context,RunActivity::class.java)

//                intent.putExtra("qs_distance", listDistance )
//                intent.putExtra("qs_laps",listLaps)
//                intent.putExtra("qs_rest",listRest)
                startActivity(intent)
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}