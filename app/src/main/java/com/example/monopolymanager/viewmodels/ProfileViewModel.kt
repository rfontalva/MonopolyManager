package com.example.monopolymanager.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.monopolymanager.database.userDao
import com.example.monopolymanager.entities.User

private var PREF_NAME = "MONOPOLY"

class ProfileViewModel(context: Context) : ViewModel() {
    private var db: Any? = null
    private var userDao: userDao? = null
    var user: User? = null
    init {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val idUser = sharedPref.getInt("idUser", -1)

        db = context.let { null }
        userDao = null
        user = userDao?.loadPersonById(idUser)
    }

    fun getEmail(): String {
        return "no mas mail"
    }

    fun updateEmail(newEmail: String) : Boolean{
        var existingEmails = userDao?.existingEmails(newEmail)
        if (existingEmails == 0) {

            userDao?.updatePerson(user)
        }
        return existingEmails == 0
    }

    fun updateAvatar(id: Int) {
        user?.setAvatar(id)
        userDao?.updatePerson(user)
    }

    fun getAvatar(): Int {
        return user!!.getAvatar()
    }
}