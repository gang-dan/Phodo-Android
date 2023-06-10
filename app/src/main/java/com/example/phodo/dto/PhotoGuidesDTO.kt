package com.example.phodo.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class PhotoGuidesDTO (

    @SerializedName("photoGuideId")
    val photoGuideId : Int,

    @SerializedName("photoGuideImage")
    val photo : String, // bitmap 타입으로 변경

    ) : Parcelable //, Serializable