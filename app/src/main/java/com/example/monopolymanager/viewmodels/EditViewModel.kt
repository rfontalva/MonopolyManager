package com.example.monopolymanager.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.monopolymanager.R
import com.example.monopolymanager.entities.Game
import com.example.monopolymanager.entities.Property
import com.example.monopolymanager.entities.User
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private var PREF_NAME = "MONOPOLY"

class EditViewModel(var context: Context, override var property: Property?) : AddEditViewModel(context) {
    override var user: User? = null
    private var hasWholeGroup: Boolean = false
    private var groupHousePrice : Int = 0
    private val originalHouseAmt = property!!.houses
    override val addHousesMsg = R.string.allPropertiesError
    override val removeHousesMsg = R.string.allPropertiesError
    override var game: Game? = null

    private var username: String? = null

    init {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        username = sharedPref.getString("username", "")
        hasWholeGroup = false
        groupNumber = property?.group!!
        groupHousePrice = groupDao?.getPricePerHouseByNumber(groupNumber)!!

        viewModelScope.launch {
            initializeProperties()
            isLoading.postValue(false)
        }
    }
    suspend fun initializeProperties() {
        val docRef = db.collection("Game").document("Game1")
        try {
            val dataSnapshot = docRef.get().await()
            if (dataSnapshot != null) {
                game = dataSnapshot.toObject<Game>()
                occupiedProperties += game!!.properties!!.filter { it.group == groupNumber }
                availableProperties = propertyDao?.loadAvailablePropertiesByName(
                    occupiedProperties.map {it.name!!}
                )!!
                val filtered = availableProperties.filter { it.group == groupNumber } as MutableList<Property>
                user = User(username, "")
                user!!.setCash(game!!.getCashFromUsername(username!!)!!)
                hasWholeGroup = filtered.size == 0
            } else {
                Log.d("Test", "No such document")
            }
        } catch (e: Exception) {
            Log.d("Test", e.localizedMessage)
        }
    }

    override fun setProperty(position: Int) {
        //
    }

    override fun getRentPrice() : Int? {
        return property!!.getRentPrice()
    }

    override fun getPrice() : Int {
        var totalPrice = groupHousePrice * (property!!.houses - originalHouseAmt)
        if (totalPrice < 0)
            totalPrice /= 2
        return totalPrice
    }

    override fun addHouse() : Boolean {
        if (hasWholeGroup) {
            return property!!.addHouse()
        }
        return false
    }

    override fun removeHouse() : Boolean {
        if (hasWholeGroup) {
            return property!!.removeHouse()
        }
        return false
    }

    override fun getColorArray() : MutableList<String?> {
        val color = groupDao?.getColorName(property?.group)
        val colorNames : MutableList<String?> = mutableListOf()
        mutableListOf(color!!).forEach {
            colorNames.add(context.getString(context.resources.getIdentifier("com.example.monopolymanager:string/${it}", null, null)))
        }
        return colorNames
    }

    override fun getNameArray(groupNumber: Int) : MutableList<String> {
        val propertyNamesArray : MutableList<String> = mutableListOf()
        mutableListOf(property!!.name).forEach {
            propertyNamesArray.add(context.getString(context.resources.getIdentifier("com.example.monopolymanager:string/${it}", null, null)))
        }
        return propertyNamesArray
    }

    override fun canBuy() : Pair<Int, Boolean> {
        val totalPrice = getPrice()
        val (cash, canPay) = if (totalPrice < 0)
            user!!.charge(-1 * totalPrice)
        else
            user!!.pay(totalPrice)

        if (canPay) {
            property!!.idOwner = username
            updateGame()
        }
        return Pair(cash, canPay)
    }

    override fun getHousesAmt() : Int {
        return property!!.houses
    }

    override fun hasHotel() : Boolean {
        return property!!.hasHotel
    }
}