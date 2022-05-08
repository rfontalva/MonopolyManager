package com.example.monopolymanager.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.monopolymanager.R
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.database.userDao
import com.example.monopolymanager.databinding.HomeFragmentBinding
import com.example.monopolymanager.entities.Property
import com.example.monopolymanager.propertyAdapter.PropertyAdapter
import com.example.monopolymanager.viewmodels.HomeViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


private var PREF_NAME = "MONOPOLY"

class Home : Fragment() {

    companion object {
        fun newInstance() = Home()
    }
    private lateinit var binding: HomeFragmentBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeFragmentBinding.inflate(layoutInflater)
        viewModel = HomeViewModel(requireContext())
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val navController = binding.root.findNavController()

        if (viewModel.user != null) {
            binding.propertiesList.setHasFixedSize(true)
            binding.propertiesList.layoutManager = LinearLayoutManager(context)
            binding.propertiesList.adapter = PropertyAdapter(properties = viewModel.getProperties()){ index->
                onItemClick(index)
            }
            "${getString(R.string.greeting)} ${viewModel.user!!.getUsername()}!".also { binding.greeting.text = it }
            "$${viewModel.user!!.getCash()}".also { binding.cashTxt.text = it }
        }

        binding.addButton.setOnClickListener {
            navController.navigate(HomeDirections.actionHome2ToAddEdit(isAdd = true))
        }
    }

    fun onItemClick(position: Int) {
        binding.root.findNavController().navigate(HomeDirections.actionHome2ToDetail(property = viewModel.getProperties()!![position]))
    }
}