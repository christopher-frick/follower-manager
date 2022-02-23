package com.followmanager.app.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.followmanager.app.R
import com.followmanager.app.activities.FollowerProfileActivity
import com.followmanager.app.api.API
import com.followmanager.app.api.User
import com.followmanager.app.utils.Utils

class FollowerAdapter(
    var followers: Array<User>
) : RecyclerView.Adapter<FollowerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_follower, parent, false)
        return FollowerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowerViewHolder, position: Int) {
        val follower = followers[position]
        val context = holder.itemView.context

        holder.apply {
            profile_pic.setOnClickListener {
                startActivity(
                    context,
                    Intent(context, FollowerProfileActivity::class.java)
                        .putExtra("info", Utils.userToJson(follower)),
                    null
                )
            }
            Glide.with(context).load(follower.profile_pic_url).into(profile_pic)
            username.text = "@" + follower.username

            if (follower.is_following) {
                button.text = holder.itemView.context.getString(R.string.unfollow)
                button.setBackgroundColor(context.resources.getColor(R.color.red))
                button.setOnClickListener {
                    API.unfollowUser(context, follower, button)
                }
            } else {
                button.text = context.getString(R.string.follow)
                button.setBackgroundColor(context.resources.getColor(R.color.green))
                button.setOnClickListener {
                    API.followUser(context, follower, button)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return followers.size
    }

}

class FollowerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val profile_pic = itemView.findViewById<ImageView>(R.id.follower_profile_pic)
    val username = itemView.findViewById<TextView>(R.id.follower_username)
    val button = itemView.findViewById<Button>(R.id.follower_button)
}