package com.example.monopolymanager.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.monopolymanager.R
import com.example.monopolymanager.entities.Game
import com.example.monopolymanager.entities.Group
import com.example.monopolymanager.entities.Property
import com.example.monopolymanager.entities.User
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private var PREF_NAME = "MONOPOLY"

class AddViewModel(var context: Context) : AddEditViewModel(context) {
    override var user: User? = null
    override var property: Property? = null
    override var game: Game? = null
    override val addHousesMsg = R.string.housesError
    override val removeHousesMsg = R.string.housesError
    var username: String? = ""
    var colors: MutableList<String> = mutableListOf()
    var groups: MutableList<Group> = mutableListOf()


    init {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        username = sharedPref.getString("username", "")
        viewModelScope.launch {
            initializeProperties()
            initializeGroups()
            isLoading.postValue(false)
        }
    }

    suspend fun initializeProperties() {
        val docRef = db.collection("Game").document("Game1")
        try {
            val dataSnapshot = docRef.get().await()
            if (dataSnapshot != null) {
                game = dataSnapshot.toObject<Game>()
                user = User(username, "")
                user!!.setCash(game?.players!!.firstOrNull {it.player == username}!!.cash!!)
                occupiedProperties += game!!.properties!!
                availableProperties = propertyDao?.loadAvailablePropertiesByName(
                    occupiedProperties.map {it.name!!}
                )!!
            } else {
                Log.d("Test", "No such document")
            }
        } catch (e: Exception) {
            Log.d("Test", e.localizedMessage)
        }
    }

    fun initializeGroups() {
        groups = groupDao?.getGroupsByNumbers(availableProperties.map {it.group})!!
        colors = groups!!.map {it.color} as MutableList<String>
    }

    override fun setProperty(position: Int) {
        val propertiesInGroup = availableProperties.filter { it.group == groupNumber}
        property = propertiesInGroup[position]
    }

    override fun getRentPrice() : Int? {
        return property?.getRentPrice()!!
    }

    override fun getPrice() : Int {
        return property!!.price
    }

    override fun addHouse() : Boolean {
        return true
    }

    override fun removeHouse() : Boolean {
        return true
    }

    override fun getColorArray(): MutableList<String?> {
        return groups.map {
            context.getString(context.resources.getIdentifier("com.example.monopolymanager:string/${it.colorName}", null, null))
        }.toMutableList()
    }

    override fun getNameArray(position: Int) : MutableList<String> {
        groupNumber = groups.firstOrNull { it.color == colors[position] }?.group!!
        val x = availableProperties.filter { it.group == groupNumber}.map {
            context.getString(context.resources.getIdentifier("com.example.monopolymanager:string/${it.name}", null, null))
        }.toMutableList()
        return x
    }

    override fun canBuy() : Pair<Int, Boolean> {
        val (cash, canPay) = user!!.pay(property!!.price)

        if (canPay) {
            property!!.idOwner = username
            updateGame()
        }
        return Pair(cash, canPay)
    }

    override fun getHousesAmt() : Int {
        return 0
    }

    override fun hasHotel() : Boolean {
        return false
    }
}