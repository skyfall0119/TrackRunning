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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaykim.trackrunning.databinding.FragmentActivityBinding
import com.jaykim.trackrunning.db.AppDatabase
import com.jaykim.trackrunning.db.RunsDao
import com.jaykim.trackrunning.db.RunsEntity



class ActivityFragment : Fragment() {


    private var _binding: FragmentActivityBinding? = null
    private val binding get() = _binding!!
    private lateinit var db : AppDatabase
    private lateinit var runsDao: RunsDao
    private lateinit var currentRun : RunsEntity
    private lateinit var adapter : FinishedActivityRvAdapter
    private var runDataMap = mutableMapOf<String, Array<String>>()
    private var selectedPos =0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentActivityBinding.inflate(inflater, container, false)
        val view = binding.root

        initDb()
        initBtn()


        return view
    }



    private fun initDb() {
        Thread{
            //get position.
            val args = this.arguments
            val curPos = args?.getInt("position")!!

            //retrieve data
            db = AppDatabase.getInstance(requireContext() )!!
            runsDao = db.getRunsDao()
            currentRun = runsDao.getAllRuns()[curPos]

            calcData()
            initView()

        }.start()
    }

   //fastest average calc
    private fun calcData() {
        val sortData = mutableMapOf<String,ArrayList<Int>>()

        //sort by distance
        for (singleRun in currentRun.singleWorkout) {
            if (sortData.containsKey(singleRun.distance)) {
                sortData[singleRun.distance]?.add(singleRun.msTime)
            }else{
                val arl = ArrayList<Int>()
                arl.add(singleRun.msTime)
                sortData[singleRun.distance] = arl
            }
        }

        sortData.keys.forEach {key->
            var sum = 0
            var fastest = sortData[key]!![0]
            for (e in sortData[key]!!)  {
                sum += e
                if (e < fastest ) fastest = e
            }
            var avg = sum / sortData[key]!!.size
            runDataMap[key] = arrayOf(Helper.intTimeToStr(fastest),Helper.intTimeToStr(avg))
        }
    }


    private fun initView() {
        //recyclerView. update the UI
        activity?.runOnUiThread {
            adapter = FinishedActivityRvAdapter(currentRun.singleWorkout)
            binding.apply{
                activityRv.adapter = adapter
                activityRv.layoutManager = LinearLayoutManager(requireActivity())
                activityDate.text = currentRun.date
                activityTitle.text = currentRun.title
                tvTotalTime2.text = currentRun.totalTime
                tvTotalDist2.text = currentRun.totalDist
            }



            //fastest, average time onclicklistener
            adapter.setOnItemClickListener(object : ActivitiesRvAdapter.onItemClickListener{
                override fun onItemClick(view: View, position: Int) {
                    adapter.selectedPos = position
//                    adapter.notifyDataSetChanged()
                    adapter.notifyItemChanged(selectedPos)
                    adapter.notifyItemChanged(position)
                    selectedPos = position

                    binding.tvFastestLaptime2.text = runDataMap[currentRun.singleWorkout[position].distance]?.get(0)
                    binding.tvAvgLaptime2.text = runDataMap[currentRun.singleWorkout[position].distance]?.get(1)
                }
            })




            //update actionbar
            (requireActivity() as AppCompatActivity).supportActionBar!!.title = currentRun.title
            setHasOptionsMenu(true)
        }
    }



    //back button. delete button.
    private fun initBtn() {
        binding.btnActivityBack.setOnClickListener{
            backToActivities()
        }
    }


    private fun backToActivities(){
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container,ActivitiesFragment())
        transaction.commit()
        (requireActivity() as AppCompatActivity).supportActionBar!!.setTitle(R.string.menu_activities)
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
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.activity_delete_yes)) { _, _ ->
                        //delete and return to activity
                        Thread{
                            runsDao.deleteRuns(currentRun)
                        }.start()

                        Toast.makeText(requireActivity(),
                            "${currentRun.date} ${currentRun.title} ${getString(R.string.activity_deleted)}",
                            Toast.LENGTH_LONG).show()
                        backToActivities()
                    }

                    .setNegativeButton(getString(R.string.activity_delete_no)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create().show()
            }
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}

