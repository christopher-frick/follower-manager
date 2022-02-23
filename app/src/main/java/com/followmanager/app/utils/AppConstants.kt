package com.followmanager.app.utils

import android.content.Context
import com.followmanager.app.api.User

object AppConstants {
    /*SHARED PREFERENCE VARIABLES*/
    const val PREF_APP_NAME: String = "prefs"
    val PREF_USER_ID: String? = "user_id"
    val PREF_USER_NAME: String? = "user_name"
    val PREF_USER_PROFILE: String? = "user_profile"
    val PREF_LOGIN: String? = "LOGIN_PREF"
    val DEVICE_TOKEN: String? = "DeviceToken"
    val DARK_MODE: String? = "NIGHT_MODE"
    const val SESSION_ID: String = "SESSION_ID"
    const val FOLLOWERS: String = "FOLLOWERS"
    const val FOLLOWING: String = "FOLLOWING"
    const val LANGUAGE: String = "LANGUAGE"

    const val PREF_IS_LOGIN = "is_login"
    const val PREF_TOKEN = "token"
    const val FIREBASE_CHILD_CHAT_USER = "ChatUserList"
    const val FIREBASE_CHILD_USER = "User"
    const val PREF_IS_DARK_MODE = "is_dark_mode"

    fun setPreferenceIntUserId(key: String?, value: Int, context: Context) {

        val sharedPreference = context.getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putInt(key, value)
        editor.apply()

    }

    fun getPreferenceIntUserId(key: String?, context: Context): Int {
        val sharedPreference = context.getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE)
        return sharedPreference.getInt(key, 0)
    }

    fun setPreferenceStringName(key: String?, value: String, context: Context) {
        val sharedPreference = context.getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getPreferenceStringName(key: String?, context: Context): String? {
        val sharedPreference = context.getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE)
        return sharedPreference.getString(key, "")
    }

    fun setPreferenceStringProfileImage(key: String?, value: String, context: Context) {
        val sharedPreference = context.getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getPreferenceStringProfileImage(key: String?, context: Context): String? {
        val sharedPreference = context.getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE)
        return sharedPreference.getString(key, "")
    }

    fun setPreferenceBooleanIsLogin(key: String?, value: Boolean, context: Context) {
        val sharedPreference = context.getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE)
        //SharedPreferences sharedPreference = AppController.getInstance().getAppContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        val editor = sharedPreference.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getPreferenceBooleanIsLogin(key: String?, context: Context): Boolean {
        val sharedPreference = context.getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE)
        //SharedPreferences sharedPreference = AppController.getInstance().getAppContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        return sharedPreference.getBoolean(key, false)
    }

    fun setPreferenceToken(key: String?, value: String, context: Context) {
        val sharedPreference = context.getSharedPreferences(DEVICE_TOKEN, Context.MODE_PRIVATE)
        //SharedPreferences sharedPreference = AppController.getInstance().getAppContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        val editor = sharedPreference.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun setPreferenceSessionID(value: String?, context: Context) {
        val sharedPreference = context.getSharedPreferences(SESSION_ID, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        if (value == null) {
            editor.clear()
        } else {
            editor.putString(SESSION_ID, value)
        }
        editor.apply()
    }

    fun getPreferenceSessionID(context: Context): String? {
        val sharedPreference = context.getSharedPreferences(SESSION_ID, Context.MODE_PRIVATE)
        return sharedPreference.getString(SESSION_ID, "")
    }

    fun getPreferenceLanguage(context: Context): String? {
        val sharedPreference = context.getSharedPreferences(LANGUAGE, Context.MODE_PRIVATE)
        return sharedPreference.getString(LANGUAGE, "")
    }

    fun setPreferenceLanguage(value: String?, context: Context) {
        val sharedPreference = context.getSharedPreferences(LANGUAGE, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        if (value == null) {
            editor.clear()
        } else {
            editor.putString(LANGUAGE, value)
        }
        editor.apply()
    }


    fun setPreferenceFollowersList(value: Map<String, User>?, context: Context) {
        val sharedPreference = context.getSharedPreferences(FOLLOWERS, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        if (value == null) {
            editor.clear()
        } else {
            editor.putString(FOLLOWERS, Utils.mapToJson(value))

        }
        editor.apply()
    }

    fun getPreferenceFollowersList(context: Context): Map<String, User>? {
        val sharedPreference = context.getSharedPreferences(FOLLOWERS, Context.MODE_PRIVATE)

        val json = sharedPreference.getString(FOLLOWERS, "") ?: return null
        return Utils.jsonToMap(json)
    }

    fun setPreferenceFollowingList(value: Map<String, User>?, context: Context) {
        val sharedPreference = context.getSharedPreferences(FOLLOWING, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        if (value == null) {
            editor.clear()
        } else {
            editor.putString(FOLLOWING, Utils.mapToJson(value))
        }
        editor.apply()
    }

    fun getPreferenceFollowingList(context: Context): Map<String, User>? {
        val sharedPreference = context.getSharedPreferences(FOLLOWING, Context.MODE_PRIVATE)

        val json = sharedPreference.getString(FOLLOWING, "") ?: return null
        return Utils.jsonToMap(json)
    }

}

