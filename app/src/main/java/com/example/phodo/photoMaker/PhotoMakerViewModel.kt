package com.example.phodo.photoMaker

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.phodo.Repository.PhotoGuideRepository
import com.example.phodo.Repository.PhotoMakerRepository
import org.json.JSONObject

class PhotoMakerViewModel(private val photomakerRepository: PhotoMakerRepository) : ViewModel()  {

    //var isMake = MutableLiveData<Boolean>()
    val finalContourJson = MutableLiveData<JSONObject>()
    val tagList = MutableLiveData<Set<String>>()
    val trevi_width = 1080.0
    val trevi_height = 1440.0

    fun requestPhotoGuide() {

    }

}
/*
class PhotoMakerViewModel(val appli: Application, private val photomakerRepository: PhotoMakerRepository) : AndroidViewModel(appli)  {

    //var isMake = MutableLiveData<Boolean>()
    val finalContourJson = MutableLiveData<JSONObject>()
    val tagList = MutableLiveData<Set<String>>()
    val trevi_width = 1080.0
    val trevi_height = 1440.0

    fun requestPhotoGuide() {

    }

}
* */