package com.followmanager.app.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.followmanager.app.MainActivity
import com.followmanager.app.R

class SplashScreenActivity : AppCompatActivity() {

    private val splashTime = 3000L
    private lateinit var myHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        myHandler = Handler()
        myHandler.postDelayed({
            goToMainActivity()
        }, splashTime)
    }

    private fun goToMainActivity() {
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }
}