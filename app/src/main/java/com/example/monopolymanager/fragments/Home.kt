package com.example.monopolymanager.fragments

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monopolymanager.R
import com.example.monopolymanager.databinding.HomeFragmentBinding
import com.example.monopolymanager.adapters.PropertyAdapter
import com.example.monopolymanager.entities.Game
import com.example.monopolymanager.utils.convertPixelsToDp
import com.example.monopolymanager.viewmodels.HomeViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


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
        val db = Firebase.firestore


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
                    "$${viewModel.user!!.getCash()}".also { binding.cashTxt.text = it }
                }
            }
        }

        binding.addButton.setOnClickListener {
            val navController = binding.root.findNavController()
            navController.navigate(HomeDirections.actionHome2ToAddEdit(isAdd = true))
        }
    }

    fun onItemClick(position: Int) {
        binding.root.findNavController().navigate(HomeDirections.actionHome2ToDetail(property = viewModel.getProperties()!![position]))
    }
}