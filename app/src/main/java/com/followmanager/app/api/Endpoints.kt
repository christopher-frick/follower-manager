package com.followmanager.app.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


internal interface Endpoints {
    @FormUrlEncoded
    @POST("auth/login")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<String>

    @FormUrlEncoded
    @POST("user/info")
    fun getUserInfo(
        @Field("sessionid") sessionid: String,
        @Field("user_id") userid: String
    ): Call<User>

    @FormUrlEncoded
    @POST("user/info_by_username")
    fun getUserInfoByUsername(
        @Field("sessionid") sessionid: String,
        @Field("username") username: String
    ): Call<User>

    @FormUrlEncoded
    @POST("user/followers")
    fun getUserFollowers(
        @Field("sessionid") sessionid: String,
        @Field("user_id") userid: String
    ): Call<Map<String, User>>

    @FormUrlEncoded
    @POST("user/following")
    fun getUserFollowing(
        @Field("sessionid") sessionid: String,
        @Field("user_id") userid: String
    ): Call<Map<String, User>>

    @FormUrlEncoded
    @POST("user/follow")
    fun followUser(
        @Field("sessionid") sessionid: String,
        @Field("user_id") userid: String
    ): Call<Boolean>

    @FormUrlEncoded
    @POST("user/unfollow")
    fun unfollowUser(
        @Field("sessionid") sessionid: String,
        @Field("user_id") userid: String
    ): Call<Boolean>

}
