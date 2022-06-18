package com.example.monopolymanager.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monopolymanager.R
import com.example.monopolymanager.database.AppDatabase
import com.example.monopolymanager.database.groupDao
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.entities.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private var PREF_NAME = "MONOPOLY"

abstract class AddEditViewModel(context: Context) : ViewModel() {
    var db = Firebase.firestore
    var roomDb: AppDatabase? = null
    var propertyDao: propertyDao? = null
    var groupDao: groupDao? = null
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    protected var occupiedProperties: MutableList<PropertyGameDetails> = mutableListOf()
    protected var availableProperties: MutableList<Property> = mutableListOf()
    var groupNumber = 0

    abstract var game: Game?
    abstract var property: Property?
    abstract var user: User?
    abstract val addHousesMsg : Int
    abstract val removeHousesMsg : Int

    init {
        roomDb = AppDatabase.getAppDataBase(context)
        propertyDao = roomDb?.propertyDao()
        groupDao = roomDb?.groupDao()
    }


    abstract fun getRentPrice() : Int?
    abstract fun getPrice() : Int
    abstract fun addHouse() : Boolean
    abstract fun removeHouse() : Boolean
    abstract fun getColorArray() : MutableList<String?>
    abstract fun canBuy() : Pair<Int, Boolean>
    abstract fun getHousesAmt() : Int
    abstract fun hasHotel() : Boolean

    abstract fun getNameArray(groupNumber: Int): MutableList<String>
    abstract fun setProperty(position: Int)

    fun updateGame() {
        if (game == null)
            return
        game!!.update(property!!, user!!)
        db.collection("Game").document(game!!.name).set(game!!)
    }
}
