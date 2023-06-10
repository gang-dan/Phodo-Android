package com.example.phodo.PhotoMap

import android.content.Context
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phodo.Repository.MapRepository
import com.example.phodo.Repository.PhotoGuideRepository
import com.example.phodo.dto.PhotoSpotItemDTO
import com.example.phodo.dto.PhotoSpotsDTO
import kotlinx.coroutines.launch

class MapViewModel(private val mapRepository: MapRepository): ViewModel() {

    val spotsLiveData = MutableLiveData<List<PhotoSpotsDTO>>()
    val spotInfoLiveData = MutableLiveData<PhotoSpotItemDTO>()
    val selectedMarker = MutableLiveData<PhotoSpotItemDTO>()


    fun getPhotoSpotList(isSelected : Boolean, photo_latitude : Double, photo_longitude : Double) {

            // 특정 포토가이드 화면에서 맵으로 이동한 경우
            viewModelScope.launch {
                spotsLiveData.value = mapRepository.getPhotoSpots(photo_latitude, photo_longitude,500, isSelected)
            }

    }

    fun getPhotoSpotInfo(photospot_id : Int, photo_latitude : Double, photo_longitude : Double) {
        viewModelScope.launch {
            spotInfoLiveData.value = mapRepository.getPhotoSpotInfo(photospot_id, photo_latitude, photo_longitude)
        }
    }

}