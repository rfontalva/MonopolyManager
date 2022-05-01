package com.example.monopolymanager.fragments

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.monopolymanager.R
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.groupDao
import com.example.monopolymanager.database.userDao
import com.example.monopolymanager.viewmodels.DetailViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


private var PREF_NAME = "MONOPOLY"

class Detail : Fragment() {

    private var db: appDatabase? = null
    private var groupDao: groupDao? = null
    lateinit var v: View
    lateinit var viewPager: ViewPager2
    lateinit var tabLayout: TabLayout
    lateinit var headerColor : ImageView

    companion object {
        fun newInstance() = Detail()
    }

    private lateinit var viewModel: DetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.detail_fragment, container, false)
        tabLayout = v.findViewById(R.id.tab_layout)
        viewPager = v.findViewById(R.id.viewPager)
        headerColor = v.findViewById(R.id.headerColor)

        db = appDatabase.getAppDataBase(v.context)
        groupDao = db?.groupDao()
        val property = DetailArgs.fromBundle(requireArguments()).property
        val color = groupDao?.getColor(property.group)
        val colorName = groupDao?.getColorName(property.group)
        headerColor.contentDescription = "${colorName}"
        headerColor.setBackgroundColor(Color.parseColor(color!!))
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("idProperty", property!!.idProperty)
        editor.apply()

        return v
    }

    override fun onStart() {
        super.onStart()

        viewPager.setAdapter(ViewPagerAdapter(requireActivity()))
        // viewPager.isUserInputEnabled = false

        TabLayoutMediator(tabLayout, viewPager, TabLayoutMediator.TabConfigurationStrategy { tab, position ->
            when (position) {
                0 -> tab.text = "${getString(R.string.general)}"
                1 -> tab.text = "${getString(R.string.sell)}"
                else -> tab.text = "undefined"
            }
        }).attach()
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