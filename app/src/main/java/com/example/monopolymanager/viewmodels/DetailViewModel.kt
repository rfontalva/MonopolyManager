package com.example.monopolymanager.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.groupDao
import com.example.monopolymanager.entities.Property

private var PREF_NAME = "MONOPOLY"

class DetailViewModel(context: Context, var property: Property?) : ViewModel() {

    init {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("idProperty", property!!.idProperty)
        editor.apply()
    }
}