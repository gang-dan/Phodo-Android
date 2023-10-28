package com.example.phodo.photoMaker

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.phodo.GuideLine
import com.example.phodo.R
import com.example.phodo.RetrofitInstance
import com.example.phodo.ViewModelFactory
import com.example.phodo.data.RemoteDataSourceImp
import com.example.phodo.databinding.ActivityPhotoMakerBinding
import com.example.phodo.utils.PreferenceUtils
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.io.IOException
import java.util.*


class PhotoMaker : FragmentActivity() {

    /*
    companion object {
        lateinit var prefs: PreferenceUtil
    }

     */

    private var mModule: Module? = null

    var origin_img_uri : Uri? = null // 갤러리 이미지 uri
    var ouput_img : Bitmap? = null // output Bitmap 이미지
    //var sementicMask_mat : Mat? = null   // output Mat 이미지

    val photoGuideLine = GuideLine()

    //var contourList = mutableListOf<MatOfPoint>()
    //var contourIdxMap = HashMap<Int, Boolean>() // 컨투어 리스트 -> coontourFragment로 빼기

    // 해시태그 리스트

    // 위치 정보

    lateinit var binding_maker: ActivityPhotoMakerBinding
    val viewModel : PhotoMakerViewModel by viewModels { ViewModelFactory(RemoteDataSourceImp(RetrofitInstance)) }

    val frag1 = ContourFragment(photoGuideLine)
    val frag2 = TagLocFragment(photoGuideLine)
    val fragList = arrayOf(frag1, frag2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding_maker = ActivityPhotoMakerBinding.inflate(layoutInflater)
        val view = binding_maker.root
        setContentView(view)

        //HomeActivity.prefs = PreferenceUtil(applicationContext)

        setActionBar(binding_maker.toolbar)
        actionBar!!.setHomeButtonEnabled(true)
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.setDisplayShowTitleEnabled(false)

        val intent: Intent = intent
        val origin_img = intent.getStringExtra("photo_obj")
        origin_img_uri = Uri.parse(origin_img)
        binding_maker.imageview.setImageURI(origin_img_uri)

        //뷰페이저 어댑터 (뷰페이저1과는 다르게 쓸데없는 메서드 없음!)
        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return fragList.size
            }

            override fun createFragment(position: Int): Fragment {
                fragList[position]
                return fragList[position]
            }
        }

        frag1.contourImg.observe(this, Observer {
            binding_maker.imageview.setImageBitmap(it)
        })

        if(PreferenceUtils.init(this)!!.getAccessToken() != "") {
            val makePhotoguideBtn = findViewById<FrameLayout>(R.id.making_photo_guide_btn)
            makePhotoguideBtn.background = ContextCompat.getDrawable(applicationContext, R.drawable.btn_made_photo_guide_on)

            val makePhotoguideMesg = findViewById<FrameLayout>(R.id.making_photo_guide_message)
            makePhotoguideMesg.background = ContextCompat.getDrawable(applicationContext, R.drawable.ic_photo_guide_message_tooltip_on)
            val r = resources.getDisplayMetrics()
            val pxValue = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 161F, r).toInt()

            makePhotoguideMesg.layoutParams.width = pxValue

            makePhotoguideBtn.setOnClickListener {
                runSegmentation()
                it.visibility = View.GONE
                makePhotoguideMesg.visibility = View.GONE
                binding_maker.pager.adapter = adapter

            }

        } else {
            val makePhotoguideBtn = findViewById<FrameLayout>(R.id.making_photo_guide_btn)
            makePhotoguideBtn.background = ContextCompat.getDrawable(applicationContext, R.drawable.btn_made_photo_guide_off)

            val makePhotoguideMesg = findViewById<FrameLayout>(R.id.making_photo_guide_message)
            makePhotoguideMesg.background = ContextCompat.getDrawable(applicationContext, R.drawable.ic_photo_guide_message_tooltip_off)
            val r = resources.getDisplayMetrics()
            val pxValue = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 283F, r).toInt()
            makePhotoguideMesg.layoutParams.width = pxValue

        }

