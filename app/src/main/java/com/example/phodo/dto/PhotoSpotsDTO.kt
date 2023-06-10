package com.example.phodo.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoSpotsDTO (
    @SerializedName("photoSpotId")
    val photoSpotId: Int,

    @SerializedName("photoGuideNum")
    val photoGuideNum: Int,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double

) : Parcelable