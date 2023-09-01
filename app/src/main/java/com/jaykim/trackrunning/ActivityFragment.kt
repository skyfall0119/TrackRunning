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
            initView()
        }.start()
    }



    private fun initView() {
        //recyclerView. update the UI
        activity?.runOnUiThread {
            adapter = FinishedActivityRvAdapter(currentRun.singleWorkout)
            binding.activityRv.adapter = adapter
            binding.activityRv.layoutManager = LinearLayoutManager(requireActivity())

            binding.activityDate.text = currentRun.date
            binding.activityTitle.text = currentRun.title
            binding.tvTotalTime2.text = currentRun.totalTime
            binding.tvTotalDist2.text = currentRun.totalDist

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
                    .setPositiveButton(getString(R.string.activity_delete_yes)) { dialog, id->
                        //delete and return to activity
                        Thread{
                            runsDao.deleteRuns(currentRun)
                        }.start()

                        Toast.makeText(requireActivity(),
                            "${currentRun.date} ${currentRun.title} deleted",
                            Toast.LENGTH_LONG).show()
                        backToActivities()
                    }

                    .setNegativeButton(getString(R.string.activity_delete_no)) {dialog, id->
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