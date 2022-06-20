package com.example.monopolymanager.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monopolymanager.database.AppDatabase
import com.example.monopolymanager.database.propertyDao
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
    private var roomDb: AppDatabase? = null
    private var propertyDao: propertyDao? = null
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    var cashUpdate: MutableLiveData<Int?> = MutableLiveData(null)
    var user: User? = null
    var game: Game? = null
    var gameName : String? = null
    private var properties: MutableList<Property> = mutableListOf()
    private var username: String? = null

    init {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        username = sharedPref.getString("username", "")
        gameName = sharedPref.getString("game", "")
        roomDb = AppDatabase.getAppDataBase(context)
        propertyDao = roomDb?.propertyDao()
        isLoading.value = true
        viewModelScope.launch {
            initializeUser()
            initializeGame()
            initializeProperties()
            registerUpdateCash()
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
        val docRef = db.collection("Game").document(gameName!!)
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

    private fun initializeProperties() {
        val filteredProperties = game!!.properties
            .filter { it.idOwner == user!!.getUsername()}
            .sortedBy { it.group }
        for (property in filteredProperties) {
            val foundProperty = propertyDao?.loadPropertyByName(property.name)
            foundProperty?.setDetails(property)
            properties.add(foundProperty!!)
        }
    }

    fun getCash() : Int? {
        return game!!.getCashFromUsername(user!!.getUsername()!!)
    }

    fun getProperties(): MutableList<Property> {
        return properties
    }

    fun getAvatar() : Int {
        return user!!.getAvatar()
    }

    private fun registerUpdateCash() {
        val docRef = db.collection("Game").document(gameName!!)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.d("Test", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val gameUpdate = snapshot.toObject<Game>()
                cashUpdate.value = gameUpdate?.getCashFromUsername(username!!)!!
            } else {
                Log.d("Test", "Current data: null")
            }
        }
    }
}