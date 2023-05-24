package com.example.phodo.PhotoGuide

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.phodo.HomeActivity
import com.example.phodo.PhotoMap.MapActivity
import com.example.phodo.databinding.ActivityPhotoGuideDetailBinding
import com.example.phodo.ui.home.CameraFragment

class PhotoGuideDetail : AppCompatActivity() {

    private lateinit var guide_detail_binding: ActivityPhotoGuideDetailBinding
    private lateinit var viewModel : PhotoGuideDetailViewModel //by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        guide_detail_binding = ActivityPhotoGuideDetailBinding.inflate(layoutInflater)
        val view = guide_detail_binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(PhotoGuideDetailViewModel::class.java)

        val getPhotoIntent: Intent = intent
        val selected_obj_item = getPhotoIntent.getParcelableExtra<PhotoGuideItem>("selected_guide_item")
        viewModel.setDetailVideModel(selected_obj_item!!,this)


        viewModel.selectedPhotoItem.observe(this, Observer {
            //화면 표시할 것들 (이미자, 컨투어, 좋아요, 태그, 위치 등) -> 변화가 있을 수 있어서 업데이트 필요함
            //guide_detail_binding.imageView.setImageResource(it.photo) //매번 사진이 삭제되었을 수 있음guide_detail_binding.imageView.setImageBitmap(viewModel.bit_img)
            guide_detail_binding.imageView.setImageBitmap(viewModel.bit_img)

        })

        guide_detail_binding.button.setOnClickListener {
            //해당 포토가이드의 위치 추출해서 맵화면으로 넘김
            //위치 없을 경우 위치 없다는 Toast 띄우기
            if (selected_obj_item.location != null) {
                val photo_loc = selected_obj_item.location

                val reqMapIntent = Intent(this, MapActivity::class.java)
                reqMapIntent.putExtra("latitude",photo_loc.latitude)
                reqMapIntent.putExtra("longitude",photo_loc.longitude)
                reqMapIntent.putExtra("location_name",selected_obj_item.location_name)
                startActivity(reqMapIntent)
            } else {
                Toast.makeText(this,"설정된 포토스팟이 없습니다.",Toast.LENGTH_SHORT).show()
            }
        }

        guide_detail_binding.button3.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("selected_guide_item",selected_obj_item)
            startActivity(intent)

        }


        guide_detail_binding.imageView2.setOnClickListener {
            finishActivity()
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
