package com.example.monopolymanager.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.groupDao
import com.example.monopolymanager.entities.Property

private var PREF_NAME = "MONOPOLY"

class DetailViewModel(context: Context, var property: Property?) : ViewModel() {
    private var db: appDatabase? = null
    private var groupDao: groupDao? = null
    var color : String? = null
    var colorName : String? = null

    init {
        db = context.let { appDatabase.getAppDataBase(it) }
        groupDao = db?.groupDao()
        color = groupDao?.getColor(property!!.group)
        colorName = groupDao?.getColorName(property!!.group)
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("idProperty", property!!.idProperty)
        editor.apply()
    }
}