package com.example.phodo.dto

import android.graphics.Bitmap
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import org.opencv.core.MatOfPoint

@Parcelize
data class PhotoGuideApplyDTO (

    @SerializedName("originalImage")
    val photo : Bitmap,

    @SerializedName("maskImage")
    val maskImage : String,

    @SerializedName("guideLine")
    val contourList : String,

    ): Parcelable