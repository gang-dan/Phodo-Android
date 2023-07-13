package com.example.phodo.PhotoGuide

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.phodo.*
import com.example.phodo.PhotoMap.MapActivity
import com.example.phodo.Repository.PhotoGuideRepository
import com.example.phodo.data.RemoteDataSourceImp
import com.example.phodo.databinding.ActivityPhotoGuideListBinding
import com.example.phodo.dto.PhotoGuidesDTO

import java.io.Serializable


class PhotoGuideList : AppCompatActivity() {

    private lateinit var guide_list_binding: ActivityPhotoGuideListBinding
    private val viewModel : PhotoGuideListViewModel by viewModels { ViewModelFactory(
        RemoteDataSourceImp(RetrofitInstance)
    ) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        guide_list_binding = ActivityPhotoGuideListBinding.inflate(layoutInflater)
        val view = guide_list_binding.root
        setContentView(view)

        // 액션바 없애고 툴바를 대신해 사용할 수 있도록 설정
        setSupportActionBar(guide_list_binding.toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        // HomeButton을 노출 시킴
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        guide_list_binding.recycler1.layoutManager = GridLayoutManager(this,3)

        /*무한스크롤이나 refresh 구현시 viewmodel.getPhoGuideList 하도록 해야함*/
        viewModel.getPhodoGiudeList()


        viewModel.guideLiveData.observe(this, Observer { //viewmodel에서 만든 변경관찰 가능한todoLiveData를 가져온다.
            setAdapter() //setData함수는 TodoAdapter에서 추가하겠습니다.

        })


        /*

        //getPhodoGiudeList() 호출시 guideLiveData에 변화가 생기면서 어댑터도 동작
        //guideLiveData(포토가이드리스트)가 업데이트되면 리사이클러뷰 어댑터에 알려서 다시 홀드
        viewModel.guideLiveData.observe(this, Observer { //viewmodel에서 만든 변경관찰 가능한todoLiveData를 가져온다.
            (guide_list_binding.recycler1.adapter as PhotoGuideAdapter).setData(it) //setData함수는 TodoAdapter에서 추가하겠습니다.

        })

         */


        // 지도뷰로 이동
        guide_list_binding.floatingActionButton.setOnClickListener {
            //UI 업데이트가 필요없거나 단순 객체 넘기는 구현은 뷰모델 X
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)

        }

    }
    fun setAdapter() {
        // 리스트 아이템 선택
        guide_list_binding.recycler1.adapter = PhotoGuideAdapter(
            viewModel.guideLiveData.value!!,
            onSelectitem = {
                val intent = Intent(this, PhotoGuideDetail::class.java)
                intent.putExtra("selected_guide_item", it)
                startActivity(intent)
            })
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