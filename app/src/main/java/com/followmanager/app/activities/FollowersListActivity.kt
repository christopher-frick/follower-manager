package com.followmanager.app.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.followmanager.app.MainActivity
import com.followmanager.app.R
import com.followmanager.app.adapters.FollowerAdapter
import com.followmanager.app.api.Endpoints
import com.followmanager.app.api.ServiceBuilder
import com.followmanager.app.api.User
import com.followmanager.app.utils.AppConstants
import com.followmanager.app.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowersListActivity : AppCompatActivity() {

    private var usersList: Map<String, User> = mapOf<String, User>()

    private var sessionID: String? = null
    private lateinit var userID: String

    private var isLoaded: Boolean = false

    private var type = 0

    private lateinit var progress: ProgressBar
    private lateinit var userInfo: User
    private lateinit var followers: Map<String, User>
    private lateinit var following: Map<String, User>

    private var gainedFollowers: MutableMap<String, User> = mutableMapOf<String, User>()
    private var lostFollowers: MutableMap<String, User> = mutableMapOf<String, User>()

    private var mutualFollowers: MutableMap<String, User> = mutableMapOf<String, User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_followers_list)

        progress = findViewById(R.id.progressBarList)
        sessionID = AppConstants.getPreferenceSessionID(applicationContext)
        type = intent.getIntExtra("type", 0)

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

        findViewById<TextView>(R.id.followers_list_title).text = (intent.getStringExtra("title"))
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

    fun calculateFollowers() {
        // If any of the arrays hasn't been initialized then return
        if (!this::followers.isInitialized || !this::following.isInitialized) return;

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
        }

        var mutualFollowCount = 0;
        for (follower in followers) {
            for (following in following) {
                if (follower.key == following.key) {
                    mutualFollowCount++
                    mutualFollowers[follower.key] = follower.value
                }
            }
        }


        isLoaded = true
        checkType(type)
    }

    fun loadFollowersRecyclerView(jsonList: String) {

        usersList = Utils.jsonToMap(jsonList) ?: usersList

        val recycler = findViewById<RecyclerView>(R.id.followers_recycler)
        recycler.adapter = FollowerAdapter(Utils.mapToArray(usersList))
        recycler.layoutManager = LinearLayoutManager(this)

        progress.visibility = View.GONE
        recycler.visibility = View.VISIBLE
    }

    fun checkType(type: Int) {
        if (type == 1) {
            userDontFollowBack()
        } else if (type == 2) {
            followerDontFollowBack()
        } else if (type == 3) {
            gainedFollowers()
        } else if (type == 4) {
            lostFollowers()
        } else {
            Toast.makeText(this, "Error please try again!", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    fun userDontFollowBack() {
        if (!isLoaded) return

        var listUsers = mutableMapOf<String, User>()
        for (f in followers) {
            var found = false
            for (m in mutualFollowers) {
                if (f.key == m.key) found = true;
            }

            if (!found) {
                listUsers[f.key] = f.value
                listUsers[f.key]!!.is_following = false
            }
        }

        val jsonList = Utils.mapToJson(listUsers)
        loadFollowersRecyclerView(jsonList)
    }

    fun followerDontFollowBack() {
        if (!isLoaded) return

        var listUsers = mutableMapOf<String, User>()
        for (f in following) {
            var found = false
            for (m in mutualFollowers) {
                if (f.key == m.key) found = true;
            }

            if (!found) {
                listUsers[f.key] = f.value
                listUsers[f.key]!!.is_following = false
            }
        }

        val jsonList = Utils.mapToJson(listUsers)
        loadFollowersRecyclerView(jsonList)
    }

    fun gainedFollowers() {
        if (!isLoaded) return

        var listUsers = HashMap<String, User>()
        for (g in gainedFollowers) {
            listUsers[g.key] = g.value


            var found = false
            for (f in following) {
                if (f.key == g.key) found = true
            }

            listUsers[g.key]!!.is_following = found
        }

        val jsonList = Utils.mapToJson(listUsers)
        loadFollowersRecyclerView(jsonList)
    }

    fun lostFollowers() {
        if (!isLoaded) return

        var listUsers = mutableMapOf<String, User>()
        for (l in lostFollowers) {
            listUsers[l.key] = l.value

            var found = false
            for (f in following) {
                if (f.key == l.key) found = true
            }

            listUsers[l.key]!!.is_following = found
        }

        val jsonList = Utils.mapToJson(listUsers)
        loadFollowersRecyclerView(jsonList)
    }

    fun goBack(v: View?) {
        finish()
    }

}