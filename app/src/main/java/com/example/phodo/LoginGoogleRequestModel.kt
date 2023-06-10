package com.example.phodo

import com.google.gson.annotations.SerializedName

data class LoginGoogleRequestModel(
    @SerializedName("grant_type")
    private val grant_type: String,
    @SerializedName("client_id")
    private val client_id: Int,
    @SerializedName("client_secret")
    private val client_secret: Int,
    @SerializedName("code")
    private val code: String
)