//        binding_maker.container.setOnClickListener {
//            //CoroutineScope(Dispatchers.Main).launch {
//            // 버튼 클릭시 동작하는 코루틴이므로 CoroutineScope 사용
//            // 함수 return 후 페이저와 관련된 작업이 들어가므로 UI 관련 작업이라 판단하여 Dispatchers.Main 사용
//
//            // 세그멘테이션 시작 // 백그라운드에서 수행해야 할 작업 // suspend 해야 하는지 안 해야 하는지??...
//            //GlobalScope.launch(Dispatchers.Default) {
//            // 백그라운드에서 수행해야 할 작업
//            // 예를 들어, 네트워크 요청, 파일 다운로드, 계산 작업 등
//            //}
//
//        }


        }

    fun photoGuidesSave() {
        // 저장 확정시
        // 최종 컨투어 라인 생성
        val final_contourList = photoGuideLine.createFinalGuideLine()
        //val final_countourJson = matOfPointListToJson(final_contourList) //Json.encodeToString(contourList.value)  // 데이터를 JSON으로 직렬화

        /* 마스크 이미지 생성 */
        val mask = createMask(ouput_img!!, final_contourList)

        /* 외곽선 입힌 이미지 생성 */
        val guideImg = createGuideImg(ouput_img!!, final_contourList)

        /* 외곽선 투명 이미지 생성 */
        val guideTransImg = createTransGuideImg(ouput_img!!, final_contourList)

        binding_maker.imageview.setImageBitmap(mask)


        // 서버 전송 api 호출
        viewModel.requestPhotoGuide(PreferenceUtils.init(this)!!.getAccessToken()!!, PreferenceUtils.init(this)!!.userId, ouput_img!!, guideImg, mask, guideTransImg, frag2.tagList.toList(), 0.0,0.0,"")


    }

    fun createMask(img : Bitmap, contourList : MutableList<MatOfPoint>) : Bitmap {
        //val result_mask_mat = Mat.zeros(img.width, img.height, CvType.CV_8U)
        val contour_img_mat = Mat()
        Utils.bitmapToMat(img, contour_img_mat)
        val blackColor = Scalar(10.0, 10.0, 10.0)
        contour_img_mat.setTo(blackColor)

        // 모든 컨투어를 반복하면서 컨투어 내부를 마스킹합니다.
        for (i in contourList.indices) {
            Imgproc.drawContours(contour_img_mat, contourList, i, Scalar(255.0, 255.0, 255.0),
                5)
        }
        val result_mask_bit = Bitmap.createBitmap(img.width, img.height, Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(contour_img_mat, result_mask_bit)

        return result_mask_bit

    }

    fun createTransGuideImg(img : Bitmap, contourList : MutableList<MatOfPoint>) : Bitmap {
        // 외곽선 투명 이미지 생성
        val contour_img_mat = Mat()
        Utils.bitmapToMat(img, contour_img_mat)
        //val contour_img_mat = Mat.zeros(img.width, img.height, CvType.CV_8U)
        //val contour_img_mat = Mat()
        //Imgproc.resize(contour_img_mat, contour_img_mat, Size(img.width.toDouble(), img.height.toDouble()), 0.0, 0.0, Imgproc.INTER_AREA)
        val transparentColor = Scalar(0.0, 0.0, 0.0, 0.0)
        contour_img_mat.setTo(transparentColor)

        for (contourIdx in contourList.indices) {
            Imgproc.drawContours(
                contour_img_mat,
                contourList,
                contourIdx,
                Scalar(255.0, 255.0, 255.0),
                5
            )
        }

        val result_mask_bit = Bitmap.createBitmap(img.width, img.height, Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(contour_img_mat, result_mask_bit)
        return result_mask_bit

    }

    fun createGuideImg(img : Bitmap, contourList : MutableList<MatOfPoint>) : Bitmap {
        // 외곽선 투명 이미지 생성
        val contour_img_mat = Mat()
        Utils.bitmapToMat(img, contour_img_mat)

        for (contourIdx in contourList.indices) {
            Imgproc.drawContours(
                contour_img_mat,
                contourList,
                contourIdx,
                Scalar(255.0, 255.0, 255.0),
                5
            )
        }

        val result_mask_bit = Bitmap.createBitmap(img.width, img.height, Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(contour_img_mat, result_mask_bit)
        return result_mask_bit

    }


    // Sementic Segmentation 모델 실행
    fun runSegmentation() {

        val CLASSNUM = 21
        val PERSON = 15

        try {
            // uri 이미지를 비트맵 버퍼에
            val buf = contentResolver.openInputStream(origin_img_uri!!)
            ouput_img = BitmapFactory.decodeStream(buf)

            // 이미지 회전 보정
            ouput_img = fixImageOrientation(ouput_img!!, origin_img_uri!!)
            buf!!.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }


        /* Load Model */
        try {
            mModule = LiteModuleLoader.loadModuleFromAsset(assets, "deeplabv3_scripted_optimized.ptl")
        } catch (e: IOException) {
            Log.e("ImageSegmentation", "Error reading assets", e)
            finish()
        }

        /* run model & processing thread */
        val makingOuput_thread = object : Thread() {
            override fun run() {
                super.run()

                // 이미지 원본 크기
                val input_width = ouput_img!!.getWidth()
                var input_height = ouput_img!!.getHeight()

                val maxWidth = 1000
                val maxHeight = 1000

                val aspectRatio = input_width.toFloat() / input_height.toFloat()

                // 이미지가 지정한 maxWidth 및 maxHeight보다 클 경우 크기 조정
                if (input_width > maxWidth || input_height > maxHeight) {
                    if (aspectRatio > 1) {
                        // 이미지의 가로가 더 길 경우
                        val newWidth = maxWidth
                        val newHeight = Math.round(newWidth / aspectRatio)
                         ouput_img =  Bitmap.createScaledBitmap(ouput_img!!, newWidth, newHeight, false)
                    } else {
                        // 이미지의 세로가 더 길 경우
                        val newHeight = maxHeight
                        val newWidth = Math.round(newHeight * aspectRatio)
                        ouput_img = Bitmap.createScaledBitmap(ouput_img!!, newWidth, newHeight, false)
                    }
                }


                val inputTensor: Tensor = TensorImageUtils.bitmapToFloat32Tensor(
                    ouput_img,
                    TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                    TensorImageUtils.TORCHVISION_NORM_STD_RGB
                )

                //val inputs = inputTensor.dataAsFloatArray
                val outTensors = mModule!!.forward(IValue.from(inputTensor)).toDictStringKey()
                val outputTensor: Tensor = outTensors["out"]!!.toTensor()
                val result = outputTensor.dataAsFloatArray

                 val output_width = ouput_img!!.getWidth()
                 val ouput_height = ouput_img!!.getHeight()

                // processing (사람에 대한 픽셀만 남김)
                val intValues = IntArray(output_width * ouput_height)
                for (j in 0 until ouput_height) {
                    for (k in 0 until output_width) {
                        var maxi = 0
                        var maxj = 0
                        var maxk = 0
                        var maxnum = -Double.MAX_VALUE
                        for (i in 0 until CLASSNUM) {
                            val score: Float = result[i * (output_width * ouput_height) + j * output_width + k]
                            if (score > maxnum) {
                                maxnum = score.toDouble()
                                maxi = i
                                maxj = j
                                maxk = k
                            }
                        }
                        if (maxi == PERSON) {
                            intValues[maxj * output_width + maxk] = 0xFFFFFFFF.toInt()
                        } else {
                            intValues[maxj * output_width + maxk] = 0xFF000000.toInt()
                        }
                    }
                }

                //val bmpSegmentation = Bitmap.createScaledBitmap(ouput_img!!, output_width, ouput_height, true)

                // sementic 결과로 mask 이미지 만들기
                var sementicMask_bit = ouput_img!!.copy(ouput_img!!.config, true)
                sementicMask_bit.setPixels(
                    intValues,
                    0,
                    sementicMask_bit.width,
                    0,
                    0,
                    sementicMask_bit.width,
                    sementicMask_bit.height
                )


                // mask 이미지 크기를 원래 원본 이미지 크기로 변환
                sementicMask_bit = Bitmap.createScaledBitmap(sementicMask_bit, ouput_img!!.getWidth(), ouput_img!!.getHeight() ,true)

                // 최종 output bitmap 이미지를 Mat 이미지로 변환
                var sementicMask_mat = Mat(sementicMask_bit.height, sementicMask_bit.width, CvType.CV_8UC1) // CV_32S
                Utils.bitmapToMat(sementicMask_bit, sementicMask_mat)


                /* 추가 영역분할 위해 mask 연산 이미지 생성 */
                val new_sementicMask_mat = Mat(sementicMask_bit.height, sementicMask_bit.width, CvType.CV_8UC1) // 또 다른 Mat 이미지
                Utils.bitmapToMat(sementicMask_bit, new_sementicMask_mat)

                // 초기 모델의 결과로 컨투어 추출
                Imgproc.cvtColor(new_sementicMask_mat, new_sementicMask_mat, Imgproc.COLOR_BGR2GRAY)
                Imgproc.threshold(new_sementicMask_mat, new_sementicMask_mat, 110.0, 255.0, Imgproc.THRESH_BINARY) // 이진화 수행

                // 컨투어 추출
                extractContour(new_sementicMask_mat)


                //마스크 연산
                val new_origin_img_mat = Mat()
                Utils.bitmapToMat(ouput_img, new_origin_img_mat)

                // 사람만 원래 이미지, 배경은 검정
                val combinedImg_mat = Mat()
                new_origin_img_mat.copyTo(combinedImg_mat, sementicMask_mat)
                Core.subtract(Mat.ones(new_origin_img_mat.size(), new_origin_img_mat.type()), sementicMask_mat, sementicMask_mat)
                combinedImg_mat.setTo(Scalar(0.0, 0.0, 0.0), sementicMask_mat)


               // 그레이스케일 변환
               val combinedImg_mat_gray = Mat()
               Imgproc.cvtColor(combinedImg_mat, combinedImg_mat_gray, Imgproc.COLOR_BGR2GRAY)

               // 이진화
               val combinedImg_mat_thresh = Mat()
               Imgproc.threshold(combinedImg_mat_gray, combinedImg_mat_thresh, 0.0, 255.0, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU)

               // 모폴로지 연산을 통한 노이즈 및 홀 제거
               val kernel = Mat.ones(3, 3, CvType.CV_8U)
               val opening = Mat()
               Imgproc.morphologyEx(combinedImg_mat_thresh, opening, Imgproc.MORPH_OPEN, kernel, Point(1.0, 1.0), 3) //3

               // 확실한 배경 (Sure Background) 생성
               val sureBg = Mat()
               Imgproc.dilate(opening, sureBg, kernel, Point(), 30) //50 -> 30(gooD)

               // 거리 변환 (Distance Transform) 적용
               val distTransform = Mat()
               Imgproc.distanceTransform(opening, distTransform, Imgproc.DIST_L2, 3)
               val maxDist = Core.minMaxLoc(distTransform).maxVal
               val sureFg = Mat()
               Core.compare(distTransform, Scalar(0.05 * maxDist), sureFg, Core.CMP_GT) //0.1 -> 0.05 (0.03)

               // 확실한 전경 (Sure Foreground) 생성
               sureFg.convertTo(sureFg, CvType.CV_8U)

               // Unknown 영역 생성
               val unknown = Mat()
               Core.subtract(sureBg, sureFg, unknown)

               // 전경 객체 라벨링
               val markers = Mat()
               Imgproc.connectedComponents(sureFg, markers)
               Core.add(markers, Scalar(1.0), markers)

               // Unknown 영역을 0으로 설정
               markers.setTo(Scalar(0.0), unknown)

               Imgproc.cvtColor(combinedImg_mat, combinedImg_mat, Imgproc.COLOR_RGB2BGR)
               markers.convertTo(markers, CvType.CV_32SC1)

                // Watershed 알고리즘 적용
                Imgproc.watershed(combinedImg_mat, markers)

                // markers 매트릭스에서 영역 레이블 추출
                val labelImage = Mat()
                markers.convertTo(labelImage, CvType.CV_8U)

                // 각 영역에 대한 컨투어 추출
                for (label in 0 until Core.minMaxLoc(markers).maxVal.toInt() + 1) { // 레이블 0 및 1은 배경 및 경계입니다.
                    // 배경에 대한거 무조건 하나 만들기
                    val instanceMask_mit = Mat.zeros(labelImage.size(), CvType.CV_8U)
                    Core.compare(labelImage, Scalar(label.toDouble()), instanceMask_mit, Core.CMP_EQ)

                    extractContour(instanceMask_mit)

                }

                // 컨투어 선택을 위한 맵 초기화
                /*
                for (idx in photoGuideLine.guideLineList.indices){
                    contourIdxMap[idx] = true
                }

                 */


            }
        }

        makingOuput_thread.start()
        makingOuput_thread.join()

        //objectLabling()

    }

    // 컨투어 추출
    fun extractContour(mask : Mat) {

        val contours = mutableListOf<MatOfPoint>()
        Imgproc.findContours(mask, contours, Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        if (contours.isNotEmpty()) {
            // 여기에서 각 영역에 대한 컨투어 리스트(contours)를 사용할 수 있습니다.
            //contourList.addAll(contours)
            photoGuideLine.guideLineList.addAll(contours)
        }
    }

    // 레이블에 해당하는 이진 마스크 생성 함수
    fun createMask(labeledImage: Mat, label: Int): Mat {
        val mask = Mat.zeros(labeledImage.size(), CvType.CV_8U)
        for (row in 0 until labeledImage.rows()) {
            for (col in 0 until labeledImage.cols()) {
                val value = labeledImage.get(row, col)[0].toInt()
                if (value == label) {
                    mask.put(row, col, 255.0)
                }
            }
        }
        return mask
    }



    fun fixImageOrientation(bitmap:Bitmap, origin_img_uri: Uri): Bitmap {
        try {
            val buf = contentResolver.openInputStream(origin_img_uri)
            val exif = ExifInterface(buf!!)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            val rotationDegrees = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }

            // 이미지를 회전
            val matrix = android.graphics.Matrix()
            if (rotationDegrees != 0) {
                matrix.postRotate(rotationDegrees.toFloat())
            }

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: IOException) {
            // Exif 메타데이터를 읽는 중에 오류가 발생할 수 있으므로 예외 처리 필요
            e.printStackTrace()
            return bitmap // 오류 발생 시 원본 이미지 반환
        }
    }

    fun addToMatOfPointList(doubleList: HashMap<Int, ArrayList<MatOfPoint>>, key: Int, vararg points: org.opencv.core.Point) {
        val matOfPoint = MatOfPoint(*points)

        //matOfPoint.fromArray(*points)

        if (doubleList.containsKey(key)) {
            doubleList[key]?.add(matOfPoint)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            // id가 이미 이걸로 설정되어 있음 (옵션메뉴에 사용자가 구성한 아이템 외에 안드에서 제공하는 홈버튼이 하나 더 추가되는 것으로 이해하면 됨)
            android.R.id.home -> {
                finishActivity()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        //super.onBackPressed() // 부모 클래스의 메서드를 호출하기 때문에 해당 액티비티 종료됨
        finishActivity()
    }

    fun finishActivity() {
        finish()

        //커스텀 애니메이션 추가하기

    }

}
