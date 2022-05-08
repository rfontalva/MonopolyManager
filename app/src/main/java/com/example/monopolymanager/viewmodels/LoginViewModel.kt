package com.example.monopolymanager.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.userDao
import com.example.monopolymanager.entities.PropertiesRepository
import com.example.monopolymanager.entities.User

class LoginViewModel(private var context: Context?) : ViewModel() {
    private var db: appDatabase? = null
    private var userDao: userDao? = null
    init {
        db = context?.let { appDatabase.getAppDataBase(it) }
        userDao = db?.userDao()
    }

    fun validate(username: String, password: String) : User? {
        return userDao?.validate(username, password)
    }

    fun initializeProperties() {
        context?.let { PropertiesRepository(it) }
    }
}