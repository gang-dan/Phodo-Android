package com.example.phodo.PhotoMap

import android.graphics.Bitmap
import android.location.Location


data class PhotoSpotItem (

    val location : Location,
    val location_name : String?,
    val guide_size : Int,
    val location_img : Int //아무거나 대표이미지 한개 주면 됨

)
