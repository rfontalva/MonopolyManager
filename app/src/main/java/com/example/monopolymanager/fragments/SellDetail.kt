package com.example.monopolymanager.fragments

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.monopolymanager.R
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.groupDao
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.database.userDao
import com.example.monopolymanager.databinding.SellDetailFragmentBinding
import com.example.monopolymanager.entities.Property
import com.example.monopolymanager.entities.User
import com.example.monopolymanager.viewmodels.SellDetailViewModel
import com.google.android.material.snackbar.Snackbar


class SellDetail : Fragment() {

    companion object {
        fun newInstance() = SellDetail()
    }
    private lateinit var binding: SellDetailFragmentBinding
    private lateinit var viewModel: SellDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = SellDetailFragmentBinding.inflate(layoutInflater)
        viewModel = SellDetailViewModel(context)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        "${getString(R.string.mortgagePrice)} ${viewModel.property?.mortgage}".also { binding.mortgageTxt.text = it }
        if (viewModel.property!!.isMortgaged) {
            binding.mortgageBtn.text = getString(R.string.unmortgage)
        }
        binding.mortgageBtn.setOnClickListener {
            val succesful = viewModel.mortgage()
            if (viewModel.isMortgaged()) {
                binding.mortgageBtn.text = getString(R.string.unmortgage)
            } else {
                binding.mortgageBtn.text = getString(R.string.mortgage)
            }
            if (!succesful!!) {
                Snackbar.make(binding.root,getString(R.string.notEnoughCash), Snackbar.LENGTH_SHORT).show()
            } else {
                viewModel.update()
            }
        }
    }

}