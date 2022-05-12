package com.example.monopolymanager.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import com.example.monopolymanager.viewmodels.GeneralDetailViewModel
import com.example.monopolymanager.R
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.groupDao
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.database.userDao
import com.example.monopolymanager.databinding.GeneralDetailFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar


class GeneralDetail : Fragment() {

    companion object {
        fun newInstance() = GeneralDetail()
    }

    lateinit var binding: GeneralDetailFragmentBinding

    private lateinit var viewModel: GeneralDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GeneralDetailFragmentBinding.inflate(layoutInflater)
        viewModel = GeneralDetailViewModel(context)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val nameId = resources.getIdentifier("com.example.monopolymanager:string/${viewModel.property?.name}", null, null);
        binding.propertyName.text = resources.getString(nameId)
        "${getString(R.string.price)} ${viewModel.property?.price}".also { binding.price.text = it }
        "${getString(R.string.rent)} ${viewModel.property?.getRentPrice()}".also { binding.currentRent.text = it }
        "${getString(R.string.houses)} ${viewModel.property?.houses}".also { binding.housesAmt.text = it }

        "${getString(R.string.mortgagePrice)} ${viewModel.property?.mortgage}".also { binding.mortgageTxt.text = it }
        if (viewModel.property!!.isMortgaged) {
            binding.isMortgagedTxt2.text = resources.getString(R.string.isMortgaged)
        } else {
            binding.isMortgagedTxt2.text = ""
        }

        binding.mortgageBtn.setOnClickListener {
            if (!viewModel.property!!.isMortgaged) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.mortgage))
                    .setMessage(getString(R.string.mortgageDetails))
                    .setPositiveButton(getString(R.string.ok)) { _, _ -> doMortgage() }
                    .setNegativeButton(getString(R.string.cancel), /* listener = */ null)
                    .show();
            } else doMortgage()
        }

        binding.sellBtn.setOnClickListener {
            if (viewModel.property!!.isMortgaged) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.isMortgaged))
                    .setMessage(getString(R.string.unmortgageFirst))
                    .setPositiveButton(getString(R.string.ok)) { _, _ -> null }
                    .show();
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.sell))
                    .setMessage(getString(R.string.sellDetails))
                    .setPositiveButton(getString(R.string.ok)) { _, _ ->
                        viewModel.sell()
                        binding.root.findNavController().navigate(DetailDirections.actionDetailToAddEdit(isAdd = false, property = viewModel.property))
                    }
                    .setNegativeButton(getString(R.string.cancel), /* listener = */ null)
                    .show();
            }
        }

        binding.editBtn.setOnClickListener {
            binding.root.findNavController().navigate(DetailDirections.actionDetailToAddEdit(isAdd = false, property = viewModel.property))
        }
    }

    fun doMortgage(): DialogInterface.OnClickListener? {
        val succesful = viewModel.mortgage()
        if (viewModel.property!!.isMortgaged) {
            binding.isMortgagedTxt2.text = resources.getString(R.string.isMortgaged)
        } else {
            binding.isMortgagedTxt2.text = ""
        }
        if (!succesful!!) {
            Snackbar.make(
                binding.root,
                getString(R.string.notEnoughCash),
                Snackbar.LENGTH_SHORT
            ).show()
        } else {
            viewModel.update()
        }
        return null
    }

}