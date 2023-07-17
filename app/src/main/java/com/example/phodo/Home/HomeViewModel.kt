package com.example.phodo.Home

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phodo.R
import com.example.phodo.Repository.HomeRepository
import com.example.phodo.dto.PhotoGuideItemDTO
import kotlinx.coroutines.launch
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

    var isLandBtn =  MutableLiveData<Boolean>()
    var isPeopleBtn =  MutableLiveData<Boolean>()
    var isContourBtn =  MutableLiveData<Boolean>()

    var isLogin = MutableLiveData<Boolean>()

    // 서버에 access 토큰 및 사용자 정보 요청
     fun requestLogin(auth_code : String) {
        // api 호출
        viewModelScope.launch {
            val loginInfo = homeRepository.requestLogin(auth_code)
            HomeActivity.prefs.setAccessToken(loginInfo.accessToken)
            HomeActivity.prefs.setUserInfo(loginInfo.isNewMember,loginInfo.memberId,loginInfo.memberName,"email",loginInfo.profileImage)
            isLogin.value = true
        }

        //토큰 정보 세팅
        //HomeActivity.prefs.setAccessToken("accessToken", accessToken)

        // 사용자 정보 세팅
        //HomeActivity.prefs.setUserProfile()
    }

    fun requestLogout() {
        viewModelScope.launch {
            homeRepository.requestLogout(HomeActivity.prefs.accessToken)
            HomeActivity.prefs.deleteAccessToken()
            isLogin.value = false
        }
    }

    // 현재 가지고 있는 access 토큰이 유효한지 확인
    fun isValidToken() : Boolean {
        return true

    }

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




}