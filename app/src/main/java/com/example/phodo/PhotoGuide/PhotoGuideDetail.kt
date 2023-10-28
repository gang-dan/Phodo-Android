package com.example.phodo.PhotoGuide

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.phodo.*
import com.example.phodo.Home.HomeActivity
import com.example.phodo.PhotoMap.MapActivity
import com.example.phodo.data.RemoteDataSourceImp
import com.example.phodo.databinding.ActivityPhotoGuideDetailBinding
import com.example.phodo.dto.PhotoGuidesDTO
import com.squareup.picasso.Picasso

class PhotoGuideDetail : AppCompatActivity() {

    private lateinit var guide_detail_binding: ActivityPhotoGuideDetailBinding
    private val viewModel : PhotoGuideDetailViewModel by viewModels { ViewModelFactory(
        RemoteDataSourceImp(RetrofitInstance)
    ) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        guide_detail_binding = ActivityPhotoGuideDetailBinding.inflate(layoutInflater)
        val view = guide_detail_binding.root
        setContentView(view)

        // 리스트화면에서 선택된 포토가이드 객체 넘겨받음
        val getPhotoIntent: Intent = intent
        val selected_obj_item = getPhotoIntent.getParcelableExtra<PhotoGuidesDTO>("selected_guide_item")

        // 넘겨받은 포토가이드 객체에서 디테일한 정보 요청
        viewModel.getGuideDetail(selected_obj_item!!.photoGuideId, this)

        // 포토가이드 디테일 객체를 관찰 (서버에서 성공적으로 전달받으면 화면에 필요한 데이터 표시)
        viewModel.selectedPhotoItem.observe(this, Observer {
            //guide_detail_binding.imageView.setImageBitmap(it)

            /* 외곽선 적용된 이미지 */
            Picasso.get()
                .load(it.contourImage)
                .into(guide_detail_binding.imageView)

            /* 태그 */
            setTag(it.tagList)

            /* 좋아요 수 */
            /* 위치 객체 설정 */

        })


        // 포토가이드 위치 이동 버튼
        guide_detail_binding.locBtn.setOnClickListener {
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

        // 포토가이드 적용하기 버튼
        guide_detail_binding.applyBtn.setOnClickListener {
            // 적용에 용이하게 (미리) 가공된 데이터를 넘김 (GuideLine 객체)
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("selected_guide_item", viewModel.selectedPhotoItem.value)
            startActivity(intent)
        }

        guide_detail_binding.imageView2.setOnClickListener {
            finishActivity()
        }

    }

    // 화면에 태그를 표시
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
