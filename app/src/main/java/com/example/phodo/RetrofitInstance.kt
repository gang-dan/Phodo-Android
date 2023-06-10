package com.example.phodo

import okhttp3.OkHttpClient
import okhttp3.internal.http2.Http2Reader.Companion.logger
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


object RetrofitInstance {

    //API 호출 서버
    const val BASE_URL = "http://phododo-env.eba-vn2bdd4z.ap-northeast-2.elasticbeanstalk.com"

    val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC  // 로그 레벨 BASIC
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()


    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) //GsonConverterFactory //ScalarsConverterFactory
            .build()

        }

        val api : ApiService by lazy {

            retrofit.create(ApiService::class.java)
        }
}
