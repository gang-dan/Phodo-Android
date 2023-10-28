package com.example.phodo.data

import android.graphics.Bitmap
import com.example.phodo.dto.*
import okhttp3.RequestBody
import org.opencv.core.MatOfPoint

interface RemoteDataSource {

    //포토가이드
    suspend fun getPhotoGuides() : List<PhotoGuidesDTO>
    suspend fun getDetailPhotoGuide(photoGuideId : Int) : PhotoGuideItemDTO

    //포토맵
    suspend fun getPhotoSpots(latitude:Double, longitude:Double, scope:Int, is_select:Boolean) : List<PhotoSpotsDTO>
    suspend fun getPhotoSpotInfo(photoSpotId : Int, latitude:Double,longitude:Double) : PhotoSpotItemDTO

    //로그인,인증
    suspend fun requestLogin(IdToken: String) : LoginDTO
    suspend fun requestLogout(access_token: String)
    suspend fun requestAccessToken(auth_code: String)

    //포토가이드 만들기
    suspend fun postPhotoGuide(access_token: String, userId: Int, originImg: Bitmap, contourImg: Bitmap, maskImg : Bitmap, contourTransImg: Bitmap,
                               tagList: List<String>, latitude : Double, longitude : Double, photoSpotName: String?)

}