package com.example.phodo.PhotoMap

import android.content.Context
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.phodo.PhotoGuide.PhotoGuideItem
import com.example.phodo.Repository.PhotoGuideRepository

class MapViewModel: ViewModel() {

    val spotLiveData = MutableLiveData<List<PhotoSpotItem>>()


    fun getPhotoSpotList(context: Context, current_loc:Location) {
        val phoguide_repo = PhotoGuideRepository(context)
        this.spotLiveData.value = phoguide_repo.getPhotoSpotListData(current_loc)
    }

}