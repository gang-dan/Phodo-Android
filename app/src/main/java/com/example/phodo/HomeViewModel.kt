package com.example.phodo

import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.phodo.PhotoGuide.PhotoGuideItem
import com.google.android.gms.auth.api.signin.GoogleSignIn

class HomeViewModel: ViewModel() {

    //val accountLiveData = MutableLiveData<TextView>()
    var photoGuide = MutableLiveData<PhotoGuideItem>()
    var isPhootGuide = MutableLiveData<Boolean>()

    // 서버에 access 토큰 및 사용자 정보 요청
    fun requestLogin() {
        // api 호출

        // 멤버 정보 세팅
        //HomeActivity.prefs.setUserProfile()

        //토큰 정보 세팅
        //HomeActivity.prefs.setAccessToken("accessToken", accessToken)
    }

    // 현재 가지고 있는 access 토큰이 유효한지 확인
    fun isValidToken() : Boolean {
        return true

    }



}