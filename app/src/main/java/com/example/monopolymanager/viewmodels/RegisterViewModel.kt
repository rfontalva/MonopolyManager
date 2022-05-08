package com.example.monopolymanager.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.database.userDao
import com.example.monopolymanager.entities.User

class RegisterViewModel(context: Context) : ViewModel() {
    private var db: appDatabase? = null
    private var userDao: userDao? = null

    init {
        db = context.let { appDatabase.getAppDataBase(it) }
        userDao = db?.userDao()
    }

    fun validate(username: String, mail: String, password: String): Boolean {
        val u = User(username, password, mail)
        if (userDao?.isAvailable(u.getUsername(), u.getMail()) == 0) {
            userDao?.insertPerson(u)
            return true
        }
        return false
    }
}