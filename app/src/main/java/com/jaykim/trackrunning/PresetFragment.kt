package com.jaykim.trackrunning

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaykim.trackrunning.databinding.FragmentPresetBinding
import com.jaykim.trackrunning.db.AppDatabase
import com.jaykim.trackrunning.db.PresetDao
import com.jaykim.trackrunning.db.PresetEntity
import kotlin.concurrent.thread


class PresetFragment : Fragment() {

    private var _binding: FragmentPresetBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PresetRvAdapter
    private lateinit var db : AppDatabase
    private lateinit var presetDao : PresetDao
    private lateinit var presetList : List<PresetEntity>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPresetBinding.inflate(inflater, container, false)
        val view = binding.root


        initDb()
        initBtn()

        return view
    }



    private fun initDb() {
        thread {
            db = AppDatabase.getInstance(requireContext())!!
            presetDao = db.getPresetDao()
            presetList = presetDao.getAllPreset()
            initView()
        }
    }

    private fun initView() {
        activity?.runOnUiThread {
            adapter = PresetRvAdapter(presetList)
            binding.presetRv.adapter = adapter
            binding.presetRv.layoutManager = LinearLayoutManager(context)

            if (adapter.itemCount == 0) {
                binding.presetRv.visibility = View.GONE
                binding.presetNoData.visibility = View.VISIBLE
            } else {
                binding.presetRv.visibility = View.VISIBLE
                binding.presetNoData.visibility = View.GONE
            }


            //onClickListener for recyclerview item.
            adapter.setOnItemClickListener(object : PresetRvAdapter.onItemClickListener{

                override fun onItemClick(view: View, position: Int) {
                    val bundle = Bundle()
                    bundle.putInt("position",position)
                    val singleFrag = PresetAddFragment()
                    singleFrag.arguments = bundle
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragment_container,singleFrag)
                    transaction.addToBackStack(null)
                    transaction.commit()

                }
            })
        }
    }


    private fun initBtn() {
        binding.presetFabAdd.setOnClickListener {
            var bundle = Bundle()
            bundle.putInt("position",-1)
            val singleFrag = PresetAddFragment()
            singleFrag.arguments = bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container,singleFrag)
            transaction.addToBackStack(null)
            transaction.commit()
        }

    }






    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}