package com.example.monopolymanager

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class SplashActivity : AppCompatActivity() {

    private val shrink: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.shrink)}
    private val grow: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.grow)}
    private val SPLASH_TIME_OUT:Long = 1400 //1.5sec

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val img = findViewById<ImageView>(R.id.splashImg)
        val animationSet = AnimationSet(false)
        animationSet.addAnimation(grow)
        animationSet.addAnimation(shrink)
        img.startAnimation(animationSet)

        Handler().postDelayed(

            {
                startActivity(Intent(this, Login::class.java))
                finish()
            }
            , SPLASH_TIME_OUT)
    }
}