package com.example.monopolymanager.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.monopolymanager.R
import com.example.monopolymanager.databinding.GeneralDetailFragmentBinding
import com.example.monopolymanager.entities.Property
import com.example.monopolymanager.entities.QRData
import com.example.monopolymanager.viewmodels.GeneralDetailViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class GeneralDetail(val property: Property?) : Fragment() {

    companion object {
        fun newInstance() = GeneralDetail(null)
    }

    lateinit var binding: GeneralDetailFragmentBinding

    private lateinit var viewModel: GeneralDetailViewModel

    private var isOpen = false

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim)}
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim)}
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim)}
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GeneralDetailFragmentBinding.inflate(layoutInflater)
        viewModel = GeneralDetailViewModel(requireContext(), property)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)

        viewModel.sellSuccess.observe(viewLifecycleOwner) { result ->
            if (result == true) {
                binding.root.findNavController().navigate(DetailDirections.actionDetailToHome2())
            }
        }


        binding.menuBtn.setOnClickListener {
            toggleVisibility()
            toggleAnimations()
            isOpen = !isOpen
        }

        val nameId = resources.getIdentifier("com.example.monopolymanager:string/${viewModel.property?.name}", null, null);
        binding.propertyName.text = resources.getString(nameId)
        "${getString(R.string.price)} ${viewModel.property?.price}".also { binding.price.text = it }
        "${getString(R.string.rent)} ${viewModel.getRentPrice()}".also { binding.currentRent.text = it }
        if (viewModel.property!!.hasHotel)
            "${getString(R.string.houses)} ${getString(R.string.hotel)}".also { binding.housesAmt.text = it }
        else
            "${getString(R.string.houses)} ${viewModel.getHouses()}".also { binding.housesAmt.text = it }


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
                    .show()
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.sell))
                    .setMessage(getString(R.string.sellDetails))
                    .setPositiveButton(getString(R.string.ok)) { _, _ ->
                        scope.launch {
                            viewModel.sell()
                        }
                    }
                    .setNegativeButton(getString(R.string.cancel), /* listener = */ null)
                    .show()
            }
        }

        binding.editBtn.setOnClickListener {
            binding.root.findNavController().navigate(DetailDirections.actionDetailToAddEdit(isAdd = false, property = viewModel.property))
        }

        val qrView = layoutInflater.inflate(R.layout.qr, null)
        val qrImg = qrView.findViewById<ImageView>(R.id.imageView2)
        qrImg.setImageBitmap(QRData.createQRData(viewModel.getGame(), viewModel.getUsername(), viewModel.getRentPrice()))

        binding.generateQRBtn.setOnClickListener{
            MaterialAlertDialogBuilder(requireContext())
                .setView(qrView)
                .setMessage("Cobrar")
                .setNegativeButton(getString(R.string.cancel), /* listener = */ null)
                .show()
        }
    }

    private fun doMortgage(): DialogInterface.OnClickListener? {
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
//            viewModel.update()
        }
        return null
    }

    private fun toggleVisibility() {
        if (!isOpen) {
            binding.sellBtn.visibility = View.VISIBLE
            binding.mortgageBtn.visibility = View.VISIBLE
            binding.generateQRBtn.visibility = View.VISIBLE
            binding.editBtn.visibility = View.VISIBLE
            binding.mortgageBtn.isClickable = true
            binding.generateQRBtn.isClickable = true
            binding.editBtn.isClickable = true
            binding.sellBtn.isClickable = true
        } else {
            binding.sellBtn.visibility = View.GONE
            binding.mortgageBtn.visibility = View.GONE
            binding.generateQRBtn.visibility = View.GONE
            binding.editBtn.visibility = View.GONE
            binding.mortgageBtn.isClickable = false
            binding.generateQRBtn.isClickable = false
            binding.editBtn.isClickable = false
            binding.sellBtn.isClickable = false
        }
    }

    private fun toggleAnimations() {
        if (!isOpen) {
            binding.sellBtn.startAnimation(fromBottom)
            binding.generateQRBtn.startAnimation(fromBottom)
            binding.mortgageBtn.startAnimation(fromBottom)
            binding.editBtn.startAnimation(fromBottom)
            binding.menuBtn.startAnimation(rotateOpen)
        } else {
            binding.sellBtn.startAnimation(toBottom)
            binding.generateQRBtn.startAnimation(toBottom)
            binding.mortgageBtn.startAnimation(toBottom)
            binding.editBtn.startAnimation(toBottom)
            binding.menuBtn.startAnimation(rotateClose)
        }
    }
}