package com.example.phodo.photoMaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.phodo.RetrofitInstance
import com.example.phodo.ViewModelFactory
import com.example.phodo.data.RemoteDataSourceImp
import com.example.phodo.databinding.ActivityPhotoMakerBinding

class PhotoMaker : FragmentActivity(){

    private lateinit var binding_maker: ActivityPhotoMakerBinding
    val viewModel : PhotoMakerViewModel by viewModels { ViewModelFactory(RemoteDataSourceImp(RetrofitInstance)) }
    //val viewModel : PhotoMakerViewModel by viewModels { ViewModelFactory(applicationContext as Application,RemoteDataSourceImp(RetrofitInstance)) }

    val frag1 = ContourFragment()
    val frag2 = TagLocFragment()

    val fragList = arrayOf(frag1, frag2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding_maker = ActivityPhotoMakerBinding.inflate(layoutInflater)
        val view = binding_maker.root
        setContentView(view)

        setActionBar(binding_maker.toolbar)
        actionBar!!.setHomeButtonEnabled(true)
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.setDisplayShowTitleEnabled(false)


        val intent: Intent = intent
        val img = intent.getStringExtra("photo_obj")
        val imageUri = Uri.parse(img)
        binding_maker.imageview.setImageURI(imageUri)

        //뷰페이저 어댑터 (뷰페이저1과는 다르게 쓸데없는 메서드 없음!)
        val adapter = object  : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return fragList.size
            }

            override fun createFragment(position: Int): Fragment {
                fragList[position]
                return fragList[position]
            }

        }

        frag1.contourImg.observe(this, Observer {
            binding_maker.imageview.setImageBitmap(it)
        })



        binding_maker.container.setOnClickListener {
            // 세그멘테이션 호출
            //viewModel.isMake.value = true
            it.visibility = View.GONE
            binding_maker.pager.adapter = adapter

            //frag1.setContourBox(this)

        }

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