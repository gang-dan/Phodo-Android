package com.example.phodo

import android.graphics.Bitmap
import android.location.Location
import com.example.phodo.dto.*
import okhttp3.RequestBody
import org.opencv.core.MatOfPoint
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    /*
     * oauth
     */
    //@Headers("content-type: application/json")
    @POST("/api/oauth/login")   //login/oauth2/code/google
    suspend fun requestLogin(
        @Header("Authorization") IdToken: String //("Authorization")
    ) : LoginDTO //유저 정보와 토큰을 받아옴


/*
    @POST("/api/oauth/login")
    suspend fun requestLogin(
        @Body requestBody: RequestBody
    ): LoginDTO

 */


    @POST("/api/oauth/logout")
    suspend fun logout(
        @Header("Authorization") accessToken: String
    ) // 토큰 삭제를 요청


    /*
    * PhotoGuide
    */

    // DB에 저장된 모든 포토가이드 리스트
    @GET("/api/guide/all")
    suspend fun getAllPhotoGuide(
    ): List<PhotoGuidesDTO>

    // 포토가이드 상세 화면 확인시
    @GET("/api/guide/{photoGuideId}")
    suspend fun getDetailPhotoGuide(
        @Path("photoGuideId") photoGuideId: Int
    ): PhotoGuideItemDTO

    // 특정 위치의 포토가이드 리스트
    @GET("")
    suspend fun getLocationPhotoGuide(
        @Query("locationName") locationName: String
    ): List<PhotoGuideItemDTO>

    // 사용자가 만든 포토가이드 리스트
    @GET("")
    suspend fun getMyPhotoGuide(
        @Header("accessToken") accessToken: String,
        @Query("userId") userId: Int
    ): List<PhotoGuideItemDTO>

    // 사용자가 찜한 포토가이드 리스트
    @GET("")
    suspend fun getMyLikePhotoGuide(
        @Header("accessToken") accessToken: String,
        @Query("userId") userId: Int
    ) : List<PhotoGuideItemDTO>


    /*
    * PhotoSpot
    */
    // 위치 정보를 주면 해당 위치 및 주변 포토 스팟 정보들을 전해줌 (location_name에 매칭되는 포토스팟 있는지 확인 + location을 기준으로 scope 범위내 포토 스팟)
    @GET("/api/spots")
    suspend fun getPhotoSpots(
        @Query("latitude") latitude: Double, // 기준 좌표
        @Query("longitude") longitude: Double, // null이면 단순 현재 위치에서 포토스팟 검색 (detail 화면에서 넘어간X)
        @Query("radius") scope: Int,
        @Query("isSelected") is_select : Boolean // true면 포토가이드상세 화면에서 맵화면으로 넘어간 상태이므로 가장 처음 데이터를 해당 location 정보로 세
    ): List<PhotoSpotsDTO>


    @GET("/api/spots/{photoSpotId}")
    suspend fun getPhotoSpotInfo(
        @Path("photoSpotId") photoSpotId: Int,
        @Query("latitude") latitude: Double, // 기준 좌표
        @Query("longitude") longitude: Double,
    ): PhotoSpotItemDTO


    /*
     * PhotoSGuideMaker
     */
    @POST("/api/guide")
    suspend fun requestMakePhotoGuide(
        @Header("accessToken") accessToken: String,
        @Body userId: Int,
        @Body originImage: Bitmap,
        @Body contourImage: Bitmap,
        @Body maskImage: Bitmap,
        @Body contourTransImage: Bitmap,
        @Body tagList: List<String>,
        @Body latitude : Double,
        @Body longitude: Double,
        @Body photoSpotName: String?,
        )

    /*
    @POST("/api/guide")
    suspend fun requestGuide(
        @Body requestImage: Bitmap
    ): PhotoMakerResponseDTO


    @POST("/api/guide/{photoGuideId}")
    suspend fun requestMakeFinalGuide(
        @Path("photoGuideId") photoGuideId: Int,
        @Body guideJsonFile: String,
        @Body tagList: List<String>
    )



     */

    /*
    * 좋아요 누르기
     */






}