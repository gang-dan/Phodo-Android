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
import com.example.phodo.GuideLine
import com.example.phodo.R
import com.example.phodo.databinding.FragmentContourBinding
import kotlinx.coroutines.CoroutineScope
import org.json.JSONObject
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.BufferedReader
import java.io.InputStreamReader


class ContourFragment(val photoGuideLine : GuideLine) : Fragment() {

    private lateinit var frag_contour_binding : FragmentContourBinding
    lateinit var parent : PhotoMaker

    val contourImg = MutableLiveData<Bitmap>()
    var reponseJsonObject = JSONObject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        frag_contour_binding = FragmentContourBinding.inflate(layoutInflater)
        val view = frag_contour_binding.root
        parent = requireActivity() as PhotoMaker

        //컨투어 리스트 타입 변환해서 1차 세팅해야 함
        initcontourIdxMap()

        // 첫 외곽선 체크 박스 셋팅
        setContourBox(requireContext())
        contourImg.value = photoGuideLine.drawGuideLine(photoGuideLine.contourIdxMap, parent.ouput_img!!)

        return view
    }

    fun initcontourIdxMap() {
        for (idx in photoGuideLine.guideLineList.indices){
            photoGuideLine.contourIdxMap[idx] = true
        }
    }


    fun setContourBox(context: Context) {
        var curr_layout = LinearLayout(context)

        var count = 0
        for (contour_idx in photoGuideLine.contourIdxMap.keys) {
            if(count % 3 == 0) { //라운딩 박스 그려주기
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
            check_box.tag = contour_idx

            // 체크 변화시 리스너
            check_box.setOnCheckedChangeListener { compoundButton, b ->
                //val idx = compoundButton.tag as MatOfPoint
                if(photoGuideLine.contourIdxMap[contour_idx] == false) {
                    photoGuideLine.contourIdxMap[contour_idx] = true
                } else {
                    photoGuideLine.contourIdxMap[contour_idx] = false
                }
                // 체크 박스 변화에 따라 외곽선 화면에 다시 그림
                contourImg.value = photoGuideLine.drawGuideLine(photoGuideLine.contourIdxMap, parent.ouput_img!!)
            }
            curr_layout.addView(check_box)

            count++

        }
    }



    /*
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

     */


}