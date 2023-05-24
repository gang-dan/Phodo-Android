package com.example.phodo.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {

    private val prefs: SharedPreferences =
    context.getSharedPreferences("prefs_phodo", Context.MODE_PRIVATE)

    val accessToken: String
        get() = prefs.getString("access_token", "").toString()

    val userName: String
        get() = prefs.getString("user_name", "").toString()

    val userEmail: String
        get() = prefs.getString("user_email", "").toString()

    val userProfileImg: String
        get() = prefs.getString("user_profile_img", "").toString()

    fun setAuthCode(authCode: String) {
        prefs.edit()?.apply {
            putString("auth_code", authCode)
        }?.apply()
    }


    fun setAccessToken(str: String) {
        prefs.edit().putString("access_token", str).apply()
    }

    fun setUserInfo(userName: String,userEmail : String, userProfileImg : String) {
        prefs.edit().putString("user_name", userName).apply()
        prefs.edit().putString("user_email", userEmail).apply()
        prefs.edit().putString("user_profile_img", userProfileImg).apply()
    }


    fun deleteAccessToken() {
        prefs.edit()?.apply {
            remove("accessToken")
        }?.apply()
    }


}