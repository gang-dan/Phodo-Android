package com.example.phodo

import com.example.phodo.dto.LoginGoogleRequestModel
import com.example.phodo.dto.LoginGoogleResponseModel
import com.example.phodo.dto.UserInfo
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

interface LoginService {

    @POST("/token")
    fun getAccessToken(
        @Body request: LoginGoogleRequestModel
    ): Call<LoginGoogleResponseModel>

    @GET("oauth2/v2/userinfo")
    fun getUserInfo(
        @Header("Authorization") accessToken: String
    ): Call<UserInfo>


    companion object {

        private val gson = GsonBuilder().setLenient().create()

        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC  // 로그 레벨 BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()

        fun loginRetrofit(baseUrl: String): LoginService {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(LoginService::class.java)
        }
    }
}