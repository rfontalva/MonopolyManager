package com.example.monopolymanager.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.monopolymanager.R
import com.example.monopolymanager.database.groupDao
import com.example.monopolymanager.databinding.DetailFragmentBinding
import com.example.monopolymanager.viewmodels.DetailViewModel
import com.google.android.material.tabs.TabLayoutMediator


private var PREF_NAME = "MONOPOLY"

class Detail : Fragment() {

    private lateinit var binding : DetailFragmentBinding
    private var db: Any? = null
    private var groupDao: groupDao? = null
    private lateinit var viewModel : DetailViewModel

    companion object {
        fun newInstance() = Detail()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DetailFragmentBinding.inflate(layoutInflater)
        viewModel = DetailViewModel(requireContext(), DetailArgs.fromBundle(requireArguments()).property)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.viewPager.adapter = ViewPagerAdapter(requireActivity())
        // viewPager.isUserInputEnabled = false

        TabLayoutMediator(
            binding.tabLayout,
            binding.viewPager
        ) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.general)
                1 -> tab.text = getString(R.string.title)
                else -> tab.text = "undefined"
            }
        }.attach()
    }


    class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun createFragment(position: Int): Fragment {

            return when(position){
                0 -> GeneralDetail()
                1 -> SellDetail()

                else -> GeneralDetail()
            }
        }

        override fun getItemCount(): Int {
            return TAB_COUNT
        }

        companion object {
            private const val TAB_COUNT = 2
        }
    }
}