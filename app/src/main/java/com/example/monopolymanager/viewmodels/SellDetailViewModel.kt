package com.example.monopolymanager.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.entities.Property

private var PREF_NAME = "MONOPOLY"

class SellDetailViewModel(context: Context?) : ViewModel() {
    private var db: appDatabase? = null
    private var propertyDao: propertyDao? = null
    var property: Property? = null

    init {
        val sharedPref: SharedPreferences = context!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val idProperty = sharedPref.getInt("idProperty", -1)
        db = context.let { appDatabase.getAppDataBase(it) }
        propertyDao = db?.propertyDao()
        property = propertyDao?.loadPropertyById(idProperty)
    }
}