package com.example.phodo

import android.location.Location
import com.example.phodo.PhotoGuide.PhotoGuideItem
import com.example.phodo.PhotoMap.PhotoSpotItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    /*
     * oauth
     */

    /*
    @POST("")
    suspend fun requestLogin(
        @Header("email") email: String,
        @Query("password") password: String
    ) :  //유저 정보와 토큰을 받아옴

     */

    @POST("")
    fun logout() // 토큰 삭제를 요청? //suspend

    @GET("")
    fun isValidToken(
        @Header("accessToken") accessToken: String
    ) : Boolean

    /*
    * PhotoGuide
    */

    // 사용자가 만든 포토가이드 리스트
    @GET("")
    fun getMyPhotoGuide(
        @Header("accessToken") accessToken: String,
        @Query("userId") userId: Int
    ): Call<List<PhotoGuideItem>>

    // 사용자가 찜한 포토가이드 리스트
    @GET("")
    fun getMyLikePhotoGuide(
        @Header("accessToken") accessToken: String,
        @Query("userId") userId: Int
    ): List<PhotoGuideItem>


    // DB에 저장된 모든 포토가이드 리스트
    @GET("")
    fun getAllPhotoGuide(
    ): List<PhotoGuideItem>

    // 특정 위치의 포토가이드 리스트
    @GET("")
    fun getLocationPhotoGuide(
        @Query("locationName") locationName: String
    ): List<PhotoGuideItem>

    // 포토가이드 상세 화면 확인시
    @GET("")
    fun getDetailPhotoGuide(
        @Query("photoGuideId") photoGuideId: Int
    ): PhotoGuideItem


    /*
    * PhotoSpot
    */

    // 위치 정보를 주면 해당 위치 및 주변 포토 스팟 정보들을 전해줌 (location_name에 매칭되는 포토스팟 있는지 확인 + location을 기준으로 scope 범위내 포토 스팟)
    @GET("")
    fun getPhotoSpotInfo(
        @Query("location") location: Location,
        @Query("location_name") location_name: String?, //null이면 그냥 아무장소에서나 검색하기 누른것임
        @Query("scope") scope: Int

    ): List<PhotoSpotItem>


}