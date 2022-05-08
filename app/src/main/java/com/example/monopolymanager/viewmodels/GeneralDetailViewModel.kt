package com.example.monopolymanager.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.entities.Property

class GeneralDetailViewModel(context: Context?, idProperty: Int) : ViewModel() {
    private var db: appDatabase? = null
    private var propertyDao: propertyDao? = null
    var property: Property? = null

    init {
        db = context?.let { appDatabase.getAppDataBase(it) }
        propertyDao = db?.propertyDao()
        property = propertyDao?.loadPropertyById(idProperty)
    }
}