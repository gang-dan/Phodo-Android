package com.example.phodo.Home.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.phodo.dto.PhotoGuideItemDTO
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import java.net.URL

class CameraViewModel : ViewModel() {

    /*
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

     */

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

    //인물, 배경 분리 이미지 세팅
    fun setMaskImg() {

        val maskImg_url = URL(photoGuide.value!!.maskImage)
        // 마스크 이미지 로드
        val maskBitmap = BitmapFactory.decodeStream(maskImg_url.openConnection().getInputStream())
        maskImage.value = maskBitmap

        // 원본 이미지 로드
        val oriImg_url = URL(photoGuide.value!!.maskImage)
        val originalBitmap = BitmapFactory.decodeStream(oriImg_url.openConnection().getInputStream())
        originalImage.value = originalBitmap

        val orijinalMat = Mat()
        Utils.bitmapToMat(originalImage.value, orijinalMat)

        val maskMat = Mat()
        Utils.bitmapToMat(maskImage.value, maskMat)

        val resultLandImage_Mat = Mat.zeros(orijinalMat.size(), CvType.CV_8UC4)
        val resultPeopleImage_Mat = Mat.zeros(orijinalMat.size(), CvType.CV_8UC4)

        val transparentColor = Scalar(0.0, 0.0, 0.0, 0.0)
        resultLandImage_Mat.setTo(transparentColor)
        resultPeopleImage_Mat.setTo(transparentColor)


        // 화면 처리를 위한 핸들러
        val handler = object  : Handler(Looper.myLooper()!!) {
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
                    }

                }

            }
        }


        val thread1 = object : Thread() {
            override fun run() {//백그라운드 스레드로 처리하고 프로그레스바로처리
                super.run()
                for (i in 0 until orijinalMat.rows()) {
                    for (j in 0 until orijinalMat.cols()) {
                        val mask_pixel = maskMat.get(i, j)
                        val ori_pixel = orijinalMat.get(i, j)

                        //ori_pixel[3] = 0.0 // 초기 투명도

                        if (mask_pixel[0] == 255.0) {
                            resultLandImage_Mat.put(i, j,*ori_pixel)
                        } else {
                            resultPeopleImage_Mat.put(i, j,*ori_pixel) //*ori_pixel
                        }
                    }
                }
                val msg = Message()
                msg.what = 0
                handler.sendMessage(msg) // 넘길 데이터 있을 때
            }
        }
        thread1.start()

    }


}