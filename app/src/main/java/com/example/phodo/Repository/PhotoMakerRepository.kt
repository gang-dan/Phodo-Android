package com.example.phodo.Repository

import android.graphics.Bitmap
import com.example.phodo.data.RemoteDataSourceImp
import com.example.phodo.dto.PhotoSpotsDTO
import kotlinx.serialization.json.Json
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint

class PhotoMakerRepository(private val remoteDataSource: RemoteDataSourceImp) {

    suspend fun getPhotoSpotInfo(latitude:Double, longitude:Double, scope:Int, is_select:Boolean) : List<PhotoSpotsDTO> {
        return remoteDataSource.getPhotoSpots(latitude,longitude,scope,is_select) //굳이 is_select 줄 필요가 없을듯?,,,

    }
    suspend fun postPhotoGuide(access_token: String, userId: Int, originImg: Bitmap, contourImg: Bitmap, maskImg : Bitmap,contourTransImg: Bitmap,
                               tagList: List<String>, latitude : Double, longitude : Double, photoSpotName: String?) {
        return remoteDataSource.postPhotoGuide(access_token, userId, originImg, contourImg, maskImg, contourTransImg, tagList, latitude, longitude, photoSpotName)

    }

}