package com.example.monopolymanager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.monopolymanager.entities.User
import com.example.monopolymanager.utils.switchLocal


class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

}