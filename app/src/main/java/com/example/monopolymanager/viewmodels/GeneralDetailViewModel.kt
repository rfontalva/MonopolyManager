package com.example.monopolymanager.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monopolymanager.database.groupDao
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.database.userDao
import com.example.monopolymanager.entities.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class GeneralDetailViewModel(var property: Property?) : ViewModel() {
    private var db = Firebase.firestore
    var user: User? = null
    var pricePerHouse: Int? = null
    var gameName : String = "mX8VgDtI3ZwMF4sye9as"
    var isInitialized: MutableLiveData<Boolean> = MutableLiveData(false)
    var sellSuccess: MutableLiveData<Boolean?> = MutableLiveData(false)

    init {
        viewModelScope.launch {
            initializeGroup()
        }
    }

    private suspend fun initializeGroup() {
        val docRef = db.collection("Group").document(property!!.group.toString())
        try {
            val dataSnapshot = docRef.get().await()
            if (dataSnapshot != null) {
                pricePerHouse = dataSnapshot.get("pricePerHouse") as Int
                isInitialized.postValue(true)
            } else {
                Log.d("Test", "No such document")
            }
        } catch (e: Exception) {
            Log.d("Test", e.localizedMessage)
        }
    }

    fun mortgage(): Boolean? {
        return property?.mortgage(user!!)
    }

    fun isMortgaged(): Boolean {
        return property!!.isMortgaged
    }

    suspend fun sell() {
//        SELECT count(*) from Property WHERE groupNumber = :groupNumber AND (idUser <> :idUser OR idUser is null)
        var hasWholeGroup = false
        var game: Game? = null

        val docRef = db.collection("Game").document(gameName)
        try {
            val dataSnapshot = docRef.get().await()
            if (dataSnapshot != null) {
                game = dataSnapshot.toObject<Game>()
                val pptyCount = if (property?.group == 1 || property?.group == 8) 2 else 3
                hasWholeGroup = game?.properties?.count { it.group == property!!.group && it.idOwner == property!!.idOwner } == pptyCount
            } else {
                Log.d("Test", "No such document")
            }
        } catch (e: Exception) {
            Log.d("Test", "ERROR", e)
            sellSuccess.postValue(true)
        }

        if (hasWholeGroup) {

            game?.properties?.forEach {
                if (it.name != property!!.name) {
                    user!!.charge(it.houses * pricePerHouse!! / 2)
                    it.houses = 0
                    it.hasHotel = false
                }
                else {
                    game.properties.remove(it)
                }
            }
        }
        property!!.idOwner = null
        user!!.charge(property!!.price)
        game?.players?.remove(game.players.filter{it.player == user!!.getUsername()}[0])
        game?.players?.add(UserGameDetails(user?.getUsername(), user?.getCash()))
        update(game!!)
        sellSuccess.postValue(true)
    }

    fun update(details: Game) {
        db.collection("Game").document(gameName).set(details)
    }


    fun getRentPrice() : Int {
        val hasWholeGroup = false
        if (hasWholeGroup && property!!.houses == 0)
            return property?.getRentPrice()!! * 2
        return property?.getRentPrice()!!
    }

    fun getHouses() : Int {
        return property!!.houses
    }
}