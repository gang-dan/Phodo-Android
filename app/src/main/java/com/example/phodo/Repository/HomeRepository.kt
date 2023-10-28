package com.example.phodo.Repository

import com.example.phodo.data.RemoteDataSourceImp
import com.example.phodo.dto.LoginDTO
import com.example.phodo.dto.PhotoSpotsDTO
import okhttp3.RequestBody
import org.json.JSONObject

class HomeRepository(private val remoteDataSource: RemoteDataSourceImp) {

    suspend fun requestLogin(IdToken:String) : LoginDTO {
        return remoteDataSource.requestLogin(IdToken) //굳이 is_select 줄 필요가 없을듯?,,,
    }


    suspend fun requestLogout(access_token: String) {
        return remoteDataSource.requestLogout(access_token)
    }

    suspend fun requestAccessToken(auth_code:String) {
        return remoteDataSource.requestAccessToken(auth_code)

    }
}