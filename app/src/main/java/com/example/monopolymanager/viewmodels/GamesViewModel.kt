package com.example.monopolymanager.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.monopolymanager.entities.Game
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

private var PREF_NAME = "MONOPOLY"


class GamesViewModel(val context: Context) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val games = mutableListOf<Game>()

    fun getGames(username: String) : MutableList<Game> {

        db.collection("Game").whereArrayContains("players", username)
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val game = document.toObject<Game>()
                    games.add(game)
                }
            }
        return games
    }

    fun setGame(position: Int) {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("game", games[position].name)
        editor.apply()
    }
}