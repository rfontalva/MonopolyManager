package com.example.monopolymanager.fragments

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.monopolymanager.R
import com.example.monopolymanager.viewmodels.RegisterViewModel
import com.example.monopolymanager.entities.User
import com.example.monopolymanager.Login
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.userDao


class Register : Fragment() {

    companion object {
        fun newInstance() = Register()
    }

    private var db: appDatabase? = null
    private var userDao: userDao? = null
    private lateinit var viewModel: RegisterViewModel
    lateinit var password : EditText
    private lateinit var validatePassword : EditText
    lateinit var username : EditText
    lateinit var email : EditText
    lateinit var register : Button
    lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.register_fragment, container, false)
        username = v.findViewById(R.id.usernameRegister)
        email = v.findViewById(R.id.emailRegister)
        password = v.findViewById(R.id.passwordRegister)
        validatePassword = v.findViewById(R.id.passwordValidation)
        register = v.findViewById(R.id.registerBtn)
        return v
    }

    override fun onStart() {
        super.onStart()
        db = appDatabase.getAppDataBase(v.context)
        userDao = db?.userDao()
        register.setOnClickListener {
            if (!allFieldsValidate()) {
                Snackbar.make(v,"Todos los campos son necesarios", Snackbar.LENGTH_SHORT).show()
            }
            else if (password.text.toString() != validatePassword.text.toString()) {
                Snackbar.make(v,"Las contraseñas no coinciden", Snackbar.LENGTH_SHORT).show()
            }
            else {
                var u = User(username.text.toString(), password.text.toString(), email.text.toString())
                if (userDao?.isAvailable(u.getUsername(), u.getMail()) == 0) {
                    userDao?.insertPerson(u)
                    v.findNavController().navigate(RegisterDirections.actionRegisterToLogin())
                } else {
                    Snackbar.make(v,"Usuario o email no disponibles", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun allFieldsValidate() : Boolean {
        return !(TextUtils.isEmpty(username.text) || TextUtils.isEmpty(email.text) || TextUtils.isEmpty(password.text) || TextUtils.isEmpty(validatePassword.text))
    }
}