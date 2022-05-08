package com.example.monopolymanager.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.findNavController
import com.example.monopolymanager.R
import com.example.monopolymanager.viewmodels.LoginViewModel
import com.example.monopolymanager.Login
import com.example.monopolymanager.MainActivity
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.userDao
import com.example.monopolymanager.databinding.LoginFragmentBinding
import com.example.monopolymanager.entities.PropertiesRepository
import com.example.monopolymanager.utils.switchLocal
import com.google.android.material.snackbar.Snackbar

private var PREF_NAME = "MONOPOLY"

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = Login()
    }

    private lateinit var binding: LoginFragmentBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(layoutInflater)
        viewModel = LoginViewModel(context)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val navController = binding.root.findNavController()
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val stayLoggedPref = sharedPref.getBoolean("stayLoggedIn", false)


        if (stayLoggedPref) {
            val action = LoginFragmentDirections.actionLoginToMainActivity()
            navController.navigate(action)
        }
        val isInitialized = sharedPref.getBoolean("isInitialized", false)
        if (!isInitialized) {
            viewModel.initializeProperties()
            val editor = sharedPref.edit()
            editor.putBoolean("isInitialized", true)
            editor.apply()
        }

        binding.btnSubmit.setOnClickListener {
            val user = viewModel.validate(binding.username.text.toString(), binding.password.text.toString())
            if (user != null) {
                val action = LoginFragmentDirections.actionLoginToMainActivity()
                val editor = sharedPref.edit()
                editor.putInt("idUser", user.idUser)
                editor.putString("language", "es")
                editor.apply()
                navController.navigate(action)
            }
            else {
                Snackbar.make(binding.root,getString(R.string.wrongUser), Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.toRegisterBtn.setOnClickListener {
            navController.navigate(LoginFragmentDirections.actionLoginToRegister())
        }
    }
}