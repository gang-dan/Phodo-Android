package com.example.phodo.PhotoGuide

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.phodo.*
import com.example.phodo.databinding.ActivityPhotoGuideListBinding
import com.example.phodo.databinding.GuideRowBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class PhotoGuideList : AppCompatActivity() {

    private lateinit var guide_list_binding: ActivityPhotoGuideListBinding
    private lateinit var rowBinding: GuideRowBinding
    private var photoGuideList = arrayListOf<PhotoGuideItem>()
    private val viewModel: PhotoGuideListViewModel by viewModels()

    var imgRes = intArrayOf(
        R.drawable.sample_swiss,
        R.drawable.sample_dessert,
        R.drawable.sample_colo,
        R.drawable.sample_dan,
        R.drawable.sample_gorill,
        R.drawable.sample_family,
        R.drawable.sample_firenze,
        R.drawable.sample_swiss,
        R.drawable.sample_dessert,
        R.drawable.sample_swiss,
        R.drawable.sample_dessert,
        R.drawable.sample_colo,
        R.drawable.sample_dan,
        R.drawable.sample_gorill,
        R.drawable.sample_family,
        R.drawable.sample_firenze,
        R.drawable.sample_swiss,
        R.drawable.sample_dessert,
    )

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

        /* 컨투어 json 데이터 읽어오기 -> Repository로 넘어갑니다. */
        val inputStream = assets.open("contour_data.json")
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

        // 포토가이드 아이템 객체 생성 (id, 이미지, 컨투어 데이터 셋팅)
        for (i in 0..10) {
            val guidItem = PhotoGuideItem(i,imgRes[i],jsonData)
            photoGuideList.add(guidItem)
        }

        viewModel.setListVideModel(photoGuideList)

        // 아래로 당겨 새로고침


        // 리스트 아이템 선택
        guide_list_binding.recycler1.adapter = PhotoGuideAdapter(photoGuideList,
            onSelectitem = {
                //viewModel.moveToDetailActivity(this,it)
                val intent = Intent(this, PhotoGuideDetail::class.java)
                intent.putExtra("selected_guide_item", it) // parcel 클래스 대신 Serializable 클래스로 객체 전달
                startActivity(intent)
            })

        //guideLiveData(포토가이드리스트)가 업데이트되면 리사이클러뷰에 알림
        /*
        viewModel.todoLiveData.observe(this, Observer { //viewmodel에서 만든 변경관찰 가능한todoLiveData를 가져온다.
            (binding.recyclerView.adapter as TodoAdapter).setData(it) //setData함수는 TodoAdapter에서 추가하겠습니다.

        })
         */

        // 지도뷰로 이동
        guide_list_binding.floatingActionButton.setOnClickListener {
            //UI 업데이트가 필요없거나 단순 객체 넘기는 구현은 뷰모델 X


        }

    }
/*

        //툴바 옵션은 필터링 기능


    }

*/


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