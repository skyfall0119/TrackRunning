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

    //QuickStart NumberPicker items
    private val npItemDist = arrayOf("100", "200", "400", "800", "1200", "1600", "2000")
    private val npItemRest = arrayOf("30s", "1m", "1m 30s", "2m", "3m", "4m", "5m")

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
        npRest.maxValue = npItemDist.size-1
        npRest.displayedValues = npItemRest
        npRest.wrapSelectorWheel = false
        npRest.textSize = 100f
        npRest.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

    }

    //get the return value from break time, and transform to timer format
    private fun getTime(i :Int):String{
        when (i){
            0 -> return "00:30:00"   //"30s"
            1 -> return "01:00:00"   //"1m"
            2 -> return "01:30:00"   // "1m 30s"
            3 -> return "02:00:00"   // "2m"
            4 -> return "03:00:00"   //  "3m"
            5 -> return "04:00:00"   //  "4m"
            6 -> return "05:00:00"   //  "5m"
        }
        return  "00:00:00"
    }
    private fun initBtn() {
        //when button start is pressed, pack the running sequence and send to RunActivity.

        binding.btnRunningStart.setOnClickListener {
            activity?.let{
                val intent = Intent(context,RunActivity::class.java)

                //(run + break) x (lap-1) + run
                if(isQs){ //if quickstart, send the value from numberPicker
                    val qsRunData = ArrayList<SingleRun>() //Data Array to send

                    for (i in 1 until binding.qsNpLaps.value){
                        //lap
                        qsRunData.add(SingleRun(false,npItemDist[binding.qsNpDistance.value],1,
                            "00",":00",".00"))
                        //break
                        val breakTime = getTime(binding.qsNpRest.value)
//                        val breakTime = npItemRest[binding.qsNpRest.value]


                        qsRunData.add(SingleRun(true,"0",1,
                            breakTime.substring(0,2),breakTime.substring(2,5),breakTime.substring(5,8)))
                    }
                    // last run
                    qsRunData.add(SingleRun(false,npItemDist[binding.qsNpDistance.value],1,
                        "00",":00",".00"))

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