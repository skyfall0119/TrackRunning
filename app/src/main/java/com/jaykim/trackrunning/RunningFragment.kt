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
    private var isQs = true

    //QuickStart NumberPicker items
    private val npItemDist = Helper.qsDist
    private val npItemRest = Helper.qsRest


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRunningBinding.inflate(inflater, container, false)
        val view = binding.root


        radioSet()
        initPicker()
        initPresetView()
        initBtn()


        return view
    }

    private fun initPresetView() {

    }

    private fun radioSet() {
        val radioGroup = binding.radiogroupRunning

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.radio_qs->{  // to quickstart frame
                    isQs = true
                    //if current is not quickstart, switch to quickstart frame
                    binding.tvRadioTitle.text = getString(R.string.running_radio_qs)
                    binding.frameQs.visibility = View.VISIBLE
                    binding.frameRv.visibility = View.INVISIBLE
                }

                R.id.radio_preset->{ // to preset frame
                    isQs = false
                    binding.tvRadioTitle.text = getString(R.string.running_radio_pre)
                    binding.frameQs.visibility = View.INVISIBLE
                    binding.frameRv.visibility = View.VISIBLE
                }
            }

        }

    }


    // quickstart 에 numberpicker 세팅. 빠르게 설정해서 런.
    private fun initPicker() {

        val npDistance = binding.qsNpDistance
        val npLaps = binding.qsNpLaps
        val npRest = binding.qsNpRest


        //numberpicker  distance items
        npDistance.minValue = 0
        npDistance.maxValue = npItemDist.size-1
        npDistance.displayedValues = npItemDist
        npDistance.wrapSelectorWheel = false
        npDistance.textSize = 100f
        npDistance.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        npDistance.isVerticalFadingEdgeEnabled = true


        //numberpicker laps items
        npLaps.minValue = 1
        npLaps.maxValue = 15
        npLaps.wrapSelectorWheel = false
        npLaps.textSize = 100f
        npLaps.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        //numberpicker rest items
        npRest.minValue = 0
        npRest.maxValue = npItemRest.size-1
        npRest.displayedValues = npItemRest
        npRest.wrapSelectorWheel = false
        npRest.textSize = 100f
        npRest.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

    }

    private fun initBtn() {
        //when button start is pressed, pack the running sequence and send to RunActivity.

        binding.btnRunningStart.setOnClickListener {
            activity?.let{
                val intent = Intent(context,RunActivity::class.java)


                if(isQs){ //if quickstart, send the value from numberPicker
                    val qsRunData = ArrayList<SingleRun>() //Data Array to send
                    //(run + break) x (lap-1)
                    for (i in 1 until binding.qsNpLaps.value){

                        qsRunData.add(SingleRun(false,npItemDist[binding.qsNpDistance.value],
                            1,""))
                        qsRunData.add(SingleRun(true,"0",1,npItemRest[binding.qsNpRest.value]
                            ))
                    }
                    // last run
                    qsRunData.add(SingleRun(false,npItemDist[binding.qsNpDistance.value],
                        1,"",))

                    intent.putExtra("runData", qsRunData )
                } else{ //if preset, send the runData from preset


                }


                startActivity(intent)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}