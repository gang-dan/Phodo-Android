package com.example.phodo.Repository

import com.example.phodo.data.RemoteDataSourceImp
import com.example.phodo.dto.LoginDTO
import com.example.phodo.dto.PhotoSpotsDTO

class HomeRepository(private val remoteDataSource: RemoteDataSourceImp) {

    suspend fun requestLogin(auth_code:String) : LoginDTO {
        return remoteDataSource.requestLogin(auth_code) //굳이 is_select 줄 필요가 없을듯?,,,

    }

    suspend fun requestLogout(access_token: String) {
        return remoteDataSource.requestLogout(access_token)
    }
}