package com.example.monopolymanager.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.database.userDao
import com.example.monopolymanager.entities.Property
import com.example.monopolymanager.entities.User

private var PREF_NAME = "MONOPOLY"

class SellDetailViewModel(context: Context?) : ViewModel() {
    private var db: appDatabase? = null
    private var userDao: userDao? = null
    private var propertyDao: propertyDao? = null
    var user: User? = null
    var property: Property? = null

    init {
        val sharedPref: SharedPreferences = context!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val idProperty = sharedPref.getInt("idProperty", -1)
        val idUser = sharedPref.getInt("idUser", -1)
        db = context.let { appDatabase.getAppDataBase(it) }
        userDao = db?.userDao()
        propertyDao = db?.propertyDao()
        user = userDao?.loadPersonById(idUser)
        property = propertyDao?.loadPropertyById(idProperty)
    }

    fun mortgage(): Boolean? {
        return property?.mortgage(user!!)
    }

    fun isMortgaged(): Boolean {
        return property!!.isMortgaged
    }

    fun update() {
        propertyDao?.updateProperty(property)
        userDao?.updatePerson(user)
    }
}