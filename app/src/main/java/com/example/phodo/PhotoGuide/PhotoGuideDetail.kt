package com.example.phodo.PhotoGuide

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.phodo.databinding.ActivityPhotoGuideDetailBinding

class PhotoGuideDetail : AppCompatActivity() {

    private lateinit var guide_detail_binding: ActivityPhotoGuideDetailBinding
    private lateinit var viewModel : PhotoGuideDetailViewModel //by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        guide_detail_binding = ActivityPhotoGuideDetailBinding.inflate(layoutInflater)
        val view = guide_detail_binding.root
        setContentView(view)
        Log.d("onCreate","onCreate")
        viewModel = ViewModelProvider(this).get(PhotoGuideDetailViewModel::class.java)

        val intent: Intent = intent
        val selected_obj_item = intent.getSerializableExtra("obj") as PhotoGuideItem

        viewModel.setDetailVideModel(selected_obj_item,this)

        //뷰모델에 정의
        //현재 디테일 라이브데이터를 토대로 현재 ui를 그림

        viewModel.selectedPhotoItem.observe(this, Observer {
            //화면 표시할 것들 (이미자, 컨투어, 좋아요, 태그, 위치 등) -> 변화가 있을 수 있어서 업데이트 필요함
            //guide_detail_binding.imageView.setImageResource(it.photo) //매번 사진이 삭제되었을 수 있음guide_detail_binding.imageView.setImageBitmap(viewModel.bit_img)
            guide_detail_binding.imageView.setImageBitmap(viewModel.bit_img)

        })






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
