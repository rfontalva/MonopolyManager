package com.example.monopolymanager.fragments

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.monopolymanager.R
import com.example.monopolymanager.databinding.ProfileFragmentBinding
import com.example.monopolymanager.viewmodels.ProfileViewModel
import com.google.android.material.snackbar.Snackbar

private var PREF_NAME = "MONOPOLY"
class ProfileFragment : Fragment() {

    private var isOpen = false
    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim)}
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim)}
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim)}
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim)}
    lateinit var sharedPref: SharedPreferences

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var viewModel: ProfileViewModel
    lateinit var binding: ProfileFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ProfileFragmentBinding.inflate(layoutInflater)
        viewModel = ProfileViewModel(requireContext())
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        sharedPref = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        binding.menuBtn2.setOnClickListener {
            toggleVisibility()
            toggleAnimations()
            isOpen = !isOpen
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { stillLoading ->
            if (!stillLoading) {
                val avatarId = viewModel.getAvatar()
                binding.avatarImg.setImageResource(avatarId)
                setUpSpinners(avatarId)
            }

        }

        binding.settingsBtn.setOnClickListener {
            binding.root.findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToSettingsActivity())
        }

        binding.logOutBtn.setOnClickListener {
            if (viewModel.logOut()) {
                requireActivity().finish()
            }
            else {
                Snackbar.make(binding.root, "NOPE", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    fun setUpSpinners(avatarId: Int) {
        val avatars = mutableListOf("dog", "car", "hat", "shoe", "boat")
        val avatarIds : MutableList<Int> = mutableListOf()
        val avatarNames : MutableList<String> = mutableListOf()
        avatars.forEach {
            avatarIds.add(resources.getIdentifier("com.example.monopolymanager:drawable/${it}", null, null))
            avatarNames.add(getString(resources.getIdentifier("com.example.monopolymanager:string/${it}", null, null)))
        }

        val index = avatarIds.indexOf(avatarId)

        val avatarSpinnerAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_spinner_item, avatarNames)
        avatarSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.avatarSpinner.adapter = avatarSpinnerAdapter
        binding.avatarSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                val idImg = avatarIds[position]
                binding.avatarImg.setImageResource(idImg)
                viewModel.updateAvatar(idImg)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                //
            }
        })

        binding.avatarSpinner.setSelection(index)
    }

    private fun toggleVisibility() {
        if (!isOpen) {
            binding.settingsBtn.visibility = View.VISIBLE
            binding.logOutBtn.visibility = View.VISIBLE
            binding.settingsBtn.isClickable = true
            binding.logOutBtn.isClickable = true
        } else {
            binding.settingsBtn.visibility = View.GONE
            binding.logOutBtn.visibility = View.GONE
            binding.settingsBtn.isClickable = false
            binding.logOutBtn.isClickable = false
        }
    }

    private fun toggleAnimations() {
        if (!isOpen) {
            binding.settingsBtn.startAnimation(fromBottom)
            binding.logOutBtn.startAnimation(fromBottom)
            binding.menuBtn2.startAnimation(rotateOpen)
        } else {
            binding.settingsBtn.startAnimation(toBottom)
            binding.logOutBtn.startAnimation(toBottom)
            binding.menuBtn2.startAnimation(rotateClose)
        }
    }

}