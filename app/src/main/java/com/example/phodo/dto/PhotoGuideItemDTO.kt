package com.example.phodo.dto

import android.graphics.Bitmap
import android.location.Location
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.*


@Parcelize
data class PhotoGuideItemDTO (

    @SerializedName("photoGuideId")
    val photoGuideId : Int,

    @SerializedName("memberId") //누가 만들었는지 -> 메일주소(or 기본키 일련번호?)
    val memberId : Int,

    @SerializedName("originalImage")
    val photo : String, // bitmap 타입으로 변경

    @SerializedName("maskImage")
    val maskImage : String, // bitmap 타입으로 변경

    @SerializedName("contourImage")
    val contourImage : String, // bitmap 타입으로 변경

    /*
    @SerializedName("contourTransImage")
    val contourTransImage : String, // bitmap 타입으로 변경

     */

    @SerializedName("guideLine")
    val contourList : String, // 외과선

    @SerializedName("latitude")
    val latitude : Double,

    @SerializedName("longitude")
    val longitude : Double,

    @SerializedName("photoSpotName")
    val locationName : String,

    @SerializedName("hashtags")
    val tagList : List<String>,

    @SerializedName("heartNum")
    val heartNum : Int

) : Parcelable


//@SerializedName("guideImage")
//val guideImage : String,

//val proceessingImg //리스트에 띄울 때 그냥 이걸 띄울까 말까.....하...

