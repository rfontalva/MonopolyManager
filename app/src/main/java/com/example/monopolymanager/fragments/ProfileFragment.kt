package com.example.monopolymanager.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.findNavController
import com.example.monopolymanager.databinding.ProfileFragmentBinding
import com.example.monopolymanager.viewmodels.ProfileViewModel

private var PREF_NAME = "MONOPOLY"
class ProfileFragment : Fragment() {

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

        val avatarId = viewModel.getAvatar()
        binding.avatarImg.setImageResource(avatarId)
        val avatars = mutableListOf("dog", "car", "hat", "shoe")
        val avatarIds : MutableList<Int> = mutableListOf()
        val avatarNames : MutableList<String> = mutableListOf()
        avatars.forEach {
            avatarIds.add(resources.getIdentifier("com.example.monopolymanager:drawable/${it}", null, null))
            avatarNames.add(getString(resources.getIdentifier("com.example.monopolymanager:string/${it}", null, null)))
        }

        val index = avatarIds.indexOf(avatarId)
        binding.avatarSpinner.setSelection(index)
        val avatarSpinnerAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_spinner_item, avatarNames)
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

        binding.editMail.setText(viewModel.getEmail())

        binding.changeBtn.setOnClickListener {
            val text = binding.editMail.text.toString()
            viewModel.updateEmail(text)
        }

        binding.settingsBtn.setOnClickListener {
            binding.root.findNavController().navigate(ProfileFragmentDirections.actionSettingsFragmentToSettingsActivity())
        }

        binding.logOutBtn.setOnClickListener {
            val sharedPref: SharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean("stayLoggedIn", false)
            editor.apply()
            this.activity?.finish()
        }
    }

}