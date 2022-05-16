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
class HomeViewModel(context: Context) : ViewModel() {
    private var db: appDatabase? = null
    private var userDao: userDao? = null
    private var propertyDao: propertyDao? = null
    var user: User? = null

    init {
        db = context.let { appDatabase.getAppDataBase(it) }
        userDao = db?.userDao()
        propertyDao = db?.propertyDao()
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val idUser = sharedPref.getInt("idUser", -1)
        user = userDao?.loadPersonById(idUser)
    }

    fun getProperties(): MutableList<Property>? {
        return propertyDao?.loadPropertiesByUserId(user?.idUser)
    }

    fun getAvatar() : Int {
        return user!!.getAvatar()
    }
}