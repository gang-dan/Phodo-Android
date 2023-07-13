package com.example.phodo.data

import com.example.phodo.RetrofitInstance
import com.example.phodo.dto.*


class RemoteDataSourceImp(private val apiClient : RetrofitInstance) : RemoteDataSource{

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

    override suspend fun requestLogin(auth_code : String) : LoginDTO {
        return apiClient.api.requestLogin(auth_code)
    }

    override suspend fun requestLogout(access_token: String) {
        return apiClient.api.logout(access_token)
    }


}