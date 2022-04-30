package com.example.monopolymanager.fragments

import android.content.Context
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
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.userDao
import com.example.monopolymanager.entities.PropertiesRepository
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = Login()
    }

    private var db: appDatabase? = null
    private var userDao: userDao? = null
    lateinit var password : EditText
    lateinit var username : EditText
    lateinit var submit : Button
    lateinit var toRegister : Button
    lateinit var v: View
    private lateinit var viewModel: LoginViewModel
    private var PREF_NAME = "MONOPOLY"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.login_fragment, container, false)
        username = v.findViewById(R.id.username)
        password = v.findViewById(R.id.password)
        submit = v.findViewById(R.id.btnSubmit)
        toRegister = v.findViewById(R.id.toRegisterBtn)
        return v
    }

    override fun onStart() {
        super.onStart()
        db = appDatabase.getAppDataBase(v.context)
        userDao = db?.userDao()
        val navController = v.findNavController()
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        var isInitialized = sharedPref.getBoolean("isInitialized", false)
        if (!isInitialized) {
            PropertiesRepository(v.context)
            val editor = sharedPref.edit()
            editor.putBoolean("isInitialized", true)
            editor.apply()
        }

        submit.setOnClickListener {
            val username = userDao?.validate(username.text.toString(), password.text.toString())
            if (username != null) {
                val u = userDao?.loadPersonByUsername(username)
                val action = LoginFragmentDirections.actionLoginToMainActivity()
                val editor = sharedPref.edit()
                editor.putInt("idUser", u!!.idUser)
                editor.apply()
                navController.navigate(action)
            }
            else {
                Snackbar.make(v,getString(R.string.wrongUser), Snackbar.LENGTH_SHORT).show()
            }
        }

        toRegister.setOnClickListener {
            navController.navigate(LoginFragmentDirections.actionLoginToRegister())
        }
    }
}