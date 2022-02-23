package com.followmanager.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.followmanager.app.activities.Dashboard
import com.followmanager.app.activities.LoginActivity
import com.followmanager.app.utils.AppConstants
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var locale: Locale
    private var currentLanguage: String? = "en"
    private var currentLang: String = "CURRENT_LANG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        loadLanguage()
        checkIfUserIsAlreadyLoggedIn()
    }

    fun loadLanguage() {
        currentLanguage = intent.getStringExtra(currentLang)
        val preferenceLanguage = AppConstants.getPreferenceLanguage(applicationContext)

        if (currentLanguage == null && preferenceLanguage != null) {
            setLocale(preferenceLanguage)
        }
    }

    fun checkIfUserIsAlreadyLoggedIn() {
        var sessionID = AppConstants.getPreferenceSessionID(applicationContext)
        if (sessionID != null && sessionID != "") {
            startActivity(Intent(applicationContext, Dashboard::class.java))
        }
    }

    fun loginWithInstagram(v: View?) {
        startActivity(Intent(applicationContext, LoginActivity::class.java))
    }

    fun changeToFrench(v: View?) {
        AppConstants.setPreferenceLanguage("fr", applicationContext)
        setLocale("fr")
    }

    fun changeToEnglish(v: View?) {
        AppConstants.setPreferenceLanguage("en", applicationContext)
        setLocale("en")
    }

    fun changeToDeutsch(v: View?) {
        AppConstants.setPreferenceLanguage("de", applicationContext)
        setLocale("de")
    }

    fun setLocale(localeName: String) {
        if (localeName != currentLanguage) {
            locale = Locale(localeName)
            val res = resources
            val dm = res.displayMetrics
            val conf = res.configuration
            conf.locale = locale
            res.updateConfiguration(conf, dm)
            val refresh = Intent(
                this,
                MainActivity::class.java
            )
            refresh.putExtra(currentLang, localeName)
            startActivity(refresh)
            finish()
        }
    }
}