package com.example.phodo.dto

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    // 기타 필요한 필드들도 추가할 수 있습니다.
)