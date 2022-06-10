package com.example.monopolymanager.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.monopolymanager.R
import com.example.monopolymanager.databinding.RegisterFragmentBinding
import com.example.monopolymanager.viewmodels.RegisterViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*



class Register : Fragment() {

    companion object {
        fun newInstance() = Register()
    }

    private lateinit var binding: RegisterFragmentBinding
    private lateinit var viewModel: RegisterViewModel
    private var isLoading = false
    private var firstObserved = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RegisterFragmentBinding.inflate(layoutInflater)
        viewModel = RegisterViewModel(requireContext())
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { result ->
            isLoading = result
        })

        viewModel.success.observe(viewLifecycleOwner, Observer { result ->
//            if (firstObserved)
//                firstObserved = !firstObserved
            if(result == true)
                binding.root.findNavController().navigate(RegisterDirections.actionRegisterToLogin())
            else if (result == false)
                Snackbar.make(binding.root,getString(R.string.userOrEmailNotAvailable), Snackbar.LENGTH_SHORT).show()
        })

        binding.registerBtn.setOnClickListener {
            val pwd = binding.passwordRegister.text.toString()
            val pwdValidation = binding.passwordValidation.text.toString()
            val username = binding.usernameRegister.text.toString()
            val email = binding.emailRegister.text.toString()

            if (!allFieldsValidate()) {
                Snackbar.make(binding.root,getString(R.string.necessaryFields), Snackbar.LENGTH_SHORT).show()
            }
            else if (pwd != pwdValidation) {
                Snackbar.make(binding.root,getString(R.string.passwordsDontMatch), Snackbar.LENGTH_SHORT).show()
            }
            else if (pwd.length < 6) {
                Snackbar.make(binding.root, getString(R.string.passwordLength), Snackbar.LENGTH_SHORT).show()
            }
            else {

                scope.launch {
                    val isCreated = viewModel.createUser(email, pwd)
                    while(isLoading) {}
                    viewModel.register(isCreated, username, email)

                }

            }
        }
    }

    private fun allFieldsValidate() : Boolean {
        return !(TextUtils.isEmpty(binding.usernameRegister.text) || TextUtils.isEmpty(binding.passwordRegister.text) || TextUtils.isEmpty(binding.emailRegister.text) || TextUtils.isEmpty(binding.passwordValidation.text))
    }
}
