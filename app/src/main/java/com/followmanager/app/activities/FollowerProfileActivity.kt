package com.followmanager.app.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.followmanager.app.MainActivity
import com.followmanager.app.R
import com.followmanager.app.api.API
import com.followmanager.app.api.Endpoints
import com.followmanager.app.api.ServiceBuilder
import com.followmanager.app.api.User
import com.followmanager.app.utils.AppConstants
import com.followmanager.app.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowerProfileActivity : AppCompatActivity() {

    private var sessionID: String? = null
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follower_profile)

        sessionID = AppConstants.getPreferenceSessionID(applicationContext)

        // If sessionID is not defined go back
        if (sessionID == null || sessionID == "") {
            startActivity(
                Intent(
                    applicationContext,
                    MainActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            return
        }

        val json = intent.getStringExtra("info")
        if (json == null) {
            finish()
            return
        }

        user = Utils.jsonToUser(json!!)

        loadInfo()
    }

    fun refresh(v: View?) {
        Toast.makeText(
            applicationContext,
            applicationContext.getText(R.string.server_timeout_refresh),
            Toast.LENGTH_SHORT
        )
        findViewById<TextView>(R.id.user_followers).text = "..."
        findViewById<TextView>(R.id.user_following).text = "..."
        loadInfo()
    }

    fun loadInfo() {
        if (sessionID == null) return

        findViewById<TextView>(R.id.followers_list_title).text =
            getString(R.string.profile_of) + " ${user.username}"

        val profile_pic = findViewById<AppCompatImageView>(R.id.follower_profile_image)
        val button = findViewById<Button>(R.id.follower_profile_button)

        // Load profile pic
        Glide.with(this).load(user.profile_pic_url).into(profile_pic)

        // Load button
        if (user.is_following) {
            button.setBackgroundColor(resources.getColor(R.color.red))
            button.text = getString(R.string.unfollow)
            button.setOnClickListener {
                API.unfollowUser(applicationContext, user, button)
            }
        } else {
            button.setBackgroundColor(resources.getColor(R.color.green))
            button.text = getString(R.string.follow)
            button.setOnClickListener {
                API.followUser(applicationContext, user, button)
            }
        }

        // Load user info
        val request = ServiceBuilder.buildService(Endpoints::class.java)
        val call = request.getUserInfo(sessionID!!, user.pk)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                Log.d("FM", response.body().toString())
                if (response.isSuccessful) {
                    val info = response.body()!!

                    findViewById<TextView>(R.id.user_followers).text =
                        info.follower_count.toString()
                    findViewById<TextView>(R.id.user_following).text =
                        info.following_count.toString()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("FM", t.toString())
                Toast.makeText(
                    applicationContext,
                    applicationContext.getText(R.string.server_timeout_refresh),
                    Toast.LENGTH_SHORT
                )
            }
        })
    }


    fun goBack(v: View?) {
        finish()
    }
}