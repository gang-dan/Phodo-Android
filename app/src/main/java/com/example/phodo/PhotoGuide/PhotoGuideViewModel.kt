package com.example.phodo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.phodo.PhotoGuide.PhotoGuideItem

class PhotoGuideListViewModel : ViewModel() {

    //화면에 표시시될 변경(관찰)가능한 MutableLiveData
    val guideLiveData = MutableLiveData<List<PhotoGuideItem>>() //변경/관찰가능한 List<Todo>타입에 LiveData
    //lateinit var livedata: LiveData<PhotoGuideItem> //새롭게 업데이트된 포토가이드 리스트를 받는 LiveData
    //private val data = arrayListOf<PhotoGuideItem>()

    //뷰모델을 생성하고 초기 셋팅, 서버 통신할 땐 불필요
    fun setListVideModel(guide_item_list : List<PhotoGuideItem>) {
        this.guideLiveData.value = guide_item_list
    }

    //리사이클러뷰 새로고침 요청
    //Repositiry로부터 최신 데이터를 요청해 guideLiveData를 변경해줍니다



    //아이템을 선택해 Detail 화면으로 넘어갈 때 호출
    /*
    fun moveToDetailActivity(context : Context, guide_item: PhotoGuideItem) {
        val intent = Intent(context, PhotoGuideDetail::class.java)
        intent.putExtra("obj", guide_item) // parcel 클래스 대신 Serializable 클래스로 객체 전달
        context.startActivity(intent)

    }
     */



}