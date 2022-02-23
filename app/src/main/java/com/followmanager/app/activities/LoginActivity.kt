package com.followmanager.app.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.followmanager.app.R
import com.followmanager.app.api.Endpoints
import com.followmanager.app.api.ServiceBuilder
import com.followmanager.app.utils.AppConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    private var isTryingToLogin: Boolean = false
    private lateinit var progressLoader: ProgressBar
    private lateinit var loginButtonText: TextView
    private lateinit var loginButton: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progressLoader = findViewById(R.id.progress_loader)
        loginButtonText = findViewById(R.id.button_login_text)
        loginButton = findViewById(R.id.login_with_)
    }

    fun goBack(v: View?) {
        //startActivity(Intent(applicationContext, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

    fun changeProgressState(start: Boolean) {
        if (start) {
            progressLoader.visibility = View.VISIBLE
            loginButtonText.visibility = View.GONE
            loginButton.isClickable = false
        } else {
            progressLoader.visibility = View.GONE
            loginButtonText.visibility = View.VISIBLE
            loginButton.isClickable = true
        }
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    fun login(v: View?) {

        loginButton.hideKeyboard()
        changeProgressState(true)

        if (!isTryingToLogin) {
            isTryingToLogin = true
            val username = findViewById<EditText>(R.id.login_username).text.toString()
            val password = findViewById<EditText>(R.id.login_password).text.toString()

            val request = ServiceBuilder.buildService(Endpoints::class.java)
            val call = request.login(username, password)

            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    isTryingToLogin = false
                    if (response.isSuccessful) {
                        val sessionID: String = response.body()!!
                        AppConstants.setPreferenceSessionID(sessionID, applicationContext)
                        changeProgressState(false)
                        startActivity(Intent(applicationContext, Dashboard::class.java))
                    } else {
                        changeProgressState(false)
                        Toast.makeText(
                            applicationContext,
                            applicationContext.getText(R.string.wrong_info),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {

                    isTryingToLogin = false
                    changeProgressState(false)

                    Toast.makeText(
                        applicationContext,
                        applicationContext.getText(R.string.server_timeout),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }


    }

}