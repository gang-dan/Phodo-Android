package com.example.phodo.dto

import android.graphics.Bitmap
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoMakerResponseDTO (

    @SerializedName("photoGuideId")
    val photoGuideId : Int,

    @SerializedName("photoSpotName")
    val photoSpotName : String,

    @SerializedName("latitude")
    val latitude : Double,

    @SerializedName("longitude")
    val longitude : Double,

    @SerializedName("guideJsonFile")
    val contourList : String,

    @SerializedName("width")
    val width : Double,

    @SerializedName("height")
    val height : Double,

) : Parcelable
