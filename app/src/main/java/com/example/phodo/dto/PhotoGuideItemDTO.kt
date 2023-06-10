package com.example.phodo.dto

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
    val memberId : String,

    @SerializedName("originalImage")
    val photo : String, // bitmap 타입으로 변경

    @SerializedName("maskImage")
    val maskImage : String,

    @SerializedName("width")  // 구글드라이브에서 다운로드 받으면 photo의 크기가 자동으로 바뀌는 이슈 해결을 위해 임시로
    val width : String,

    @SerializedName("height")
    val height : String,

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

    //@SerializedName("guideImage")
    //val guideImage : String,

    //val proceessingImg //리스트에 띄울 때 그냥 이걸 띄울까 말까.....하...

) : Parcelable



