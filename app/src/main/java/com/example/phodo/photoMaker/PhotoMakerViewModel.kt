package com.example.phodo.photoMaker

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phodo.GuideLine
import com.example.phodo.Home.HomeActivity
import com.example.phodo.Repository.PhotoMakerRepository
import com.example.phodo.dto.SerializableMatOfPoint
import com.example.phodo.dto.SerializablePoint
import com.example.phodo.utils.PreferenceUtils
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.opencv.core.MatOfPoint
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

/* 사용자가 생성한 포토가이드를 전송하기 전 필요한 전처리 단계 (뷰의 내용은 몰라야 함) */
class PhotoMakerViewModel(private val photomakerRepository: PhotoMakerRepository) : ViewModel() {

    var tagList = MutableLiveData<MutableSet<String>>()


    fun requestPhotoGuide(access_token : String, userId : Int, originImg : Bitmap, contourImg : Bitmap, maskImg : Bitmap, contourTransImg : Bitmap,  tagList : List<String>, latitude : Double, longitude : Double, photoSpotName: String? ) {
        // 사용자 ID, Access Token 추출해 함께 보내기


        viewModelScope.launch {
            photomakerRepository.postPhotoGuide(access_token, userId, originImg, contourImg, maskImg, contourTransImg, tagList, latitude, longitude, photoSpotName)
        }



    }


    fun matOfPointListToJson(matOfPointList: List<MatOfPoint>): String {
        val jsonArray = JSONArray()

        for (matOfPoint in matOfPointList) {
            val pointsArray = JSONArray()

            for (point in matOfPoint.toList()) {
                val pointObject = JSONObject()
                pointObject.put("x", point.x)
                pointObject.put("y", point.y)
                pointsArray.put(pointObject)
            }

            jsonArray.put(pointsArray)
        }

        return jsonArray.toString()
    }

    /*
    fun matOfPointListToJson(matOfPointList: List<MatOfPoint>): String {
        val serializableList = matOfPointList.map { matOfPoint ->
            val points = matOfPoint.toList().map { point ->
                SerializablePoint(point.x, point.y)
            }
            SerializableMatOfPoint(points)
        }
        return Json.encodeToString(serializableList)
    }

     */
}


