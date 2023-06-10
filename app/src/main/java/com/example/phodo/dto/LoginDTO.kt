package com.example.phodo.dto

import android.graphics.Bitmap
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class LoginDTO(
    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("accessTokenExpireTime")
    val accessTokenExpireTime: String,

    @SerializedName("isNewMember")
    val isNewMember: Boolean,

    @SerializedName("memberId")
    val memberId : Int,

    @SerializedName("memberName")
    val memberName : String,

    @SerializedName("profileImage")
    val profileImage : String,

    @SerializedName("refreshToken")
    val refreshToken : String,

    @SerializedName("refreshTokenExpireTime")
    val refreshTokenExpireTime : String


) : Parcelable