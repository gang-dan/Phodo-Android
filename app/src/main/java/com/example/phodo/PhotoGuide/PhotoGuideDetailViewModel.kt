package com.example.phodo.PhotoGuide

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phodo.Repository.PhotoGuideRepository
import com.example.phodo.dto.PhotoGuideItemDTO
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.net.URL


class PhotoGuideDetailViewModel(private val phoguideRepository: PhotoGuideRepository) : ViewModel() {

    init{
        if (!OpenCVLoader.initDebug()) {
            Log.d("test", "OpenCV is not loaded!");
        } else {
            Log.d("test", "OpenCV is loaded successfully!");

        }
    }

    val selectedPhotoItem = MutableLiveData<PhotoGuideItemDTO>() //변경/관찰가능한 List<Todo>타입에 LiveData
    val resultImg = MutableLiveData<Bitmap>()
    var contoursList = ArrayList<MatOfPoint>()
    lateinit var context : Context
    val ori_src = Mat()

    fun getGuideDetail(photoGuideId : Int, context: Context) {
        this.context = context
        viewModelScope.launch {
            val photoGuides = phoguideRepository.getGuideDetail(photoGuideId)
            selectedPhotoItem.value = photoGuides
            setCountour()
        }
    }

    //컨투어 리스트를 사용하기 편하게 미리 Mat 형태로 변환해놓음
    fun setCountour() {
        //ori_picture는 Url String 형태임

        val handler = object  : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                // 핸들러에게 이 작업 수행해주세요 라고 요청할 수 있고 해당 작업은 메인 스레드에서 처리
                when (msg.what) {
                    0 -> {
                        setGuideImg()
                    }
                }
                }

            }

        Log.d("contuor","${selectedPhotoItem.value!!.contourList}")
        val oriImg_url = URL(selectedPhotoItem.value!!.photo)
        val thread1 = object : Thread() {
            override fun run() {//백그라운드 스레드로 처리하고 프로그레스바로처리
                super.run()

                val ori_picture: Bitmap = BitmapFactory.decodeStream(oriImg_url.openConnection().getInputStream())
                Utils.bitmapToMat(ori_picture, ori_src)

                val jsonObject = JSONObject(selectedPhotoItem.value!!.contourList)

                for (i in 0 until jsonObject.length()) {
                    val jsonArray = jsonObject.getJSONArray("${i}")
                    val points = arrayOfNulls<Point>(jsonArray.length())

                    for(j in 0 until jsonArray.length()){
                        val pointObject = jsonArray.getJSONObject(j)
                        points[j] = Point(pointObject.getDouble("x"), pointObject.getDouble("y"))

                    }
                    val contour = MatOfPoint(*points)
                    contoursList.add(contour)
                }

                val msg = Message()
                msg.what = 0
                handler.sendMessage(msg)
            }
        }
        thread1.start()

    }

    fun setGuideImg() {
        val guideImg = Mat()
        Imgproc.resize(ori_src, guideImg, Size(selectedPhotoItem.value!!.width.toDouble(), selectedPhotoItem.value!!.height.toDouble()), 0.0, 0.0, Imgproc.INTER_AREA) //3024.0, 4032.0

        for (contourIdx in contoursList.indices) {
            Imgproc.drawContours(
                guideImg,
                contoursList,
                contourIdx,
                Scalar(255.0, 255.0, 255.0),
                5
            )
        }

        val tempBmp1 = Bitmap.createBitmap(
            guideImg.width(), guideImg.height(),
            Bitmap.Config.ARGB_8888
        )

        Utils.matToBitmap(guideImg, tempBmp1)
        resultImg.value = tempBmp1
    }

}