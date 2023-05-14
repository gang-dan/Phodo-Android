package com.example.phodo

import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyPageViewModel: ViewModel() {
    val accountLiveData = MutableLiveData<TextView>()


}