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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.jaykim.trackrunning.databinding.FragmentPresetAddBinding
import com.jaykim.trackrunning.db.AppDatabase
import com.jaykim.trackrunning.db.PresetDao
import com.jaykim.trackrunning.db.PresetEntity
import java.util.Collections
import kotlin.concurrent.thread


class PresetAddFragment : Fragment() {

    private var _binding: FragmentPresetAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var db : AppDatabase
    private lateinit var presetDao : PresetDao
    private lateinit var presetEntity : PresetEntity
    private lateinit var adapter : PresetAddRvAdapter
    private lateinit var runData : ArrayList<SingleRun>

    private var curPos = 0
    private lateinit var curTitle : String
    private val npItemDist = Helper.qsDist
    private val npItemRest = Helper.qsRest




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentPresetAddBinding.inflate(inflater, container, false)




        initDb()
        initBtn()

//        requireActivity().dispatchTouchEvent()


        return binding.root
    }




    private fun initDb() {
        thread {

            db = AppDatabase.getInstance(requireContext())!!
            presetDao = db.getPresetDao()

            val args = this.arguments
            curPos = args?.getInt("position")!!


            // if adding new, initialize empty runData
            if (curPos == -1) {
                runData = ArrayList()
                curTitle = getString(R.string.preset_add_navTitle)
            } else {
                //if accessing to already created preset, get preset from the position
                presetEntity = presetDao.getAllPreset()[curPos]
                runData = presetEntity.SingleWorkout
                binding.presetAddEnterTitle.setText(presetEntity.title)
                // show delete button on the appbar only when previous preset is opened
                curTitle = presetEntity.title
                setHasOptionsMenu(true)

            }

            initView()
        }
    }

    private fun initView(){
        activity?.runOnUiThread {

            adapter = PresetAddRvAdapter(runData)
            binding.presetAddRv.adapter = adapter

            // swap / drag action config
            val swipeGesture = object : SwipeGesture(){
                // longpressed to drag move
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPos = viewHolder.adapterPosition
                    val toPos = target.adapterPosition

                    Collections.swap(runData, fromPos, toPos)
                    adapter.notifyItemMoved(fromPos, toPos)

                    return false
                }

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG)
                        viewHolder?.itemView?.alpha = 0.5f
                    super.onSelectedChanged(viewHolder, actionState)
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    viewHolder.itemView?.alpha = 1.0f
                    super.clearView(recyclerView, viewHolder)
                }



                // swipe left to delete
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    when (direction){
                        ItemTouchHelper.LEFT -> {
                            adapter.deleteItem(viewHolder.adapterPosition)
                        }
                    }
                    super.onSwiped(viewHolder, direction)
                }
            }
            ItemTouchHelper(swipeGesture).attachToRecyclerView(binding.presetAddRv)



            (requireActivity() as AppCompatActivity).supportActionBar!!.title = curTitle
        }

    }



    private fun initBtn() {

        val npAddDist = binding.presetAddNpdist
        val npAddRest = binding.presetAddNprest

        //numberpicker  distance items
        npAddDist.apply {
            minValue = 0
            maxValue = npItemDist.size-1
            displayedValues = npItemDist
            wrapSelectorWheel = false
            textSize = 60f
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            isVerticalFadingEdgeEnabled = true
        }

        //numberpicker rest items
        npAddRest.apply {
            minValue = 0
            maxValue = npItemRest.size-1
            displayedValues = npItemRest
            wrapSelectorWheel = false
            textSize = 60f
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }

        //dist+ btn
        binding.presetAddBtnDist.setOnClickListener {
            runData.add(SingleRun(false, npItemDist[npAddDist.value], "0"))
            adapter.notifyItemInserted(runData.size-1)
            binding.presetAddRv.scrollToPosition(runData.size-1)
        }
//        rest+ btn
        binding.presetAddBtnRest.setOnClickListener {
            runData.add(SingleRun(true,"0",  npItemRest[npAddRest.value]))
            adapter.notifyItemInserted(runData.size-1)
            binding.presetAddRv.scrollToPosition(runData.size-1)
        }

        //Done btn
        binding.presetAddDone.setOnClickListener {

            // if theres no break between runs,
            var inOrder = true
            var isR = runData[0].isRest
            for (i in 1..< runData.size){
                if (runData[i].isRest == isR) inOrder = false
                isR = runData[i].isRest
            }

            //if no items added, toast a message.
            if (adapter.itemCount == 0 || !inOrder) {
                Toast.makeText(requireActivity(),
                    getString(R.string.preset_add_noItem),
                    Toast.LENGTH_SHORT).show()

            }
            else { //if no title entered, add default title. add/update the list. back to preset
                thread{
                    var title = binding.presetAddEnterTitle.text.toString()
                    if (title == "") title = getString(R.string.preset_add_defaultTitle)

                    //if new, add new Entity. if existing, update the previous.
                    if (curPos == -1)
                        presetDao.insertPreset(PresetEntity(null,title,runData))
                    else {
                        presetEntity.title = title
                        presetDao.updatePreset(presetEntity)
                    }
                }
                backToPreset()
            }
        }
        //back btn
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
                            "${presetEntity.title} ${getString(R.string.preset_add_deleted)}",
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