package com.example.monopolymanager.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.database.userDao
import com.example.monopolymanager.entities.Property
import com.example.monopolymanager.entities.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

private var PREF_NAME = "MONOPOLY"
class HomeViewModel(context: Context) : ViewModel() {
    private var db = Firebase.firestore
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    var user: User? = null

    init {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "")
        isLoading.value = true
        val docRef = db.collection("User").document(username!!)
        docRef.get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot != null) {
                    user = dataSnapshot.toObject<User>()
                    isLoading.value = false
                } else {
                    Log.d("Test", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Test", "get failed with ", exception)
            }
    }

    fun getProperties(): MutableList<Property>? {
        return mutableListOf()
    }

    fun getAvatar() : Int {
        return user!!.getAvatar()
    }
}