package com.example.monopolymanager.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monopolymanager.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private var PREF_NAME = "MONOPOLY"

class ProfileViewModel(val context: Context) : ViewModel() {
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val success: MutableLiveData<Boolean?> = MutableLiveData(null)
    val db = Firebase.firestore
    var user: User? = null

    init {
        viewModelScope.launch {
            initializeUser()
            isLoading.postValue(false)
        }
    }

    suspend fun initializeUser() {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "")
        val docRef = db.collection("User").document(username!!)
        try {
            val dataSnapshot = docRef.get().await()
            if (dataSnapshot != null) {
                user = dataSnapshot.toObject<User>()
            } else {
                Log.d("Test", "No such document")
                success.postValue(false)
            }
        } catch (e: Exception) {
            Log.d("Test", e.localizedMessage)
            success.postValue(false)
        }
    }


    fun updateAvatar(id: Int) {
        user?.setAvatar(id)
        db.collection("User").document(user?.getUsername()!!).set(user!!)
    }

    fun getAvatar(): Int {
        return user!!.getAvatar()
    }

    fun logOut() : Boolean {
        FirebaseAuth.getInstance().signOut()
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("stayLoggedIn", false)
        return editor.commit()
    }

}