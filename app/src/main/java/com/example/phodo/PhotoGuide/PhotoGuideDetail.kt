package com.example.phodo.PhotoGuide

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.phodo.HomeActivity
import com.example.phodo.PhotoGuideListViewModel
import com.example.phodo.PhotoMap.MapActivity
import com.example.phodo.R
import com.example.phodo.ViewModelFactory
import com.example.phodo.databinding.ActivityPhotoGuideDetailBinding
import com.example.phodo.dto.PhotoGuideItemDTO
import com.example.phodo.dto.PhotoGuidesDTO

class PhotoGuideDetail : AppCompatActivity() {

    private lateinit var guide_detail_binding: ActivityPhotoGuideDetailBinding
    //private lateinit var viewModel : PhotoGuideDetailViewModel //by viewModels()
    private val viewModel : PhotoGuideDetailViewModel by viewModels { ViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        guide_detail_binding = ActivityPhotoGuideDetailBinding.inflate(layoutInflater)
        val view = guide_detail_binding.root
        setContentView(view)

        //viewModel = ViewModelProvider(this).get(PhotoGuideDetailViewModel::class.java)

        val getPhotoIntent: Intent = intent
        val selected_obj_item = getPhotoIntent.getParcelableExtra<PhotoGuidesDTO>("selected_guide_item")

        viewModel.getGuideDetail(selected_obj_item!!.photoGuideId, this)

        viewModel.resultImg.observe(this, Observer {
            //화면 표시할 것들 (이미자, 컨투어, 좋아요, 태그, 위치 등) -> 변화가 있을 수 있어서 업데이트 필요함
            Log.d("loc","${viewModel.selectedPhotoItem.value!!.width},${viewModel.selectedPhotoItem.value!!.height}")
            guide_detail_binding.imageView.setImageBitmap(it)
            setTag(viewModel.selectedPhotoItem.value!!.tagList)

        })

        guide_detail_binding.button.setOnClickListener {
            //해당 포토가이드의 위치 추출해서 맵화면으로 넘김
            //위치 없을 경우 위치 없다는 Toast 띄우기
            if (viewModel.selectedPhotoItem.value!!.latitude != null && viewModel.selectedPhotoItem.value!!.longitude != null) {
                val reqMapIntent = Intent(this, MapActivity::class.java)
                reqMapIntent.putExtra("latitude",viewModel.selectedPhotoItem.value!!.latitude)
                reqMapIntent.putExtra("longitude",viewModel.selectedPhotoItem.value!!.longitude)
                startActivity(reqMapIntent)

            } else {
                Toast.makeText(this,"설정된 포토스팟이 없습니다.",Toast.LENGTH_SHORT).show()
            }
        }

        guide_detail_binding.button3.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("selected_guide_item",viewModel.selectedPhotoItem.value)
            startActivity(intent)

        }

        guide_detail_binding.imageView2.setOnClickListener {
            finishActivity()
        }

    }

    fun setTag(tagList : List<String>) {
        if (tagList.size != 0) {
            for (i in 0 until tagList.size) {
                val frameLayout = FrameLayout(this)
                val layoutParams = FrameLayout.LayoutParams(
                    200,
                    80
                )
                layoutParams.marginStart = 10
                layoutParams.bottomMargin = 30
                frameLayout.layoutParams = layoutParams
                frameLayout.setBackgroundResource(R.drawable.custom_tag_box)

                val textView = TextView(this)
                val textLayoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    80
                )
                textLayoutParams.marginStart = 18
                textLayoutParams.topMargin = Gravity.CENTER_VERTICAL
                textView.text = "#" + tagList[i]
                textView.textSize = 13.toFloat()
                textView.setTextColor(ContextCompat.getColor(this, R.color.white))
                textView.layoutParams = textLayoutParams
                frameLayout.addView(textView)

                val parentLayout: ViewGroup = guide_detail_binding.tagLayout
                parentLayout.addView(frameLayout)

                /*
                val button = Button(this) // Button 객체를 생성합니다
                //button.id = View.generateViewId() // 고유한 ID를 생성하여 설정합니다
                val layout = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    80
                )
                layout.bottomMargin = 10
                layout.leftMargin = 10
                button.layoutParams = layout // 버튼의 레이아웃 파라미터를 설정합니다
                //button.gravity = Gravity.LEFT // 버튼의 Gravity를 설정합니다
                button.text = "#" + tagList[i]
                button.textSize = 10.toFloat()
                button.setTextColor(ContextCompat.getColor(this, R.color.white))
                button.setBackgroundResource(R.drawable.custom_tag_box)
                //button.setBackgroundColor(ContextCompat.getColor(this, R.color.light_white))

                val parentLayout: ViewGroup = guide_detail_binding.tagLayout
                parentLayout.addView(button)

                 */
            }
        }
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
