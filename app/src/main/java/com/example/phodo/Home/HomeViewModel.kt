package com.example.phodo.Home

import android.app.Application
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phodo.Repository.HomeRepository
import com.example.phodo.dto.LoginDTO
import com.example.phodo.dto.PhotoGuideApplyDTO
import com.example.phodo.dto.PhotoGuideItemDTO
import com.example.phodo.utils.PreferenceUtils
import com.squareup.picasso.Picasso
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.net.URL


class HomeViewModel(private val homeRepository: HomeRepository): ViewModel() {

    var photoGuide = MutableLiveData<PhotoGuideItemDTO>()
    var isPhootGuide = MutableLiveData<Boolean>()

    var originalImage = MutableLiveData<Bitmap>()
    var maskImage = MutableLiveData<Bitmap>()

    var resultLandImage = MutableLiveData<Bitmap>()
    var resultPeopleImage = MutableLiveData<Bitmap>()
    var resultContourImage = MutableLiveData<Bitmap>()

    var isLandBtn = MutableLiveData<Boolean>()
    var isPeopleBtn = MutableLiveData<Boolean>()
    var isContourBtn = MutableLiveData<Boolean>()

    var loginInfo = MutableLiveData<LoginDTO>()

    // 서버에 access 토큰 및 사용자 정보 요청
    fun requestLogin(IdToken: String, email: String){
        // api 호출
        viewModelScope.launch {
            // 데이터를 안 주는 경우 Try-catch 처리
            // access token 암호화 기술 사용
            loginInfo.value = homeRepository.requestLogin(IdToken)
        }


    }

    // 현재 저장하고 있는 세셩 등을 다 지움
    fun requestLogout() {
        viewModelScope.launch {
            //homeRepository.requestLogout(HomeActivity.prefs.accessToken)
            //PreferenceUtils.init()!!.deleteAccessToken()
            //HomeActivity.deleteUserInfo()
        }
    }

