package com.example.phodo.data

import android.graphics.Bitmap
import android.provider.Settings.Global.getString
import android.util.Log
import com.example.phodo.LoginService
import com.example.phodo.R
import com.example.phodo.RetrofitInstance
import com.example.phodo.dto.*
import okhttp3.RequestBody
import org.json.JSONObject
import org.opencv.core.MatOfPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body


class RemoteDataSourceImp(private val apiClient : RetrofitInstance) : RemoteDataSource{

    /* api를 기능별로 만들어서 remoteDataSource로 다형성 보장  */

    object ClientInformation {
        const val CLIENT_ID = "214501978899-i8eb4aol6gb0r3cuau2iq4i8sdgoqdrp.apps.googleusercontent.com"
        const val CLIENT_SECRET = "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"
    }

    override suspend fun getPhotoGuides(): List<PhotoGuidesDTO> {
        return apiClient.api.getAllPhotoGuide()
    }

    override suspend fun getDetailPhotoGuide(photoGuideId : Int): PhotoGuideItemDTO {
       return apiClient.api.getDetailPhotoGuide(photoGuideId)
    }

    override suspend fun getPhotoSpots(latitude:Double, longitude:Double, scope:Int, is_select:Boolean): List<PhotoSpotsDTO> {
        return apiClient.api.getPhotoSpots(latitude, longitude, scope,true) //is_select
    }

    override suspend fun getPhotoSpotInfo(photoSpotId : Int, latitude:Double,longitude:Double): PhotoSpotItemDTO {
        return apiClient.api.getPhotoSpotInfo(photoSpotId,latitude,longitude)
    }

    override suspend fun postPhotoGuide(access_token: String, userId: Int, originImg: Bitmap,  contourImg: Bitmap, maskImg : Bitmap, contourTransImg: Bitmap,
                                        tagList: List<String>, latitude : Double, longitude : Double, photoSpotName: String?) {
        return apiClient.api.requestMakePhotoGuide(access_token, userId, originImg, contourImg, maskImg, contourTransImg, tagList, latitude, longitude, photoSpotName)
    }


    override suspend fun requestLogin(IdToken : String) : LoginDTO {

        return apiClient.api.requestLogin(IdToken) //IdToken
    }

    override suspend fun requestLogout(access_token: String) {
        return apiClient.api.logout(access_token)
    }

    override suspend fun requestAccessToken(auth_code: String){
        LoginService.loginRetrofit("https://oauth2.googleapis.com").getAccessToken( // https://www.googleapis.com/
            request = LoginGoogleRequestModel(
                code = auth_code.orEmpty(),
                client_id = ClientInformation.CLIENT_ID,
                client_secret = ClientInformation.CLIENT_SECRET,
                //redirect_uri = "https://phododo-env.eba-vn2bdd4z.ap-northeast-2.elasticbeanstalk.com/login/oauth2/code/google",
                grant_type = "authorization_code",
            )
        ).enqueue(object : Callback<LoginGoogleResponseModel> {
            override fun onResponse(call: Call<LoginGoogleResponseModel>, response: Response<LoginGoogleResponseModel>) {
                if(response.isSuccessful) {
                    val accessToken = response.body()?.access_token.orEmpty()
                    Log.d("accessToken", "accessToken: $accessToken")

                    val authorizationHeader = "Bearer $accessToken"
                    Log.d("authorizationHeader", "authorizationHeader: $authorizationHeader")

                    LoginService.loginRetrofit("https://www.googleapis.com").getUserInfo(authorizationHeader
                    ).enqueue(object : Callback<UserInfo> {
                        override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                            if (response.isSuccessful) {
                                val userInfo = response.body()
                                val userId = userInfo?.id
                                val userEmail = userInfo?.email
                                val userName = userInfo?.name

                                Log.d("userInfo", "userInfo: $userInfo")
                                Log.d("userId", "userId: $userId")
                                Log.d("userEmail", "userEmail: $userEmail")
                                Log.d("userName", "userName: $userName")
                            } else {
                                // API 요청 실패
                                Log.d("Failure", "Failure")
                            }
                        }

                        override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                            // 네트워크 오류 등으로 인해 요청 실패
                            Log.e("Error", "getOnFailure: ",t.fillInStackTrace() )
                        }
                    })

                    //Log.d("expire", "${response.body()?.expires_in.orEmpty()}")
                    //Log.d("refreshToken", "${response.body()?.refresh_token.orEmpty()}")
                    //sendAccessToken(accessToken)
                }else{
                    Log.d("accessToken", "Failure")
                }
            }
            override fun onFailure(call: Call<LoginGoogleResponseModel>, t: Throwable) {
                Log.e("Error", "getOnFailure: ",t.fillInStackTrace() )
            }
        })
    }


}