package com.example.monopolymanager.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.groupDao
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.entities.Property

private var PREF_NAME = "MONOPOLY"

class SellDetailViewModel(context: Context?) : ViewModel() {
    private var db: appDatabase? = null
    private var propertyDao: propertyDao? = null
    private var groupDao: groupDao? = null
    var property: Property? = null
    var housePrice = 0
    var color = ""

    init {
        val sharedPref: SharedPreferences = context!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val idProperty = sharedPref.getInt("idProperty", -1)
        db = context.let { appDatabase.getAppDataBase(it) }
        propertyDao = db?.propertyDao()
        groupDao = db?.groupDao()
        property = propertyDao?.loadPropertyById(idProperty)
        val group = groupDao?.getGroupByNumber(property!!.group)
        housePrice = group?.pricePerHouse!!
        color = group.color!!
    }
}