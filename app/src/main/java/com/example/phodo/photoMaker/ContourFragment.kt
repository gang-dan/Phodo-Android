package com.example.phodo.photoMaker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.phodo.R
import com.example.phodo.databinding.FragmentContourBinding
import org.json.JSONObject
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.BufferedReader
import java.io.InputStreamReader


class ContourFragment() : Fragment() {

    private lateinit var frag_contour_binding : FragmentContourBinding
    val contourImg = MutableLiveData<Bitmap>()
    var reponseJsonObject = JSONObject()
    var contourIdxMap = HashMap<Int, Boolean>()
    lateinit var parent : PhotoMaker

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        frag_contour_binding = FragmentContourBinding.inflate(layoutInflater)
        val view = frag_contour_binding.root
        parent = requireActivity() as PhotoMaker

        //api 호출

        //컨투어 리스트 타입 변환해서 1차 세팅해야 함
        setContourBox(requireContext())
        draw()

        return view
    }

    fun draw() {

        val ori_picture: Bitmap = BitmapFactory.decodeResource(requireContext().resources, R.drawable.sample_trevi)
        val ori_src = Mat()
        Utils.bitmapToMat(ori_picture, ori_src)

        val contoursList = ArrayList<MatOfPoint>()

        val assetManager = requireContext().resources.assets
        val inputStream = assetManager.open("trevi_contour_data.json")
        val isr = InputStreamReader(inputStream,"UTF-8") //스트림에서 문자열을 읽어오는 reader
        val br = BufferedReader(isr)  // 스트림은 그냥 데이터의 흐름이고 버퍼는 그걸 일시적으로 저장하는 곳인가?...

        var str:String? = null
        val sb = StringBuffer()

        do {
            str = br.readLine()
            if (str != null) {
                sb.append("${str}\n")
            }
        } while (str != null)
        br.close() //파일 닫기

        val jsonData : String  = sb.toString()
        reponseJsonObject = JSONObject(jsonData)

        for (i in 0 until reponseJsonObject.length()) {
            val jsonArray = reponseJsonObject.getJSONArray("${i}")
            val points = arrayOfNulls<Point>(jsonArray.length())

            for(j in 0 until jsonArray.length()){
                val pointObject = jsonArray.getJSONObject(j)
                points[j] = Point(pointObject.getDouble("x"), pointObject.getDouble("y"))

            }
            val contour = MatOfPoint(*points)
            contoursList.add(contour)
        }

        val resized_img = Mat()
        Imgproc.resize(ori_src, resized_img, Size(1080.0, 1440.0), 0.0, 0.0, Imgproc.INTER_AREA)

        for (contourIdx in contoursList.indices) {
            if (contourIdxMap.get(contourIdx) == true) {
                Imgproc.drawContours(
                    resized_img,
                    contoursList,
                    contourIdx,
                    Scalar(255.0, 255.0, 255.0),
                    5
                )
            }
        }

        val tempBmp1 = Bitmap.createBitmap(
            resized_img.width(), resized_img.height(),
            Bitmap.Config.ARGB_8888
        )
        Utils.matToBitmap(resized_img, tempBmp1)
        val bit_img = tempBmp1

        contourImg.value = bit_img
        checkContourBox()

    }


    fun setContourBox(context : Context) {
        val num = 9 //reponseJsonObject.length()
        var curr_layout = LinearLayout(context)

        for (i in 0 .. num) {

            if(i % 3 == 0) { //라운딩 박스 그려주기
                val linearLayout = frag_contour_binding.scrollLinearLayout
                val row = LayoutInflater.from(context).inflate(R.layout.contour_box_row, null) as LinearLayout

                val layoutParams =  LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    150
                )
                layoutParams.setMargins(0,0,0,20)
                row.orientation = LinearLayout.HORIZONTAL
                //row.gravity = Gravity.CENTER
                row.layoutParams = layoutParams

                linearLayout.addView(row)
                curr_layout = row
            }

            val check_box = CheckBox(context)
            val drawable = ContextCompat.getDrawable(context, R.drawable.contour_people_png)

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(90,0,0,0)
            check_box.layoutParams = layoutParams

            check_box.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null,null)

            check_box.isChecked = true //모두 체크된 상태로 시작
            check_box.tag = i
            contourIdxMap.put(i,true) //초기화

            check_box.setOnCheckedChangeListener { compoundButton, b ->
                val idx = compoundButton.tag as Int
                contourIdxMap.put(idx, b)
                draw()
            }

            curr_layout.addView(check_box)

        }
    }


    fun checkContourBox() {

        var new_rquestJsonObject = JSONObject()
        var count = 0
        for (i in 0..contourIdxMap.size) {
            if (contourIdxMap.get(i) == true) {
                new_rquestJsonObject.put("${count}",reponseJsonObject["${i}"])
                count++
            }
        }
        parent.viewModel.finalContourJson.value = new_rquestJsonObject

    }


}