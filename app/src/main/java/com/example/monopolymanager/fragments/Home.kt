package com.example.monopolymanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monopolymanager.R
import com.example.monopolymanager.databinding.HomeFragmentBinding
import com.example.monopolymanager.adapters.PropertyAdapter
import com.example.monopolymanager.entities.Game
import com.example.monopolymanager.utils.convertPixelsToDp
import com.example.monopolymanager.viewmodels.HomeViewModel



class Home : Fragment() {

    private var isOpen = false
    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim)}
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim)}
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim)}
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim)}


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
        isOpen = false
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.menuBtn3.setOnClickListener {
            toggleVisibility()
            toggleAnimations()
            isOpen = !isOpen
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (!isLoading) {
                binding.loadingHome.visibility = View.GONE
                val avatarId = viewModel.getAvatar()
                binding.avatarImgHome.setImageResource(avatarId)
                if (viewModel.user != null) {
                    binding.propertiesList.setHasFixedSize(true)
                    binding.propertiesList.layoutManager = LinearLayoutManager(context)
                    binding.propertiesList.adapter = PropertyAdapter(viewLifecycleOwner, viewModel.getProperties()){ index->
                        onItemClick(index)
                    }
                    var username = viewModel.user!!.getUsername()!!
                    "${getString(R.string.greeting)} ${username}!".also { binding.greeting.text = it }
                    binding.greeting.measure(0, 0)
                    while (convertPixelsToDp(binding.greeting.measuredWidth.toFloat(), requireContext()) >= 144) {
                        "$username".also { binding.greeting.text = it }
                        username = username.replace("...","")
                        username = "${username.slice(username.indices - 1)}..."
                        binding.greeting.measure(0, 0)
                    }
                    "$${viewModel.getCash()}".also { binding.cashTxt.text = it }
                }
            }
        }

        binding.addButton.setOnClickListener {
            val navController = binding.root.findNavController()
            navController.navigate(HomeDirections.actionHome2ToAddEdit(isAdd = true))
        }

        binding.scanButton.setOnClickListener {
            val navController = binding.root.findNavController()
            navController.navigate(HomeDirections.actionHome2ToQRScanner())
        }
    }

    fun onItemClick(position: Int) {
        binding.root.findNavController().navigate(HomeDirections.actionHome2ToDetail(property = viewModel.getProperties()!![position]))
    }

    private fun toggleVisibility() {
        if (!isOpen) {
            binding.scanButton.visibility = View.VISIBLE
            binding.addButton.visibility = View.VISIBLE
            binding.scanButton.isClickable = true
            binding.addButton.isClickable = true
        } else {
            binding.scanButton.visibility = View.GONE
            binding.addButton.visibility = View.GONE
            binding.scanButton.isClickable = false
            binding.addButton.isClickable = false
        }
    }

    private fun toggleAnimations() {
        if (!isOpen) {
            binding.scanButton.startAnimation(fromBottom)
            binding.addButton.startAnimation(fromBottom)
            binding.menuBtn3.startAnimation(rotateOpen)
        } else {
            binding.scanButton.startAnimation(toBottom)
            binding.addButton.startAnimation(toBottom)
            binding.menuBtn3.startAnimation(rotateClose)
        }
    }
}