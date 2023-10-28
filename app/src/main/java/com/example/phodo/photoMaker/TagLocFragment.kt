package com.example.phodo.photoMaker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.core.view.marginStart
import androidx.fragment.app.Fragment
import com.example.phodo.GuideLine
import com.example.phodo.R
import com.example.phodo.databinding.FragmentTagnlocBinding
import com.example.phodo.utils.PreferenceUtils
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class TagLocFragment(val photoGuideLine : GuideLine) : Fragment() {

    private lateinit var frag_tag_binding : FragmentTagnlocBinding
    var tagList = mutableSetOf<String>()
    var loc : String = ""
    var count = 0
    lateinit var parent : PhotoMaker



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        frag_tag_binding = FragmentTagnlocBinding.inflate(layoutInflater)
        val view = frag_tag_binding.root
        parent = requireActivity() as PhotoMaker

        createTag()

        // 포토가이드 저장하기 버튼
        frag_tag_binding.saveBtn.setOnClickListener {
            parent.photoGuidesSave()
            /* TagLocFragment 라는 이름에 맞게 */
            // 그냥 Parentd의 저장하기 메서드 호출
            //parent.viewModel.requestPhotoGuide(prefs.accessToken, prefs.userId, photoGuideLine, tagList.toList(), parent.ouput_img!!)

            /* 저장하기 클릭시 필요한 작업
            * 1. 최종 외곽선 리스트 생성 -> 그냥 외곽선 적용된 투명 이미지 생성
            * 2. 외곽선 리스트 json 문자열 형태로 변환 -> 그냥 외곽선 적용된 투명 이미지 생성
            * 3. 외곽선 적용된 이미지 생성 (1,2번 -> 대체시 필요 x)
            * 4. Mask 바이너리 이미지 생성
            * 5. 사용자 정보(token, id) 추출해 전송
            * 6. 원본 이미지 전송
            * 7. 위치 (위도, 경도, 이름) -> 현재는 임의 값으로
            */


            /*
            // 컨투어 내부를 채울 마스크를 생성합니다.
            val mask = Mat.zeros(parent.output_mat!!.size(), CvType.CV_8U)

            // 모든 컨투어를 반복하면서 컨투어 내부를 마스킹합니다.
            for (i in result_contourList.indices) {
                Imgproc.drawContours(mask, result_contourList, i, Scalar(255.0), Core.FILLED)
            }

            // 원하는 색상으로 컨투어와 컨투어 내부를 채웁니다.
            val color = Scalar(255.0, 255.0, 255.0, 0.0) // BGR 형식의 색상
            mask.setTo(color, mask)

            val mask_bitamp = Bitmap.createBitmap(
                mask.width(), mask.height(),
                Bitmap.Config.ARGB_8888
            )
            Utils.matToBitmap(mask,mask_bitamp)
            parent.viewModel.maskImg.value = mask_bitamp

            // 전송할 해시태그 리스트 셋팅
            parent.viewModel.tagList.value = tagList

            // 전송할 위치 문자열 셋팅
            parent.viewModel.location.value = loc

            //포토가이드 전송
            // 원본사진, 컨투어 리스트, 마스크, 위치, 태그리스트, 만든사람
            prefs = PreferenceUtil(parent)
            parent.viewModel.requestPhotoGuide(prefs.accessToken, prefs.userId, parent.ouput_img!!)

             */

        }

        return view
    }


    // 사용자의 입력에 따라 화면에 태그 표시
    fun createTag() {
        val frameLayout = FrameLayout(requireContext())
        val layoutParams = FrameLayout.LayoutParams(
            200,
            80
        )
        layoutParams.marginStart = 10
        layoutParams.bottomMargin = 30
        frameLayout.layoutParams = layoutParams
        frameLayout.setBackgroundResource(R.drawable.custom_tag_box)

        val editText = EditText(requireContext())
        val editTextParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        // editTextParams.gravity = Gravity.CENTER_VERTICAL or Gravity.START
        editTextParams.marginStart = 55
        editTextParams.topMargin = 5
        editText.layoutParams = editTextParams

        editText.imeOptions = EditorInfo.IME_ACTION_DONE
        editText.isSingleLine = true
        editText.textSize = 13.toFloat()
        editText.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
        editText.isEnabled = true
        editText.setTypeface(null, Typeface.BOLD)
        editText.setPadding(
            3,3,3,3
        )
        frameLayout.addView(editText)

        val button = Button(requireContext())
        val buttonParams = FrameLayout.LayoutParams(
            50,
            50
        )
        buttonParams.marginStart = 6
        buttonParams.topMargin = Gravity.CENTER_VERTICAL
        buttonParams.gravity = Gravity.START or Gravity.TOP
        button.setBackgroundResource(R.drawable.tag_cancel)
        frameLayout.addView(button, buttonParams)
        button.isVisible = false

        val textView = TextView(requireContext())
        val textLayoutParams = FrameLayout.LayoutParams(
            30,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        textLayoutParams.marginStart = 18
        textLayoutParams.topMargin = Gravity.CENTER_VERTICAL
        textView.text = "#"
        textView.textSize = 15.toFloat()
        textView.layoutParams = textLayoutParams
        frameLayout.addView(textView)


        editText.setOnEditorActionListener { textview, i, keyEvent ->
            var handled = false
            if (i == EditorInfo.IME_ACTION_DONE) { // 입력 완료
                // 키보드 내리기
                val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(textview.windowToken, 0)
                handled = true
                tagList.add(textview.text.toString())
                textView.isVisible = false
                textview.isEnabled = false
                button.isVisible = true

                // 다음 태그 추가
                createTag()
                count ++
            }

            handled
        }

        button.setOnClickListener {
            if (count > 0) { //rameLayout.tag != "first"
                frameLayout.removeAllViews()
                val parentLayout: ViewGroup = frag_tag_binding.tagLayout
                parentLayout.removeView(frameLayout)
            } else {
                it.isVisible = false
                editText.text = null
                textView.isVisible = true
                editText.isEnabled = true
            }
            tagList.remove(editText.text.toString())
            count --
        }
        frag_tag_binding.tagLayout.addView(frameLayout)

    }

}