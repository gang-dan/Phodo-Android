package com.example.phodo.PhotoGuide

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.phodo.R
import kotlinx.coroutines.flow.flowOf
import org.json.JSONObject
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc


class PhotoGuideDetailViewModel(application: Application) : AndroidViewModel(application) {

    val selectedPhotoItem = MutableLiveData<PhotoGuideItem>() //변경/관찰가능한 List<Todo>타입에 LiveData
    //lateinit var photo: LiveData<Int>
    lateinit var bit_img : Bitmap

    init{
        if (!OpenCVLoader.initDebug()) {
            Log.d("test", "OpenCV is not loaded!");
        } else {
            Log.d("test", "OpenCV is loaded successfully!");

        }

    }

    //처음 Detail 화면으로 넘어왔을 때 init 하는 부분
    fun setDetailVideModel(selected_obj_item : PhotoGuideItem, context : Context) {
        this.selectedPhotoItem.value = selected_obj_item

        /*

        val mask_picture: Bitmap =
            BitmapFactory.decodeResource(context.resources, R.drawable.colo_black_contour_img)
        val mask_src = Mat()
        Utils.bitmapToMat(mask_picture, mask_src)
        val hierarchy = Mat()
        val gray = Mat()

        Imgproc.findContours(
            gray,
            contours,
            hierarchy,
            Imgproc.RETR_TREE,
            Imgproc.CHAIN_APPROX_SIMPLE
        )
        Log.d("mask","${contours}")

 */
        /*
        val newWidth = 480
        val newHeight = 640

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeResource(context.resources, R.drawable.sample_colo_ori3, this)
        }

        // 이미지 크기 계산
        val widthRatio = options.outWidth.toFloat() / newWidth.toFloat()
        val heightRatio = options.outHeight.toFloat() / newHeight.toFloat()
        val sampleSize = Math.max(widthRatio, heightRatio).toInt()

        // BitmapFactory.Options 객체 업데이트
        options.apply {
            inJustDecodeBounds = false
            inSampleSize = sampleSize
        }

        // 새로운 크기로 이미지 변환
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.sample_colo_ori3, options)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false)

         */

        val ori_picture: Bitmap =
            BitmapFactory.decodeResource(context.resources, R.drawable.sample_colo_ori3)
        val ori_src = Mat()
        Utils.bitmapToMat(ori_picture, ori_src)

        val resized_img = Mat()
        Imgproc.resize(ori_src, resized_img, Size(1080.0, 1440.0), 0.0, 0.0, Imgproc.INTER_AREA)

        val contoursList = ArrayList<MatOfPoint>()
        val jsonObject = JSONObject(selected_obj_item.jsonData)


        val  w = ori_picture.width.toDouble()
        val  h = ori_picture.height.toDouble()
        Log.d("test","w : ${w}, h : ${h}")

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

      for (contourIdx in contoursList.indices) {
            //Log.d("contourIdx","${contourIdx}")
                Imgproc.drawContours(
                    resized_img,
                    contoursList,
                    contourIdx,
                    Scalar(255.0, 255.0, 255.0),
                    5
                )

        }

        val tempBmp1 = Bitmap.createBitmap(
            resized_img.width(), resized_img.height(),
            Bitmap.Config.ARGB_8888
        )

        Utils.matToBitmap(resized_img, tempBmp1)
        bit_img = tempBmp1



    }

    //이후에 해당 Item 객체의 Id로 변화를 감지
    //객체 자체가 삭제되었을 수 있기 때문에 항상 null 체크 필요



}