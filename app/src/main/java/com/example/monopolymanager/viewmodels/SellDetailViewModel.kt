package com.example.monopolymanager.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.monopolymanager.database.AppDatabase
import com.example.monopolymanager.database.groupDao
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.entities.Property


class SellDetailViewModel(context: Context, var property: Property?) : ViewModel() {
    private var roomDb: AppDatabase? = null
    private var groupDao: groupDao? = null
    private var housePrice = 0
    private var color = ""

    init {
        roomDb = AppDatabase.getAppDataBase(context)
        groupDao = roomDb?.groupDao()
        val group = groupDao?.getGroupByNumber(property!!.group)
        housePrice = group?.pricePerHouse!!
        color = group.color!!
    }

    fun getColor() : String {
        return color
    }

    fun getHousePrice() : Int {
        return housePrice
    }
}