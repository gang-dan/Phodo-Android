package com.example.phodo

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class LoginRepository {

    private val getAccessTokenBaseUrl = "https://phododo-env.eba-vn2bdd4z.ap-northeast-2.elasticbeanstalk.com"

    fun getAccessToken(authCode:String) {
        LoginService.loginRetrofit(getAccessTokenBaseUrl).getAccessToken(
            request = LoginGoogleRequestModel(
                grant_type = "authorization_code",
                client_id = R.string.server_client_id,
                client_secret = R.string.server_client_scret,
                code = authCode.orEmpty()
            )
        ).enqueue(object : retrofit2.Callback<LoginGoogleResponseModel> {
            override fun onResponse(call: Call<LoginGoogleResponseModel>, response: Response<LoginGoogleResponseModel>) {
                if(response.isSuccessful) {
                    val accessToken = response.body()?.access_token.orEmpty()
                    Log.d("token", "accessToken: $accessToken")

                }
            }

            override fun onFailure(call: Call<LoginGoogleResponseModel>, t: Throwable) {
                Log.d("token", "faile")
            }

        })
    }
}



