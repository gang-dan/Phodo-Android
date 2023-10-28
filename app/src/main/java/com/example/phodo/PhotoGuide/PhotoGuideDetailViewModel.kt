package com.example.phodo.PhotoGuide


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phodo.GuideLine
import com.example.phodo.Repository.PhotoGuideRepository
import com.example.phodo.dto.PhotoGuideApplyDTO
import com.example.phodo.dto.PhotoGuideItemDTO
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class PhotoGuideDetailViewModel(private val phoguideRepository: PhotoGuideRepository) : ViewModel() {

    lateinit var context : Context

    val selectedPhotoItem = MutableLiveData<PhotoGuideItemDTO>()
    //lateinit var photoGuide_img_url : URL
    //lateinit var PhotoGuide_img_bitmap : Bitmap
    lateinit var photoGuide_img_mat :Mat

    //var contoursList = ArrayList<MatOfPoint>()
    var resultImg = MutableLiveData<Bitmap>()

    var photoGuideLine : GuideLine? = null


    /* 포토가이드 디테일 정보 api 호출 */
    fun getGuideDetail(photoGuideId : Int, context: Context) {
        this.context = context
        viewModelScope.launch {
            val photoGuides = phoguideRepository.getGuideDetail(photoGuideId)
            selectedPhotoItem.value = photoGuides

            // 가이드라인 객체 만들기 -> 포토가이드 적용시 바로 사용할 수 있도록
            photoGuideLine = GuideLine(selectedPhotoItem.value!!.contourList)

            // 컨투어 이미지 셋팅
            /*
            val photoGuide_img_url = URL(selectedPhotoItem.value!!.maskImage)
            val photoGuide_img_bit = BitmapFactory.decodeStream(photoGuide_img_url.openConnection().getInputStream())
            */

            //setGuideImg()

        }

    }


    /* 포토가이드 디테일에 표시할 외곽선 이미지 가공 */
    /*
    fun setGuideImg(){
        // 전달받은 이미지 Mat으로 변환

        val imgLoad_thread = object : Thread() {
            override fun run() {
                super.run()
                // url 이미지 비트맵으로 로드
                photoGuide_img_url = URL(selectedPhotoItem.value!!.photo)
                PhotoGuide_img_bitmap =
                    BitmapFactory.decodeStream(photoGuide_img_url.openConnection().getInputStream())
            }
        }
        imgLoad_thread.start()
        imgLoad_thread.join()


        // 전달받은 컨투어 리스트 MutableList<MapOfPoint> 로 변환
        //contoursList = Json.decodeFromString(selectedPhotoItem.value!!.contourList)

    }

     */

    /* 포토가이드 적용시 필요한 데이터 가공 */
    /*
    fun applyPhotoGuide() : PhotoGuideApplyDTO {
        val photoGuideFilterItem = PhotoGuideApplyDTO(PhotoGuide_img_bitmap,selectedPhotoItem.value!!.maskImage,selectedPhotoItem.value!!.contourList)
        return photoGuideFilterItem

    }
     */

    //컨투어 리스트를 사용하기 편하게 미리 Mat 형태로 변환해놓음
    fun setCountour() {
        //ori_picture는 Url String 형태임

        // JSON 문자열을 다시 역직렬화하여 데이터로 복원


        /*
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

         */

    }

    /*
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

     */

}