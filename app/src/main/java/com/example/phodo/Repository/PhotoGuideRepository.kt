package com.example.phodo.Repository

import com.example.phodo.dto.PhotoGuideItemDTO
import com.example.phodo.data.RemoteDataSourceImp
import com.example.phodo.dto.PhotoGuidesDTO

class PhotoGuideRepository(private val remoteDataSource: RemoteDataSourceImp) {

    /* 컨투어 json 데이터 읽어오기 -> Repository로 넘어갑니다. */
    //API로 통신하여 데이터를 주고 받는다

    //통신 연결 전에라 여기서 데이터를 받아온척
    suspend fun getPhotoList() : List<PhotoGuidesDTO> {

        return remoteDataSource.getPhotoGuides()

        /*
        var photoGuideList = arrayListOf<PhotoGuideItemDTO>()

        val assetManager = context.resources.assets
        val inputStream = assetManager.open("contour_data.json")
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

        val tag = List() {"뷰맛집"}

        // 포토가이드 아이템 객체 생성 (id, 이미지, 컨투어 데이터 셋팅)
        for (i in 0..10) {
            val location = Location("LocationManager.GPS_PROVIDER")
            location.latitude = 37.525529
            location.longitude = 126.95451

            val guidItem = PhotoGuideItemDTO(i+1,i+1,R.sample_colo,R.drawable.colo_mask_img, 1080.0,1440.0, jsonData,location,"우리집",10)
            photoGuideList.add(guidItem)
        }

         */

    }

    suspend fun getGuideDetail(photoGuideId : Int) : PhotoGuideItemDTO {
        return remoteDataSource.getDetailPhotoGuide(photoGuideId)
    }




}