package com.example.phodo.PhotoGuide

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.phodo.R
import com.example.phodo.databinding.GuideRowBinding
import com.example.phodo.dto.PhotoGuideItemDTO
import com.example.phodo.dto.PhotoGuidesDTO
import com.squareup.picasso.Picasso
import retrofit2.http.Url
import java.net.URI
import java.net.URL


class PhotoGuideAdapter(private var photoguideList: List<PhotoGuidesDTO>,val onSelectitem: (guide_item: PhotoGuidesDTO) -> Unit //private var photoguideList: List<PhotoGuidesDTO>,
) :

    RecyclerView.Adapter<PhotoGuideAdapter.PhotoGuideViewHolder>() {

    class PhotoGuideViewHolder(val guide_binding: GuideRowBinding) :
        RecyclerView.ViewHolder(guide_binding.root) { //아이템을 만들때 여러 뷰가있기때문에 itemTodobinding으로 가져온다.

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PhotoGuideViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.guide_row, viewGroup, false) //내가 각아이템에 사용하는 view

        return PhotoGuideViewHolder(GuideRowBinding.bind(view))
    }

    override fun onBindViewHolder(guideViewHolder: PhotoGuideViewHolder, position: Int) {//item을 화면에 표시해주는


        Picasso.get()
            .load(photoguideList[position].photo)
            .into(guideViewHolder.guide_binding.imageView)


        //guideViewHolder.guide_binding.imageView.setImageBitmap(photoguideList[position].photo)


        val item = photoguideList[position]

        guideViewHolder.guide_binding.imageView.setOnClickListener {
            onSelectitem.invoke(item) //눌렀을때 객체를 전달하면서 함수를 실행한다.

        }

    }

    override fun getItemCount() = photoguideList.size

    // 새로 업데이트된 포토가이드객체 리스트를 세팅하고 리사이클러뷰에 표시
    fun setData(newList:List<PhotoGuidesDTO>){
        //photoguideList = newList as ArrayList<PhotoGuidesDTO>
        //notifyDataSetChanged()
    }


}