package com.example.monopolymanager.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.groupDao
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.database.userDao
import com.example.monopolymanager.entities.Property
import com.example.monopolymanager.entities.User

private var PREF_NAME = "MONOPOLY"

class GeneralDetailViewModel(context: Context?) : ViewModel() {
    private var db: appDatabase? = null
    private var userDao: userDao? = null
    private var groupDao: groupDao? = null
    private var propertyDao: propertyDao? = null
    var user: User? = null
    var property: Property? = null
    private var idUser : Int = 0

    init {
        val sharedPref: SharedPreferences = context!!.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val idProperty = sharedPref.getInt("idProperty", -1)
        idUser = sharedPref.getInt("idUser", -1)
        db = context.let { appDatabase.getAppDataBase(it) }
        userDao = db?.userDao()
        propertyDao = db?.propertyDao()
        groupDao = db?.groupDao()
        user = userDao?.loadPersonById(idUser)
        property = propertyDao?.loadPropertyById(idProperty)
    }

    fun mortgage(): Boolean? {
        return property?.mortgage(user!!)
    }

    fun isMortgaged(): Boolean {
        return property!!.isMortgaged
    }

    fun sell() {
        val hasWholeGroup = propertyDao?.checkWholeGroup(property!!.group, idUser) == 0
        if (hasWholeGroup) {
            val ppties = propertyDao?.loadAllInGroup(property!!.group)
            val group = groupDao?.getGroupByNumber(property!!.group)
            ppties?.forEach {
                if (it.name != property!!.name) {
                    for (i in 0..it.houses) {
                        it.removeHouse()
                        user!!.charge(group?.pricePerHouse!! / 2)
                    }
                    propertyDao?.updateProperty(it)
                }
            }
        }
        property!!.idOwner = null
        user!!.charge(property!!.price)
        update()
    }

    fun update() {
        propertyDao?.updateProperty(property)
        userDao?.updatePerson(user)
    }

    fun getRentPrice() : Int {
        val hasWholeGroup = propertyDao?.checkWholeGroup(property!!.group, idUser) == 0
        if (hasWholeGroup && property!!.houses == 0)
            return property?.getRentPrice()!! * 2
        return property?.getRentPrice()!!
    }

    fun getHouses() : Int {
        return property!!.houses
    }
}