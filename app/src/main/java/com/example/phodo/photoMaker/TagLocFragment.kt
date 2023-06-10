package com.example.phodo.photoMaker

import android.content.Context
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
import com.example.phodo.R
import com.example.phodo.databinding.FragmentTagnlocBinding

class TagLocFragment : Fragment() {

    private lateinit var frag_tag_binding : FragmentTagnlocBinding
    var tagList = mutableSetOf<String>()
    var count = 0
    lateinit var parent : PhotoMaker


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        frag_tag_binding = FragmentTagnlocBinding.inflate(layoutInflater)
        val view = frag_tag_binding.root
        parent = requireActivity() as PhotoMaker

        makeTag()

        frag_tag_binding.saveBtn.setOnClickListener {
            //포토가이드 전송
            //val contourJson2String = parent.viewModel.finalContourJson.value.toString()
            parent.viewModel.tagList.value = tagList

            //Log.d("contourJson2String","${contourJson2String.}")

        }

        return view
    }

    fun makeTag() {
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
                makeTag()
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