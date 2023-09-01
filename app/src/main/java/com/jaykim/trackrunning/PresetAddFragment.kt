package com.jaykim.trackrunning

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jaykim.trackrunning.databinding.FragmentPresetAddBinding
import com.jaykim.trackrunning.db.AppDatabase
import com.jaykim.trackrunning.db.PresetDao
import com.jaykim.trackrunning.db.PresetEntity
import kotlin.concurrent.thread


class PresetAddFragment : Fragment() {

    private var _binding: FragmentPresetAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var db : AppDatabase
    private lateinit var presetDao : PresetDao
    private lateinit var presetEntity : PresetEntity
    private lateinit var adapter : PresetAddRvAdapter
    private lateinit var runData : ArrayList<SingleRun>
    var curPos = 0
    private val npItemDist = Helper.qsDist
    private val npItemRest = Helper.qsRest


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentPresetAddBinding.inflate(inflater, container, false)




        initDb()
        initBtn()


        return binding.root
    }






    private fun initDb() {
        thread {

            db = AppDatabase.getInstance(requireContext())!!
            presetDao = db.getPresetDao()

            val args = this.arguments
            curPos = args?.getInt("position")!!

            println("breakpoint : presetAdd $curPos received")

            // if adding new, initialize empty runData
            if (curPos == -1) {
                runData = ArrayList()
            } else {
                //if accessing to already created preset, get preset from the position
                presetEntity = presetDao.getAllPreset()[curPos]
                runData = presetEntity.SingleWorkout
                binding.presetAddEnterTitle.setText(presetEntity.title)
                // show delete button on the appbar only when previous preset is opened
                (requireActivity() as AppCompatActivity).supportActionBar!!.title = presetEntity.title
                setHasOptionsMenu(true)

            }

            initView()
        }
    }

    private fun initView(){
        activity?.runOnUiThread {

            adapter = PresetAddRvAdapter(runData)
            binding.presetAddRv.adapter = adapter
        }

    }



    private fun initBtn() {


        val npAddDist = binding.presetAddNpdist
        val npAddRest = binding.presetAddNprest

        //numberpicker  distance items
        npAddDist.minValue = 0
        npAddDist.maxValue = npItemDist.size-1
        npAddDist.displayedValues = npItemDist
        npAddDist.wrapSelectorWheel = false
        npAddDist.textSize = 100f
        npAddDist.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        npAddDist.isVerticalFadingEdgeEnabled = true

        //numberpicker rest items
        npAddRest.minValue = 0
        npAddRest.maxValue = npItemRest.size-1
        npAddRest.displayedValues = npItemRest
        npAddRest.wrapSelectorWheel = false
        npAddRest.textSize = 100f
        npAddRest.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS





        //dist+,
        binding.presetAddBtnDist.setOnClickListener {
            runData.add(SingleRun(false, npItemDist[npAddDist.value],1, "0"))
            adapter.notifyDataSetChanged()
        }
//        rest+
        binding.presetAddBtnRest.setOnClickListener {
            runData.add(SingleRun(true,"0", 1, npItemRest[npAddRest.value]))
            adapter.notifyDataSetChanged()

        }

        //Done
        binding.presetAddDone.setOnClickListener {
            thread{
                val title =
                    if (binding.presetAddEnterTitle.text.toString() == "") {"My Custom Run"}
                    else {binding.presetAddEnterTitle.text.toString()}

                //if new, add new Entity. if existing, update the previous.
                if (curPos == -1)
                    presetDao.insertPreset(PresetEntity(null,title,runData))
                else presetDao.updatePreset(presetEntity)

            }

            backToPreset()
        }
        //back
        binding.presetAddBack.setOnClickListener {
            backToPreset()
        }

    }





    // actionbar delete button override
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.action_delete,menu)

    }
    // actionbar delete button override
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId){
            R.id.action_delete -> {
                val builder = AlertDialog.Builder(requireActivity())
                builder.setMessage(getString(R.string.activity_deleteDialog))
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.activity_delete_yes)) { dialog, id->
                        //delete current preset
                        Thread{
                            presetDao.deletePreset(presetEntity)
                        }.start()

                        Toast.makeText(requireActivity(),
                            "${presetEntity.title} deleted",
                            Toast.LENGTH_LONG).show()
                        backToPreset()
                    }

                    .setNegativeButton(getString(R.string.activity_delete_no)) {dialog, id->
                        dialog.dismiss()
                    }
                    .create().show()
            }
        }

        return super.onOptionsItemSelected(item)
    }






    private fun backToPreset(){
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container,PresetFragment())
        transaction.commit()
        (requireActivity() as AppCompatActivity).supportActionBar!!.setTitle(R.string.menu_preset)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}