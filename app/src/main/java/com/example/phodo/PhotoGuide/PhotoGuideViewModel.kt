package com.example.phodo

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phodo.dto.PhotoGuideItemDTO
import com.example.phodo.Repository.PhotoGuideRepository
import com.example.phodo.dto.PhotoGuidesDTO
import kotlinx.coroutines.launch

class PhotoGuideListViewModel(private val phoguideRepository: PhotoGuideRepository) : ViewModel() {

    //화면에 표시시될 변경(관찰)가능한 MutableLiveData
    //서버에서 받아온 PhotoItem 들을 저장
    val guideLiveData = MutableLiveData<List<PhotoGuidesDTO>>()


    //뷰모델을 생성하고 초기 셋팅, 서버 통신할 땐 불필요
    fun getPhodoGiudeList() {

        viewModelScope.launch {
            val photoGuides = phoguideRepository.getPhotoList()
            guideLiveData.value = photoGuides
        }

    }

    //리사이클러뷰 새로고침 요청
    //Repositiry로부터 최신 데이터를 요청해 guideLiveData를 변경해줍니다


}