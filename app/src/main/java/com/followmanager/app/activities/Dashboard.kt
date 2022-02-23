package com.followmanager.app.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.followmanager.app.MainActivity
import com.followmanager.app.R
import com.followmanager.app.api.Endpoints
import com.followmanager.app.api.ServiceBuilder
import com.followmanager.app.api.User
import com.followmanager.app.utils.AppConstants
import com.followmanager.app.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Dashboard : AppCompatActivity() {

    private var sessionID: String? = null
    private lateinit var userID: String

    private var isLoaded: Boolean = false

    private lateinit var userInfo: User
    private lateinit var followers: Map<String, User>
    private lateinit var following: Map<String, User>

    private var gainedFollowers: MutableMap<String, User> = mutableMapOf<String, User>()
    private var lostFollowers: MutableMap<String, User> = mutableMapOf<String, User>()

    private var mutualFollowers: MutableMap<String, User> = mutableMapOf<String, User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

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

        userID = sessionID!!.substring(0, sessionID!!.indexOf("%"))
        loadInfo()
    }

    /* LOAD INFO FUNCTIONS */
    fun refresh(v: View?) {
        Log.d("FM", "Refreshing..")
        Toast.makeText(
            applicationContext,
            applicationContext.getText(R.string.refreshing),
            Toast.LENGTH_SHORT
        )
        findViewById<TextView>(R.id.user_followers).text = "..."
        findViewById<TextView>(R.id.user_following).text = "..."
        findViewById<TextView>(R.id.gained_followers_number).text = "..."
        findViewById<TextView>(R.id.lost_followers_number).text = "..."
        findViewById<TextView>(R.id.followers_dont_follow_back_number).text = "..."
        findViewById<TextView>(R.id.not_following_back_number).text = "..."

        loadInfo()
    }

    private fun loadInfo() {
        if (sessionID == null) return

        val request = ServiceBuilder.buildService(Endpoints::class.java)
        val call = request.getUserInfo(sessionID!!, userID)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                Log.d("FM", response.body().toString())
                if (response.isSuccessful) {
                    loadFollowers()
                    userInfo = response.body()!!
                    updateInfo()
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

    private fun loadFollowers() {
        if (sessionID == null) return

        val request = ServiceBuilder.buildService(Endpoints::class.java)

        val callFollowers = request.getUserFollowers(sessionID!!, userID)
        callFollowers.enqueue(object : Callback<Map<String, User>> {
            override fun onResponse(
                call: Call<Map<String, User>>,
                response: Response<Map<String, User>>
            ) {
                Log.d("FM", "Loading followers")
                if (response.isSuccessful) {
                    followers = response.body()!!
                    calculateFollowers()
                }
            }

            override fun onFailure(call: Call<Map<String, User>>, t: Throwable) {
                Log.e("FM", t.toString())
                Toast.makeText(
                    applicationContext,
                    applicationContext.getText(R.string.server_timeout_refresh),
                    Toast.LENGTH_SHORT
                )
            }

        })

        val callFollowing = request.getUserFollowing(sessionID!!, userID)
        callFollowing.enqueue(object : Callback<Map<String, User>> {
            override fun onResponse(
                call: Call<Map<String, User>>,
                response: Response<Map<String, User>>
            ) {
                Log.d("FM", "Loading following")
                if (response.isSuccessful) {
                    following = response.body()!!
                    calculateFollowers()
                }
            }

            override fun onFailure(call: Call<Map<String, User>>, t: Throwable) {
                Log.e("FM", t.toString())
                Toast.makeText(
                    applicationContext,
                    applicationContext.getText(R.string.server_timeout_refresh),
                    Toast.LENGTH_SHORT
                )
            }
        })

    }

    fun updateInfo() {
        val profilePic = findViewById<AppCompatImageView>(R.id.user_profile_image)
        val followers = findViewById<TextView>(R.id.user_followers)
        val following = findViewById<TextView>(R.id.user_following)

        Glide.with(this).load(userInfo.profile_pic_url).into(profilePic)

        followers.text = Utils.formatNumber(userInfo.follower_count)
        following.text = Utils.formatNumber(userInfo.following_count)
    }

    fun calculateFollowers() {
        // If any of the arrays hasn't been initialized then return
        if (!this::followers.isInitialized || !this::following.isInitialized) return;

        /*
        *  Gained / Lost followers
        */
        val oldFollowers = AppConstants.getPreferenceFollowersList(applicationContext)
        if (oldFollowers != null) {

            for (follower in followers) {
                var found = false;
                for (oldFollower in oldFollowers) {
                    if (follower.key == oldFollower.key) found = true
                }
                if (!found) gainedFollowers[follower.key] = follower.value
            }

            for (oldFollower in oldFollowers) {
                var found = false;
                for (follower in followers) {
                    if (oldFollower.key == follower.key) found = true
                }
                if (!found) lostFollowers[oldFollower.key] = oldFollower.value;
            }

            findViewById<TextView>(R.id.gained_followers_number).setText(gainedFollowers.size.toString())
            findViewById<TextView>(R.id.lost_followers_number).setText(lostFollowers.size.toString())
        } else {
            AppConstants.setPreferenceFollowersList(followers, applicationContext)
            findViewById<TextView>(R.id.gained_followers_number).setText(followers.size.toString())
        }


        /*
        *  Followers you don't follow back
        *  Follower doesn't follow back
        */
        var mutualFollowCount = 0;
        for (follower in followers) {
            for (following in following) {
                if (follower.key == following.key) {
                    mutualFollowCount++
                    mutualFollowers[follower.key] = follower.value
                }
            }
        }

        val userDontFollowBack = followers.size - mutualFollowCount
        val followerDontFollowBack = following.size - mutualFollowCount

        findViewById<TextView>(R.id.followers_dont_follow_back_number).setText(userDontFollowBack.toString())
        findViewById<TextView>(R.id.not_following_back_number).setText(followerDontFollowBack.toString())

        isLoaded = true
    }
    /* END LOAD INFO FUNCTIONS */

    fun gotoUserDontFollowBack(v: View?) {
        startActivity(
            Intent(applicationContext, FollowersListActivity::class.java)
                .putExtra("title", resources.getString(R.string.list_of_user_doesnt_follow_back))
                .putExtra("type", 1)
        )
    }

    fun gotoFollowerDontFollowBack(v: View?) {
        startActivity(
            Intent(applicationContext, FollowersListActivity::class.java)
                .putExtra(
                    "title",
                    resources.getString(R.string.list_of_follower_doesnt_follow_back)
                )
                .putExtra("type", 2)
        )

    }

    fun gotoGainedFollowers(v: View?) {
        startActivity(
            Intent(applicationContext, FollowersListActivity::class.java)
                .putExtra("title", resources.getString(R.string.list_of_gained_followers))
                .putExtra("type", 3)
        )

    }

    fun gotoLostFollowers(v: View?) {
        startActivity(
            Intent(applicationContext, FollowersListActivity::class.java)
                .putExtra("title", resources.getString(R.string.list_of_lost_followers))
                .putExtra("type", 4)
        )

    }

    fun gotoAdvices(v: View?) {
        startActivity(Intent(applicationContext, AdvicesActivity::class.java))
    }

    fun signOut(v: View?) {
        AppConstants.setPreferenceSessionID(null, applicationContext)
        startActivity(
            Intent(
                applicationContext,
                MainActivity::class.java
            ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
    }
}