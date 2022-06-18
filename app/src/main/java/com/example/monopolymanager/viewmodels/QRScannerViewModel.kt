package com.example.monopolymanager.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monopolymanager.entities.Game
import com.example.monopolymanager.entities.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private var PREF_NAME = "MONOPOLY"

class QRScannerViewModel(val context: Context) : ViewModel() {
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    val db = Firebase.firestore
    var user: User? = null
    var game: Game? = null
    var username : String?
    private var hasFoundValid: Boolean = false

    init {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        username = sharedPref.getString("username", "")
        viewModelScope.launch {
            initializeUser()
            initializeGame()
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
            Log.d("Test", "error:", e)
        }
    }

    private suspend fun initializeGame() {
        val docRef = db.collection("Game").document("Game1")
        try {
            val dataSnapshot = docRef.get().await()
            if (dataSnapshot != null) {
                game = dataSnapshot.toObject<Game>()
                user?.setCash(game?.getCashFromUsername(user!!.getUsername()!!)!!)
            } else {
                Log.d("Test", "No such document")
            }
        } catch (e: Exception) {
            Log.d("Test", "error:", e)
        }
    }

    fun isValidGame(gameName: String, userDest: String) : Boolean {
        return gameName == game!!.name &&
                game!!.players.count { it.player == userDest } == 1 &&
                userDest != username
    }

    fun pay(amount: Int, userDest: String) : Boolean {
        if (user?.pay(amount)!!.second) {
            game?.updateBalance(user!!.getUsername()!!, userDest, amount)
            db.collection("Game").document(game!!.name).set(game!!)
        }
        return user?.pay(amount)!!.second
    }

    fun hasFoundValid() : Boolean {
        return hasFoundValid
    }

    fun setHasFoundValid() {
        hasFoundValid = true
    }
}