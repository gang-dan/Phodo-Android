package com.example.phodo.utils

import android.content.Context
import android.content.SharedPreferences
import org.opencv.android.Utils

class PreferenceUtils {

//    private var prefs: SharedPreferences =
//    context.getSharedPreferences("prefs_phodo", Context.MODE_PRIVATE)

    private val PREFS = "prefs"
    private var mContext: Context? = null
    private var prefs: SharedPreferences? = null
    private var prefsEditor: SharedPreferences.Editor? = null



    companion object {
        private var instance: PreferenceUtils? = null
        @Synchronized
        fun init(context: Context?): PreferenceUtils? {
            if (instance == null) instance = PreferenceUtils(context!!)
            return instance
        }
    }

    constructor(context: Context) {
        mContext = context
        prefs = mContext!!.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefsEditor = prefs!!.edit()
    }

//    @Synchronized
//    init(context: Context?): com.example.phodo.utils.Utils? {
//        if (instance == null) instance = com.example.phodo.utils.Utils(context!!)
//        return instance
//    }
//
//    private fun Utils(context: Context) {
//        mContext = context
//        prefs = mContext!!.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
//        prefsEditor = prefs.edit()
//    }

    fun setAccessToken(value: String) {
        prefsEditor!!.putString("access_token", value).commit()
    }

    fun setAccessTokenExpTime(str: String) {
        //prefs!!.edit().putString("accessToken_expireTime", str).apply()
        prefsEditor!!.putString("accessToken_expireTime", str).commit()
    }

    fun getAccessToken(): String? {
        return prefs!!.getString("access_token", "")
    }

    fun setRefreshToken(value: String?) {
        prefsEditor!!.putString("access_token", value).commit()
    }

    fun setRefreshTokenExpTime(str: String) {
        prefsEditor!!.putString("refreshToken_expireTime", str).commit()
    }

    fun getRefreshToken(defValue: String?): String? {
        return prefs!!.getString("refresh_token", defValue)
    }

    fun clearToken() {
        prefsEditor!!.clear().apply()
    }


//    val accessToken: String
//        get() = prefs.getString("access_token", "").toString()

    val isMember: String
        get() = prefs!!.getBoolean("is_member", false).toString()

    val userId: Int
        get() = prefs!!.getInt("user_id", -1)

    val userName: String
        get() = prefs!!.getString("user_name", "").toString()

    val userEmail: String
        get() = prefs!!.getString("user_email", "").toString()

    val userProfileImg: String
        get() = prefs!!.getString("user_profile_img", "").toString()

    fun setUserInfo(isMember:Boolean, userId: Int, userName: String, userEmail : String, userProfileImg : String?) {
        prefs!!.edit().putBoolean("is_member", isMember).apply()
        prefs!!.edit().putInt("user_id", userId).apply()
        prefs!!.edit().putString("user_name", userName).apply()
        prefs!!.edit().putString("user_email", userEmail).apply()
        prefs!!.edit().putString("user_profile_img", userProfileImg).apply()
    }

//    fun deleteUserInfo() {
//        prefs!!.edit()?.apply {
//            remove("user_id")
//        }?.apply()
//
//        prefs!!.edit()?.apply {
//            remove("user_name")
//        }?.apply()
//
//        prefs!!.edit()?.apply {
//            remove("user_email")
//        }?.apply()
//
//        prefs!!.edit()?.apply {
//            remove("user_profile_img")
//        }?.apply()
//
//        prefs.edit()?.commit()
//    }


//
//    fun setAccessToken(str: String) {
//        prefs.edit().putString("access_token", str).apply()
//    }
//
//    fun setRefreshToken(str: String) {
//        prefs.edit().putString("refresh_token", str).apply()
//    }
//    fun setRefreshTokenExpTime(str: String) {
//        prefs.edit().putString("refreshToken_expireTime", str).apply()
//    }


//
//    fun deleteAccessToken() {
//        prefs.edit()?.apply {
//            remove("access_token")
//            //Log.d("deleteAccessToken","토큰이 삭제되었습니다.")
//        }?.apply()
//
//        prefs.edit()?.apply {
//            remove("accessToken_expireTime")
//        }?.apply()
//
//        prefs.edit()?.apply {
//            remove("refresh_token")
//        }?.apply()
//
//        prefs.edit()?.apply {
//            remove("refreshToken_expireTime")
//        }?.apply()
//
//        prefs.edit()?.commit()
//    }
//



}