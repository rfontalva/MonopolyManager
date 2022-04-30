package com.example.monopolymanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.monopolymanager.entities.User

class Login : AppCompatActivity() {

    lateinit var activity : LoginUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        activity = LoginUtils()
        setContentView(R.layout.activity_login)

//        fun getUtils
    }

    class LoginUtils private constructor() {
        private var users: MutableList<User> = mutableListOf()
        init {
            users.add(User("rami","rami","rami"))
            users.add(User("pepe","pepe","pepe"))
            users.add(User("nahue","nahue","nahue"))
        }
        companion object {
            private val instance = LoginUtils()
            fun getInstance(): LoginUtils {
                return instance
            }

            @JvmName("getUsers1")
            fun getUsers(): MutableList<User> {
                return instance.users
            }

            fun addUser(u: User) {
                instance.users.add(u)
            }

            fun tryAdd(u: User) : Boolean {
                instance.users.forEach {
                    if (u.getUsername() == it.getUsername() || u.getMail() == it.getMail())
                        return false
                }
                instance.users.add(u)
                return true
            }
        }
    }

}