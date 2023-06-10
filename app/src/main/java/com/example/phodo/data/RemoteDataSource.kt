package com.example.phodo.data

import com.example.phodo.dto.*

interface RemoteDataSource {

    //포토가이드
    suspend fun getPhotoGuides() : List<PhotoGuidesDTO>
    suspend fun getDetailPhotoGuide(photoGuideId : Int) : PhotoGuideItemDTO

    //포토맵
    suspend fun getPhotoSpots(latitude:Double, longitude:Double, scope:Int, is_select:Boolean) : List<PhotoSpotsDTO>
    suspend fun getPhotoSpotInfo(photoSpotId : Int, latitude:Double,longitude:Double) : PhotoSpotItemDTO

    //로그인,인증
    suspend fun requestLogin(auth_code: String) : LoginDTO
    suspend fun requestLogout(access_token: String)

    //포토가이드 만들기


}