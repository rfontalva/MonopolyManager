package com.example.monopolymanager.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monopolymanager.entities.Game
import com.example.monopolymanager.entities.PropertyGameDetails
import com.example.monopolymanager.entities.UserGameDetails
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private var PREF_NAME = "MONOPOLY"

class GamesViewModel(val context: Context) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val games = mutableListOf<Game>()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    var hasJoined: MutableLiveData<Boolean?> = MutableLiveData(null)
    var hasCreated: MutableLiveData<Boolean?> = MutableLiveData(null)
    private var username: String? = null

    init {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        username = sharedPref.getString("username", "")
        isLoading.value = true
        viewModelScope.launch {
            initializeGames()
            isLoading.postValue(false)
        }
    }

    private suspend fun initializeGames() {
        val docRef = db.collection("Game")
        try {
            val documents = docRef.get().await()
            for (document in documents) {
                val game = document.toObject<Game>()
                if (game.players.count {it.player == username} == 1)
                    games.add(game)
            }
        } catch (e: Exception) {
            Log.d("Test", e.localizedMessage)
        }
    }

    fun getGames() : MutableList<Game> {
        return games
    }

    fun setGame(position: Int) {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("game", games[position].name)
        editor.apply()
    }

    suspend fun joinGame(name: String, password: String) {
        val docRef = db.collection("Game").document(name)
        try {
            val document = docRef.get().await()
            val game = document.toObject<Game>()
            if (game?.password == password && game.players.count {it.player == username} == 0) {
                game.players.add(UserGameDetails(username, 1500))
                docRef.set(game)
                val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putString("game", name)
                editor.apply()
                hasJoined.postValue(true)
            } else {
              hasJoined.postValue(false)
            }
        } catch (e: Exception) {
            Log.d("Test", "error: ", e)
            hasJoined.postValue(false)
        }
    }

    suspend fun createGame(name: String, password: String) {
        val docRef = db.collection("Game").document(name)
        try {
            val document = docRef.get().await()
            if (document.exists())
                hasCreated.postValue(false)
            else {
                val userDetails = mutableListOf<UserGameDetails>(UserGameDetails(username, 1500))
                val newGame = Game(name, userDetails, mutableListOf(), password)
                docRef.set(newGame)
                val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putString("game", name)
                editor.apply()
                hasCreated.postValue(true)
            }
        } catch (e: Exception) {
            Log.d("Test", "error: ", e)
            hasCreated.postValue(false)

        }
    }
}