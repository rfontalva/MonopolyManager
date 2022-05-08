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
import androidx.navigation.findNavController
import com.example.monopolymanager.viewmodels.GeneralDetailViewModel
import com.example.monopolymanager.R
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.groupDao
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.database.userDao
import com.example.monopolymanager.databinding.GeneralDetailFragmentBinding

private var PREF_NAME = "MONOPOLY"

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
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val idProperty = sharedPref.getInt("idProperty", -1)
        viewModel = GeneralDetailViewModel(context, idProperty)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val nameId = resources.getIdentifier("com.example.monopolymanager:string/${viewModel.property?.name}", null, null);
        binding.propertyName.text = resources.getString(nameId)
        "${getString(R.string.price)} ${viewModel.property?.price}".also { binding.price.text = it }
        "${getString(R.string.rent)} ${viewModel.property?.getRentPrice()}".also { binding.currentRent.text = it }
        "${getString(R.string.houses)} ${viewModel.property?.houses}".also { binding.housesAmt.text = it }
        val rentArray = viewModel.property!!.getRentArray()
        "$${ rentArray[0] }".also { binding.noHousesRent.text = it }
        "$${ rentArray[1] }".also { binding.oneHouseRent.text = it }
        "$${ rentArray[2] }".also { binding.twoHouseRent.text = it }
        "$${ rentArray[3] }".also { binding.threeHouseRent.text = it }
        "$${ rentArray[4] }".also { binding.fourHouseRent.text = it }
        "$${viewModel.property!!.rentHotel}".also { binding.hotelRent.text = it }

        binding.editBtn.setOnClickListener {
            binding.root.findNavController().navigate(DetailDirections.actionDetailToAddEdit(isAdd = false, property = viewModel.property))
        }
    }

}