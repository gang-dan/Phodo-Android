package com.example.phodo.dto

import android.graphics.Bitmap
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class  PhotoMakerRequestDTO (

    @SerializedName("photoImagee")
    val photoImagee : Bitmap,

    @SerializedName("contourList")
    val contourList : String,  // 사용자가 변경한 외곽선 리스트

    @SerializedName("tagList")
    val tagList : List<String>?

    ) : Parcelable
