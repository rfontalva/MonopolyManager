package com.example.monopolymanager.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.monopolymanager.R
import com.example.monopolymanager.databinding.SellDetailFragmentBinding
import com.example.monopolymanager.entities.Property
import com.example.monopolymanager.viewmodels.SellDetailViewModel


class SellDetail(var property: Property?) : Fragment() {

    companion object {
        fun newInstance() = SellDetail(null)
    }
    private lateinit var binding: SellDetailFragmentBinding
    private lateinit var viewModel: SellDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = SellDetailFragmentBinding.inflate(layoutInflater)
        viewModel = SellDetailViewModel(requireContext(), property)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.titleDeedRow.setBackgroundColor(Color.parseColor(viewModel.getColor()))
        binding.propertyNameRow.setBackgroundColor(Color.parseColor(viewModel.getColor()))
        val nameId = resources.getIdentifier("com.example.monopolymanager:string/${property?.name}", null, null);
        binding.propertyNameCard.text = resources.getString(nameId)
        val rentArray = property!!.getRentArray()
        "${getString(R.string.rent)} $${rentArray[0]}".also { binding.rentPrice.text = it }
        "$${ rentArray[1] }".also { binding.oneHouseRent.text = it }
        "$${ rentArray[2] }".also { binding.twoHouseRent.text = it }
        "$${ rentArray[3] }".also { binding.threeHouseRent.text = it }
        "$${ rentArray[4] }".also { binding.fourHouseRent.text = it }
        "$${viewModel.property!!.rentHotel}".also { binding.hotelRent.text = it }
        "$${viewModel.property!!.mortgage}".also { binding.mortgagePrice.text = it }
        ("${getString(R.string.housingPrice1)} ${viewModel.getHousePrice()} ${getString(R.string.housingPrice2)}" +
            "${viewModel.getHousePrice()} ${getString(R.string.housingPrice3)}").also { binding.housingPrice.text = it }
    }

}