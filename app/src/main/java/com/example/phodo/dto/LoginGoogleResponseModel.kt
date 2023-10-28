package com.example.phodo.dto

import com.google.gson.annotations.SerializedName

data class LoginGoogleResponseModel(
    @SerializedName("access_token") var access_token: String,
    @SerializedName("expires_in") var expires_in: String,
    @SerializedName("refresh_token") var refresh_token: String,
    @SerializedName("scope") var scope: String,
    @SerializedName("token_type") var token_type: String
)