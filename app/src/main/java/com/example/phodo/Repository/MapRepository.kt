package com.example.phodo.Repository

import android.location.Location
import com.example.phodo.R
import com.example.phodo.data.RemoteDataSourceImp
import com.example.phodo.dto.PhotoSpotItemDTO
import com.example.phodo.dto.PhotoSpotsDTO
import com.google.android.gms.common.api.Scope

class MapRepository(private val remoteDataSource: RemoteDataSourceImp) {

    suspend fun getPhotoSpots(latitude:Double, longitude:Double, scope:Int, is_select:Boolean) : List<PhotoSpotsDTO> {
        return remoteDataSource.getPhotoSpots(latitude,longitude,scope,is_select) //굳이 is_select 줄 필요가 없을듯?,,,

    }

    suspend fun getPhotoSpotInfo(photoSpotId : Int, latitude:Double,longitude:Double) : PhotoSpotItemDTO {
        return remoteDataSource.getPhotoSpotInfo(photoSpotId,latitude,longitude)
    }

    //현재 맵의 중심점을 기준으로 같은 이름의 스팟 Location과 해당 스팟의 가이드 개수 넘겨
    /*
    suspend fun getCurrentPhotoSpotList(loc: Location, loc_name : String?, scope: Int = 500, is_select:Boolean = false) : List<PhotoSpotItemDTO> {

        return remoteDataSource.getPhotoSpots(loc,loc_name,scope,is_select)

        /*
        //실제로는 위치 정보,갯수,위치 이름, 대표 사진 보내줘야 함 -> 나도 클래스 만들어야 할듯,,,ㅜㅜ
        var photoSpotList = arrayListOf<PhotoSpotItemDTO>()

        val location2 = Location("LocationManager.GPS_PROVIDER")
        location2.latitude = 37.52878
        location2.longitude = 126.96566

        for (i in 0 until 1) {
            val spot = PhotoSpotItemDTO(location2,"아이파크몰",3, R.drawable.sample_swiss)
            photoSpotList.add(spot)

        }

        return photoSpotList

        */
    }

     */
    /*
    suspend fun getSelectedPhotoSpotList(loc: Location, loc_name : String?, scope: Int = 500, is_select:Boolean = true) : List<PhotoSpotItemDTO> {

        return remoteDataSource.getPhotoSpots(loc,loc_name,scope,is_select)

        /*
        //실제로는 위치 정보,갯수,위치 이름, 대표 사진 보내줘야 함 -> 나도 클래스 만들어야 할듯,,,ㅜㅜ
        var photoSpotList = arrayListOf<PhotoSpotItemDTO>()

        val location2 = Location("LocationManager.GPS_PROVIDER")
        location2.latitude = 37.52878
        location2.longitude = 126.96566

        for (i in 0 until 1) {
            val spot = PhotoSpotItemDTO(location2,"아이파크몰",3, R.drawable.sample_swiss)
            photoSpotList.add(spot)

        }

        return photoSpotList

        */
    }

     */


}