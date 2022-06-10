package com.example.monopolymanager.fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.monopolymanager.Login
import com.example.monopolymanager.R
import com.example.monopolymanager.databinding.LoginFragmentBinding
import com.example.monopolymanager.entities.Group
import com.example.monopolymanager.entities.Property
import com.example.monopolymanager.entities.PropertyBasic
import com.example.monopolymanager.entities.User
import com.example.monopolymanager.viewmodels.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


private var PREF_NAME = "MONOPOLY"


class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = Login()
    }

    private lateinit var foundEmail : String
    private lateinit var binding: LoginFragmentBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(layoutInflater)
        viewModel = LoginViewModel(requireContext())

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)

        val navController = binding.root.findNavController()
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val stayLoggedPref = sharedPref.getBoolean("stayLoggedIn", false)

        viewModel.success.observe(viewLifecycleOwner) { result ->
            if (result == true) {
                val editor = sharedPref.edit()
                val user = User("r", foundEmail)
                binding.username.isEnabled = true
                binding.password.isEnabled = true
                editor.putString("username", user.getUsername()!!)
                editor.putString("language", "es")
                editor.apply()
                val action = LoginFragmentDirections.actionLoginToMainActivity()
                navController.navigate(action)
            } else if (result == false) {
                foundEmail = ""
                Snackbar.make(binding.root, getString(R.string.wrongUser), Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        viewModel.email.observe(viewLifecycleOwner) { email ->
            scope.launch {
                if (email != null) {
                    foundEmail = email
                    viewModel.validate(email, binding.password.text.toString())
                }
            }
        }

        if (stayLoggedPref) {
            val action = LoginFragmentDirections.actionLoginToMainActivity()
            navController.navigate(action)
        }

        binding.btnSubmit.setOnClickListener {
            val username = binding.username.text.toString()
            binding.username.isEnabled = false
            binding.password.isEnabled = false
            viewModel.getEmail(username)
        }

        binding.toRegisterBtn.setOnClickListener {
            navController.navigate(LoginFragmentDirections.actionLoginToRegister())
        }
    }
}