    // 토큰 유효성 검사
    fun verifyToken() {

    }
}



    //가이드라인이 적용

    /*
    fun setPhotoGuideFilter() {
        // original 이미지 깔기
        // 그 위에 투명 가이드란 사진 깔기
        // 마스크로 조절하기


        //originalImage.value = photoGuide.value!!.photo
        //val oriogin_img_url = URL(photoGuide.value!!.photo)
        //originalImage.value = BitmapFactory.decodeStream(oriogin_img_url.openConnection().getInputStream())

        val mask_img_url = URL(photoGuide.value!!.maskImage)
        maskImage.value = BitmapFactory.decodeStream(mask_img_url.openConnection().getInputStream())
        //maskImage.value = photoGuide.value!!.maskImage

        val ori_img_mat = Mat()
        val mask_img_mat = Mat()
        Utils.bitmapToMat(originalImage.value, ori_img_mat)
        Utils.bitmapToMat(maskImage.value, mask_img_mat)

        // 외곽선 List<MatOfPoint>로 변환
        val contoursList = Json.decodeFromString<ArrayList<MatOfPoint>>(photoGuide.value!!.contourList)

        // 외곽선 투명 이미지 생성
        val contour_img_mat = Mat()
        Imgproc.resize(ori_img_mat, contour_img_mat, Size(originalImage.value!!.width.toDouble(), originalImage.value!!.height.toDouble()), 0.0, 0.0, Imgproc.INTER_AREA)
        // 알파 채널 설정
        val transparentColor = Scalar(0.0, 0.0, 0.0, 0.0)
        contour_img_mat.setTo(transparentColor)

        for (contourIdx in contoursList.indices) {
            Imgproc.drawContours(
                contour_img_mat,
                contoursList,
                contourIdx,
                Scalar(255.0, 255.0, 255.0),
                4
            )
        }
        Utils.matToBitmap(contour_img_mat, resultContourImage.value)

        // 마스크 이미지로 원본 이미지 투명도 조절
        var land_img_mat = Mat.zeros(ori_img_mat.size(), CvType.CV_8UC4)
        var people_img_mat = Mat.zeros(ori_img_mat.size(), CvType.CV_8UC4)

        land_img_mat.setTo(transparentColor)
        people_img_mat.setTo(transparentColor)

        for (i in 0 until ori_img_mat.rows()) {
            for (j in 0 until ori_img_mat.cols()) {
                val mask_pixel = mask_img_mat.get(i, j)
                val ori_pixel = ori_img_mat.get(i, j)

                //ori_pixel[3] = 0.0 // 초기 투명도

                if (mask_pixel[0] > 128.0) { // 하양 -> 사람
                    people_img_mat.put(i, j,*ori_pixel) //mask_pixel
                } else {
                    Log.d("img","${mask_pixel[0]}")
                    land_img_mat.put(i, j,*ori_pixel) //*ori_pixel
                }
            }
        }

        Utils.matToBitmap(land_img_mat, resultLandImage.value)
        Utils.matToBitmap(people_img_mat, resultPeopleImage.value)

    } */



    /*
    fun loadImg(context : Context) {

        var maskBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.sample_colo)
        var originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.sample_colo)

        val handler1 = object  : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                // 핸들러에게 이 작업 수행해주세요 라고 요청할 수 있고 해당 작업은 메인 스레드에서 처리
                when (msg.what) {
                    0 -> {
                        maskImage.value = maskBitmap
                        originalImage.value = originalBitmap

                        setMaskImg()
                    }
                }
            }

        }

        val thread1 = object : Thread() {
            override fun run() {//백그라운드 스레드로 처리하고 프로그레스바로처리
                super.run()
                val maskImg_url = URL(photoGuide.value!!.maskImage)
                // 마스크 이미지 로드
                maskBitmap =
                    BitmapFactory.decodeStream(maskImg_url.openConnection().getInputStream())

                // 원본 이미지 로드
                val oriImg_url = URL(photoGuide.value!!.photo)
                originalBitmap =
                    BitmapFactory.decodeStream(oriImg_url.openConnection().getInputStream())

                val msg = Message()
                msg.what = 0
                handler1.sendMessage(msg) // 넘길 데이터 있을 때
            }

        }
        thread1.start()


    }
     */


    /*
    fun setMaskImg() {

        val orijinalMat = Mat()
        val maskMat = Mat()

        Utils.bitmapToMat(originalImage.value, orijinalMat)
        Utils.bitmapToMat(maskImage.value, maskMat)
        Imgproc.resize(maskMat,maskMat,Size(orijinalMat.width().toDouble(),orijinalMat.height().toDouble()))

        //두 이미지 사이즈 안 맞아서 오류 나는 것 같음. 해결 하람

        var resultLandImage_Mat = Mat.zeros(orijinalMat.size(), CvType.CV_8UC4)
        var resultPeopleImage_Mat = Mat.zeros(orijinalMat.size(), CvType.CV_8UC4)


        // 화면 처리를 위한 핸들러
        val handler2 = object  : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                // 핸들러에게 이 작업 수행해주세요 라고 요청할 수 있고 해당 작업은 메인 스레드에서 처리
                when (msg.what) {
                    0 -> {

                        val resultLandBitmap = Bitmap.createBitmap(
                            resultLandImage_Mat.cols(),
                            resultLandImage_Mat.rows(),
                            Bitmap.Config.ARGB_8888
                        )
                        val resultPeopleBitmap = Bitmap.createBitmap(
                            resultPeopleImage_Mat.cols(),
                            resultPeopleImage_Mat.rows(),
                            Bitmap.Config.ARGB_8888
                        )
                        Utils.matToBitmap(resultLandImage_Mat, resultLandBitmap)
                        Utils.matToBitmap(resultPeopleImage_Mat, resultPeopleBitmap)

                        resultLandImage.value = resultLandBitmap
                        resultPeopleImage.value = resultPeopleBitmap

                        setContourImg()

                    }

                }

            }
        }


        val thread2 = object : Thread() {
            override fun run() {//백그라운드 스레드로 처리하고 프로그레스바로처리
                super.run()
                //Utils.bitmapToMat(originalImage.value, orijinalMat)
                //Utils.bitmapToMat(maskImage.value, maskMat)

                //Imgproc.cvtColor(maskMat, maskMat, Imgproc.COLOR_BGR2GRAY)
                //Imgproc.threshold(maskMat, maskMat, 100.0, 255.0, Imgproc.THRESH_BINARY)

                resultLandImage_Mat = Mat.zeros(orijinalMat.size(), CvType.CV_8UC4)
                resultPeopleImage_Mat = Mat.zeros(orijinalMat.size(), CvType.CV_8UC4)

                val transparentColor = Scalar(0.0, 0.0, 0.0, 0.0)
                resultLandImage_Mat.setTo(transparentColor)
                resultPeopleImage_Mat.setTo(transparentColor)

                for (i in 0 until orijinalMat.rows()) {
                    for (j in 0 until orijinalMat.cols()) {
                        val mask_pixel = maskMat.get(i, j)
                        val ori_pixel = orijinalMat.get(i, j)

                        //ori_pixel[3] = 0.0 // 초기 투명도

                        if (mask_pixel[0] > 128.0) {
                            resultLandImage_Mat.put(i, j,*ori_pixel) //mask_pixel
                        } else {
                            Log.d("img","${mask_pixel[0]}")
                            resultPeopleImage_Mat.put(i, j,*ori_pixel) //*ori_pixel
                        }
                    }
                }
                val msg = Message()
                msg.what = 0
                handler2.sendMessage(msg) // 넘길 데이터 있을 때
            }
        }
        thread2.start()

    }

     */




    /* 투명 이미지에 외곽선만 표시 */
    /*
    fun setContourImg() {
        val ori_src = Mat()
        Utils.bitmapToMat(originalImage.value, ori_src)

        val contoursList = ArrayList<MatOfPoint>()
        val jsonObject = JSONObject(photoGuide.value!!.contourList)

        for (i in 0 until jsonObject.length()) {
            val jsonArray = jsonObject.getJSONArray("${i}")
            val points = arrayOfNulls<Point>(jsonArray.length())

            for(j in 0 until jsonArray.length()){
                val pointObject = jsonArray.getJSONObject(j)
                //Log.d("pointObject","${pointObject}")
                points[j] = Point(pointObject.getDouble("x"), pointObject.getDouble("y"))

            }
            val contour = MatOfPoint(*points)
            contoursList.add(contour)
        }

        //val resultContourImage = Mat()
        //Imgproc.resize(ori_src, resultContourImage, Size(1080.0, 1440.0), 0.0, 0.0, Imgproc.INTER_AREA)

        val resultContourImage = Mat.zeros(ori_src.size(), CvType.CV_8UC4)
        Imgproc.resize(ori_src, resultContourImage, Size(photoGuide.value!!.width.toDouble(), photoGuide.value!!.height.toDouble()), 0.0, 0.0, Imgproc.INTER_AREA)
        // 알파 채널 설정
        // 알파 채널 설정
        val transparentColor = Scalar(0.0, 0.0, 0.0, 0.0)
        resultContourImage.setTo(transparentColor)


        for (contourIdx in contoursList.indices) {
            Imgproc.drawContours(
                resultContourImage,
                contoursList,
                contourIdx,
                Scalar(255.0, 255.0, 255.0),
                5
            )
        }

        val resultContourBitmap = Bitmap.createBitmap(resultContourImage.cols(), resultContourImage.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(resultContourImage, resultContourBitmap)

        this.resultContourImage.value = resultContourBitmap
    }

     */
*/
        */
