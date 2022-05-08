package com.example.monopolymanager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.monopolymanager.utils.switchLocal
import kotlinx.coroutines.delay


class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT:Long = 1500 // 3 sec

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed(

            {
                startActivity(Intent(this, Login::class.java))
                finish()
            }
            , SPLASH_TIME_OUT)
    }
}