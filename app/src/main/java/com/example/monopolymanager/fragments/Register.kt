package com.example.monopolymanager.fragments

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.monopolymanager.viewmodels.RegisterViewModel
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import com.example.monopolymanager.databinding.RegisterFragmentBinding


class Register : Fragment() {

    companion object {
        fun newInstance() = Register()
    }

    private lateinit var binding: RegisterFragmentBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RegisterFragmentBinding.inflate(layoutInflater)
        viewModel = RegisterViewModel(requireContext())
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.registerBtn.setOnClickListener {
            if (!allFieldsValidate()) {
                Snackbar.make(binding.root,"Todos los campos son necesarios", Snackbar.LENGTH_SHORT).show()
            }
            else if (binding.passwordRegister.text.toString() != binding.passwordValidation.text.toString()) {
                Snackbar.make(binding.root,"Las contraseñas no coinciden", Snackbar.LENGTH_SHORT).show()
            }
            else {
                if(viewModel.validate(binding.usernameRegister.text.toString(), binding.emailRegister.text.toString(), binding.passwordRegister.text.toString()))
                    binding.root.findNavController().navigate(RegisterDirections.actionRegisterToLogin())
                else
                    Snackbar.make(binding.root,"Usuario o email no disponibles", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun allFieldsValidate() : Boolean {
        return !(TextUtils.isEmpty(binding.usernameRegister.text) || TextUtils.isEmpty(binding.passwordRegister.text) || TextUtils.isEmpty(binding.emailRegister.text) || TextUtils.isEmpty(binding.passwordValidation.text))
    }
}