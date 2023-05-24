package com.example.phodo.Repository

import android.content.Context
import android.location.Location
import com.example.phodo.PhotoGuide.PhotoGuideItem
import com.example.phodo.PhotoMap.PhotoSpotItem
import com.example.phodo.R
import java.io.BufferedReader
import java.io.InputStreamReader

class PhotoGuideRepository(context : Context) {

    val context = context

    var imgRes = intArrayOf(
        R.drawable.sample_swiss,
        R.drawable.sample_dessert,
        R.drawable.sample_colo_ori3,
        R.drawable.sample_dan,
        R.drawable.sample_gorill,
        R.drawable.sample_family,
        R.drawable.sample_firenze,
        R.drawable.sample_swiss,
        R.drawable.sample_dessert,
        R.drawable.sample_swiss,
        R.drawable.sample_dessert,
        R.drawable.sample_colo_ori3,
        R.drawable.sample_dan,
        R.drawable.sample_gorill,
        R.drawable.sample_family,
        R.drawable.sample_firenze,
        R.drawable.sample_swiss,
        R.drawable.sample_dessert,
    )

    /* 컨투어 json 데이터 읽어오기 -> Repository로 넘어갑니다. */
    //API로 통신하여 데이터를 주고 받는다

    //통신 연결 전에라 여기서 데이터를 받아온척
    fun getPhotoListData() : List<PhotoGuideItem> {

        var photoGuideList = arrayListOf<PhotoGuideItem>()

        val assetManager = context.resources.assets
        val inputStream = assetManager.open("contour_data2.json")
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
            val location = Location("LocationManager.GPS_PROVIDER")
            location.latitude = 37.525529
            location.longitude = 126.95451

            val guidItem = PhotoGuideItem(i+1,imgRes[i],jsonData,location,"우리집")
            photoGuideList.add(guidItem)
        }

        return photoGuideList
    }

    //현재 맵의 중심점을 기준으로 같은 이름의 스팟 Location과 해당 스팟의 가이드 개수 넘겨
    fun getPhotoSpotListData(current_loc:Location) : List<PhotoSpotItem> {

        //실제로는 위치 정보,갯수,위치 이름, 대표 사진 보내줘야 함 -> 나도 클래스 만들어야 할듯,,,ㅜㅜ
        var photoSpotList = arrayListOf<PhotoSpotItem>()

        val location2 = Location("LocationManager.GPS_PROVIDER")
        location2.latitude = 37.52878
        location2.longitude = 126.96566

        for (i in 0 until 1) {
                val spot = PhotoSpotItem(location2,"아이파크몰",3,R.drawable.sample_swiss)
                photoSpotList.add(spot)

        }

        return photoSpotList
    }

}