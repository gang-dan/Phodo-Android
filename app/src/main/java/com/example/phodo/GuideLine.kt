package com.example.phodo

import android.graphics.Bitmap
import kotlinx.serialization.json.Json
import org.json.JSONObject
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class GuideLine {

    //var img : Bitmap? = null
    var guideLineList = mutableListOf<MatOfPoint>()
    var contourIdxMap = HashMap<Int, Boolean>()
    //var guideMask : Mat? = null

    // 여러 생성자 만들기
    constructor() { }

    // guideLineList, guideMask 설정할 수 있는 생성자
    constructor(guideLineList : String) {
        Json2LineList(guideLineList)

    }

    // 화면에 가이드라인을 표시
    fun drawGuideLine(contourIdxMap : HashMap<Int, Boolean>, img : Bitmap) : Bitmap {
        // 원본 이미지(모델 input시 사이즈 조정된 이미지), 선택된 레이블 정보, 선택된 레이블 정보로 이진화된 이미지, (외곽선 표시된 이미지)
        val draw_img_mat = Mat()
        Utils.bitmapToMat(img, draw_img_mat)

        for (key in contourIdxMap.keys) {
            if (contourIdxMap[key] == true) {
                Imgproc.drawContours(
                    draw_img_mat,
                    guideLineList,
                    key,
                    Scalar(255.0, 255.0, 255.0),
                    5
                )
            }
        }

        val tempBmp1 = Bitmap.createBitmap(
            draw_img_mat.width(), draw_img_mat.height(),
            Bitmap.Config.ARGB_8888
        )
        Utils.matToBitmap(draw_img_mat, tempBmp1)
        val bit_img = tempBmp1

        return bit_img

    }

    fun drawGuideLine(img : Bitmap) : Bitmap {
        val draw_img_mat = Mat()
        Utils.bitmapToMat(img, draw_img_mat)

        for (i in guideLineList.indices) {
            Imgproc.drawContours(
                draw_img_mat,
                guideLineList,
                i,
                Scalar(255.0, 255.0, 255.0),
                5
            )
        }

        val tempBmp1 = Bitmap.createBitmap(
            draw_img_mat.width(), draw_img_mat.height(),
            Bitmap.Config.ARGB_8888
        )
        Utils.matToBitmap(draw_img_mat, tempBmp1)
        val bit_img = tempBmp1

        return bit_img

    }


    // 가이드라인 타입 변환 : MatOfPoint -> Json
    /*
    fun LineList2Json(guideLine_list : MutableList<MatOfPoint>) : Json {

    }

     */

    // 가이드라인 타입 변환 : Json -> MatOfPoint
    fun Json2LineList(guideLine_json : String) {

        val jsonObject = JSONObject(guideLine_json)
        for (i in 0 until jsonObject.length()) {
            val jsonArray = jsonObject.getJSONArray("${i}")
            val points = arrayOfNulls<Point>(jsonArray.length())

            for(j in 0 until jsonArray.length()){
                val pointObject = jsonArray.getJSONObject(j)
                points[j] = Point(pointObject.getDouble("x"), pointObject.getDouble("y"))

            }
            val contour = MatOfPoint(*points)
            guideLineList.add(contour)
        }

    }

    fun createFinalGuideLine() : MutableList<MatOfPoint> {
        val result_contourList = mutableListOf<MatOfPoint>()

        for (key in contourIdxMap.keys) {
            if(contourIdxMap[key] == true) {
                result_contourList.add(guideLineList[key])
            }
        }

        return result_contourList
    }




}
