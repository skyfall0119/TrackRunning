package com.jaykim.trackrunning

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.jaykim.trackrunning.databinding.FragmentActivitiesBinding
import com.jaykim.trackrunning.db.AppDatabase
import com.jaykim.trackrunning.db.RunsDao
import com.jaykim.trackrunning.db.RunsEntity


class ActivitiesFragment : Fragment() {

    private lateinit var adapter : ActivitiesRvAdapter
    private lateinit var db : AppDatabase
    private lateinit var runsDao: RunsDao
    private lateinit var runsList : List<RunsEntity>

    private var _binding: FragmentActivitiesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentActivitiesBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onResume() {
        super.onResume()
        initDb()

    }


    private fun initDb() {
        Thread{
            db = AppDatabase.getInstance(requireContext() )!!
            runsDao = db.getRunsDao()
            runsList = runsDao.getAllRuns()
            initRecyclerView()
        }.start()
    }

    private fun initRecyclerView() {

        activity?.runOnUiThread {
            adapter = ActivitiesRvAdapter(runsList)
            binding.activitiesRv.adapter = adapter

            if (adapter.itemCount == 0) {
                binding.activitiesRv.visibility = View.GONE
                binding.activitiesNoData.visibility = View.VISIBLE
            } else {
                binding.activitiesRv.visibility = View.VISIBLE
                binding.activitiesNoData.visibility = View.GONE
            }


            //onClickListener for recyclerview item.
            adapter.setOnItemClickListener(object : ActivitiesRvAdapter.onItemClickListener{
                override fun onItemClick(view: View, position: Int) {
                    val bundle = Bundle()
                    bundle.putInt("position",position)
                    val singleFrag = ActivityFragment()
                    singleFrag.arguments = bundle
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.apply {
                        replace(R.id.fragment_container,singleFrag)
                        addToBackStack(null)
                        commit()
                    }
                }
            })
        }



    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}