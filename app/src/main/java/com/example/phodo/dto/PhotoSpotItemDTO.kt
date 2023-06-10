package com.example.phodo.dto

import android.graphics.Bitmap
import android.location.Location
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.net.URL

@Parcelize
data class PhotoSpotItemDTO (

    @SerializedName("photoSpotId")
    val photoSpotId : Int,

    @SerializedName("photoSpotName")
    val photoSpotName : String?,

    @SerializedName("photoSpotImage")
    val photoSpotImage : URL,

    @SerializedName("hashtags")
    val hashtags : List<String>,

    @SerializedName("photoGuideNum")
    val photoGuideNum : Int,

    @SerializedName("myDistance")
    val myDistance : Int

) : Parcelable



