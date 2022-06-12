package com.example.monopolymanager.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.database.userDao
import com.example.monopolymanager.entities.Game
import com.example.monopolymanager.entities.Property
import com.example.monopolymanager.entities.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private var PREF_NAME = "MONOPOLY"
class HomeViewModel(context: Context) : ViewModel() {
    private var db = Firebase.firestore
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    var user: User? = null
    var game: Game? = null
    private var properties: MutableList<Property> = mutableListOf()
    private var username: String? = null

    init {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        username = sharedPref.getString("username", "")
        isLoading.value = true
        viewModelScope.launch {
            initializeUser()
            initializeGame()
            initializeProperties()
            isLoading.postValue(false)
        }
    }

    private suspend fun initializeUser() {
        val docRef = db.collection("User").document(username!!)
        try {
            val dataSnapshot = docRef.get().await()
            if (dataSnapshot != null) {
                user = dataSnapshot.toObject<User>()
            } else {
                Log.d("Test", "No such document")
            }
        } catch (e: Exception) {
            Log.d("Test", e.localizedMessage)
        }
    }

    private suspend fun initializeGame() {
        val docRef = db.collection("Game").document("mX8VgDtI3ZwMF4sye9as")
        try {
            val dataSnapshot = docRef.get().await()
            if (dataSnapshot != null) {
                game = dataSnapshot.toObject<Game>()
            } else {
                Log.d("Test", "No such document")
            }
        } catch (e: Exception) {
            Log.d("Test", e.localizedMessage)
        }
    }

    private suspend fun initializeProperties() {
        for (property in game!!.properties.filter { it.idOwner == user!!.getUsername()}) {
            val docRef = db.collection("Property").document(property.name!!)
            try {
                val dataSnapshot = docRef.get().await()
                if (dataSnapshot != null) {
                    val foundProperty = dataSnapshot.toObject<Property>()
                    foundProperty?.setDetails(property)
                    properties.add(foundProperty!!)
                } else {
                    Log.d("Test", "No such document")
                }
            } catch (e: Exception) {
                Log.d("Test", "${e.localizedMessage}")
            }
        }

    }

    fun getProperties(): MutableList<Property>? {
        return properties
    }

    fun getAvatar() : Int {
        return user!!.getAvatar()
    }
}