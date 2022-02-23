package com.followmanager.app.api

import android.content.Context
import android.util.Log
import android.widget.Button
import com.followmanager.app.R
import com.followmanager.app.utils.AppConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object API {

    fun unfollowUser(context: Context, user: User, button: Button) {
        val sessionID = AppConstants.getPreferenceSessionID(context)
        val request = ServiceBuilder.buildService(Endpoints::class.java)

        if (sessionID != null) {
            val call = request.unfollowUser(sessionID, user.pk)
            call.enqueue(object : Callback<Boolean> {
                override fun onResponse(
                    call: Call<Boolean>,
                    response: Response<Boolean>
                ) {
                    if (response.isSuccessful) {
                        user.is_following = false
                        button.text = context.getString(R.string.follow)
                        button.setBackgroundColor(context.resources.getColor(R.color.green))
                        button.setOnClickListener {
                            followUser(context, user, button)
                        }
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Log.e("FM", t.toString())
                }

            })

        }
    }

    fun followUser(context: Context, user: User, button: Button) {
        val sessionID = AppConstants.getPreferenceSessionID(context)
        val request = ServiceBuilder.buildService(Endpoints::class.java)

        if (sessionID != null) {
            val call = request.followUser(sessionID, user.pk)
            call.enqueue(object : Callback<Boolean> {
                override fun onResponse(
                    call: Call<Boolean>,
                    response: Response<Boolean>
                ) {
                    if (response.isSuccessful) {
                        user.is_following = true
                        button.text = context.getString(R.string.unfollow)
                        button.setBackgroundColor(context.resources.getColor(R.color.red))
                        button.setOnClickListener {
                            unfollowUser(context, user, button)
                        }
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Log.e("FM", t.toString())
                }

            })

        }
    }
}