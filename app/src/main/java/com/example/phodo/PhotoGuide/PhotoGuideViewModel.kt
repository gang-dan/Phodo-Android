package com.example.phodo

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.phodo.PhotoGuide.PhotoGuideItem
import com.example.phodo.Repository.PhotoGuideRepository

class PhotoGuideListViewModel : ViewModel() {

    //화면에 표시시될 변경(관찰)가능한 MutableLiveData
    //서버에서 받아온 PhotoItem 들을 저장
    val guideLiveData = MutableLiveData<List<PhotoGuideItem>>() //변경/관찰가능한 List<Todo>타입에 LiveData
    var selctedLiveData= MutableLiveData<PhotoGuideItem>()

    //뷰모델을 생성하고 초기 셋팅, 서버 통신할 땐 불필요
    fun getPhodoGiudeList(context: Context) {
        val phoguide_repo = PhotoGuideRepository(context)
        this.guideLiveData.value = phoguide_repo.getPhotoListData()
    }


    //리사이클러뷰 새로고침 요청
    //Repositiry로부터 최신 데이터를 요청해 guideLiveData를 변경해줍니다



}