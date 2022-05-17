package com.example.monopolymanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.monopolymanager.R
import com.example.monopolymanager.databinding.AddEditFragmentBinding
import com.example.monopolymanager.viewmodels.AddEditViewModel
import com.example.monopolymanager.viewmodels.AddViewModel
import com.example.monopolymanager.viewmodels.EditViewModel
import com.google.android.material.snackbar.Snackbar


class AddEdit : Fragment() {

    lateinit var binding: AddEditFragmentBinding
    var isAdd : Boolean = true
    var idUser : Int = 0

    companion object {
        fun newInstance() = AddEdit()
    }

    private lateinit var viewModel: AddEditViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddEditFragmentBinding.inflate(layoutInflater)
        isAdd = AddEditArgs.fromBundle(requireArguments()).isAdd
        if (isAdd) {
            binding.addEditTitle.text = getString(R.string.add)
            viewModel = AddViewModel(requireContext())
        } else {
            binding.addEditTitle.text = getString(R.string.edit)
            viewModel = EditViewModel(requireContext(), AddEditArgs.fromBundle(requireArguments()).property)
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        setUpButtons()
        setUpSpinners()

        if (!isAdd) {
            updateLabels()
            if (viewModel.hasHotel()) {
                binding.housesAmtTxt.text = getString(R.string.hotel)
            }
            else {
                binding.housesAmtTxt.text = "${viewModel.getHousesAmt()}"
            }
        }
    }

    private fun setUpButtons() {
        binding.addHouseBtn.setOnClickListener {
            when {
                viewModel.addHouse() -> {
                    updateLabels()
                    binding.housesAmtTxt.text = "${viewModel.getHousesAmt()}"
                }
                viewModel.hasHotel() -> {
                    binding.housesAmtTxt.text = getString(R.string.hotel)
                    updateLabels()
                }
                else -> {
                    Snackbar.make(binding.root,getString(viewModel.addHousesMsg), Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        binding.rmvHouseBtn.setOnClickListener {
            when {
                viewModel.removeHouse() -> {
                    updateLabels()
                    binding.housesAmtTxt.text = "${viewModel.getHousesAmt()}"
                }
                viewModel.getHousesAmt() == 0 -> {
                    //do nothing
                }
                else -> {
                    Snackbar.make(binding.root,getString(viewModel.removeHousesMsg), Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        binding.buyBtn.setOnClickListener {
            if (viewModel.property != null) {
                val (cash, canPay) = viewModel.canBuy()
                if (canPay) {
                    binding.root.findNavController()
                        .navigate(AddEditDirections.actionAddEditToHome2())
                }
                else {
                    Snackbar.make(binding.root,"${getString(R.string.notEnoughCash)} (${cash})", Snackbar.LENGTH_SHORT).show()
                }
            }
            else {
                Snackbar.make(binding.root,getString(R.string.selectFirst), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpSpinners() {
        val colorSpinnerAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_spinner_item, viewModel.getColorArray())
        colorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.colorSpinner.adapter = colorSpinnerAdapter
        if (!isAdd) {
            val propertySpinnerAdapter = ArrayAdapter(
                binding.root.context,
                android.R.layout.simple_spinner_item,
                viewModel.getNameArray(0)
            )
            binding.nameSpinner.adapter = propertySpinnerAdapter
        }

        if (isAdd) {
            binding.colorSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    val propertySpinnerAdapter = ArrayAdapter(binding.root.context, android.R.layout.simple_spinner_item, viewModel.getNameArray(position))
                    propertySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.nameSpinner.adapter = propertySpinnerAdapter
                    binding.nameSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(
                            parentView: AdapterView<*>?,
                            selectedItemView: View?,
                            position: Int,
                            id: Long
                        ) {
                            viewModel.setProperty(position)
                            updateLabels()
                            if (viewModel.hasHotel()) {
                                binding.housesAmtTxt.text = getString(R.string.hotel)
                            }
                            else {
                                binding.housesAmtTxt.text = "${viewModel.getHousesAmt()}"
                            }
                        }
                        override fun onNothingSelected(parentView: AdapterView<*>?) {
                        }
                    })
                }
                override fun onNothingSelected(parentView: AdapterView<*>?) {
                    binding.nameSpinner.isEnabled = false
                }
            })
        } else {
            binding.nameSpinner.isEnabled = false
            binding.colorSpinner.isEnabled = false
        }
    }

    private fun updateLabels() {
        "${getString(R.string.price)} ${viewModel.getPrice()}".also { binding.priceTxt.text = it}
        "${getString(R.string.rent)} ${viewModel.getRentPrice()}".also { binding.rentTxt.text = it}
    }
}