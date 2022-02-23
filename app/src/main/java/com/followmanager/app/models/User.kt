package com.followmanager.app.api

data class User(
    val pk: String,
    val username: String,
    val full_name: String,
    val profile_pic_url: String,
    val profile_pic_url_hd: String,
    val follower_count: Int,
    val following_count: Int,
    val is_private: Boolean,
    var is_following: Boolean = false
)
