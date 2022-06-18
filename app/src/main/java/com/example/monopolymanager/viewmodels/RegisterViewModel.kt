package com.example.monopolymanager.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.monopolymanager.entities.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class RegisterViewModel(context: Context) : ViewModel() {
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    var success: MutableLiveData<Boolean?> = MutableLiveData(null)

    suspend fun createUser(email: String, password: String): AuthResult? {
        isLoading.postValue(true)
        try {
            val res = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .await()
            isLoading.postValue(false)
            return res
        } catch (e: Exception) {
            Log.d("debug", e.localizedMessage)
            isLoading.postValue(false)
            return null
        }
    }

    fun register(isCreated: AuthResult?, username: String, email: String) {
        if (isCreated != null) {
            try {
                generateUser(username, email)
                success.postValue(true)
                return
            } catch (e: Exception) {
                Log.d("debug", e.localizedMessage)
            }
        }
        success.postValue(false)
    }

//    }

    private fun generateUser(username: String?, email: String?) {
        val database = FirebaseFirestore.getInstance()
        val users = database.collection("User") //users is a node in your Firebase Database.
        val user = User(username, email) //ObjectClass for Users
        users.document(user.getUsername()!!).set(user)
    }
}