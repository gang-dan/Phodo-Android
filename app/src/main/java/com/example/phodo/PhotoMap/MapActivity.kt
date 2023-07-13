package com.example.phodo.PhotoMap

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.phodo.PhotoGuide.PhotoGuideDetailViewModel
import com.example.phodo.R
import com.example.phodo.RetrofitInstance
import com.example.phodo.ViewModelFactory
import com.example.phodo.data.RemoteDataSourceImp
import com.example.phodo.databinding.ActivityMapBinding
import com.example.phodo.dto.PhotoSpotItemDTO
import com.example.phodo.dto.PhotoSpotsDTO
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView


class MapActivity : AppCompatActivity(), MapView.POIItemEventListener,BottomSheetListener {

    private lateinit var map_binding: ActivityMapBinding
    private val viewModel : MapViewModel by viewModels { ViewModelFactory(
        RemoteDataSourceImp(
            RetrofitInstance
        )
    ) }
    lateinit var photoSpot_bottomSheet : BottomSheet
    var isSelect = false

    val permission_list = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    private val ACCESS_FINE_LOCATION = 1000
    private var selected_spot_idx: Int = -1
    private lateinit var selected_marker: MapPOIItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        map_binding = ActivityMapBinding.inflate(layoutInflater)
        val view = map_binding.root
        setContentView(view)

        map_binding.mapView.currentLocationTrackingMode =
            MapView.CurrentLocationTrackingMode.TrackingModeOff

        map_binding.mapView.setPOIItemEventListener(this)

        setSupportActionBar(map_binding.toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //현재 권한 허용 되었는지 확인하고 안 되어 있으면 요청 받음 (허용 거부 하면 사용 X) -> 무조건
        if (checkLocationService()) {
            // GPS가 켜져있을 경우
            requestPermissions(permission_list, 0)

        } else {
            // GPS가 꺼져있을 경우
            Toast.makeText(this, "GPS를 켜주세요.\nGPS를 켜져 있어야 정학한 지도 서비스가 가능합니다. ", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, // requestPermissions() 에서 전달하는 requestCode 값 -> 이 코드에 따라 분기
        permissions: Array<out String>, // 확인할 권한들
        grantResults: IntArray  // 허용 or 거부 값
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for (r1 in grantResults) {
            if (r1 == PackageManager.PERMISSION_DENIED) {

                // 권한 거절 (다시 한 번 물어봄)
                val builder = AlertDialog.Builder(this)
                builder.setMessage("현재 위치를 확인하시려면 위치 권한을 허용해주세요.")
                builder.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        ACCESS_FINE_LOCATION
                    )
                }
                builder.setNegativeButton("취소") { dialog, which ->

                }
                builder.show()
            }

        }

        //map_binding.mapView.setPOIItemEventListener(this)

        val intent: Intent = intent
        if (intent.extras != null) {
            isSelect = true
            // 특정 포토가이드 화면에서 맵으로 이동한 경우
            val photo_latitude = intent.getDoubleExtra("latitude", 0.0)
            val photo_longitude = intent.getDoubleExtra("longitude", 0.0)

            val photoLocation = Location("LocationManager.GPS_PROVIDER")
            photoLocation.latitude = photo_latitude
            photoLocation.longitude = photo_longitude

            moveToLoc(photoLocation)

            //현재 선택된 장소의 상세 정보와 주변 포토스팟 정보를 가져옴
            viewModel.getPhotoSpotList(
                true,
                photo_latitude,
                photo_longitude
            )
        } else { // 리스트화면에서 넘어온 경우
            isSelect = false
            val userLocation = findCurrentUserLoc()
            moveToLoc(userLocation)

            viewModel.getPhotoSpotList(false,userLocation.longitude,userLocation.latitude)
            /*
            for (i in 0 until viewModel.spotsLiveData.value!!.size) {
                drawNormalPhotoSpot(i,viewModel.spotsLiveData.value!![i])
            }

             */

        }

        /*
        map_binding.currentSearchBtn.setOnClickListener {
            viewModel.getPhotoSpotList(this,null)
            for (i in 0 until viewModel.spotLiveData.value!!.size) {
                //drawNormalPhotoSpot(i,viewModel.spotLiveData.value!![i])
            }
        }
         */

        // 선택된 포토스팟
        viewModel.spotInfoLiveData.observe(this, Observer {
            photoSpot_bottomSheet.setSpotInfo(it)

        })

        // 포토스팟 마커
        viewModel.spotsLiveData.observe(this, Observer {
            for (i in 0 until it.size) {
                drawNormalPhotoSpot(i, it[i])
                if (isSelect == true && i == 0) {
                    selected_spot_idx = 0
                    drawSelectPhotoSpot(selected_marker)
                } else {

                }

            }
        })
    }

        fun drawNormalPhotoSpot(spotItemIdx : Int, spotItem: PhotoSpotsDTO) {
        // Drawable 이미지를 가져옵니다.
        val drawable = ContextCompat.getDrawable(this, R.drawable.sample_photo_spot_marker)

        // Drawable을 복제하여 수정 가능한 상태로 만듭니다.
        val mutableDrawable = drawable?.mutate()

        // 텍스트를 동적으로 추가합니다.
        val text = "${spotItem.photoGuideNum}"
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 30f
            textAlign = Paint.Align.CENTER
        }

        // Drawable에 텍스트를 그립니다.
        val bitmap = Bitmap.createBitmap(drawable?.intrinsicWidth ?: 0, drawable?.intrinsicHeight ?: 0, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        mutableDrawable?.setBounds(0, 0, canvas.width, canvas.height)
        mutableDrawable?.draw(canvas)
        canvas.drawText(text, canvas.width / 2f + 20f, canvas.height / 2f, textPaint)

        // 커스텀 마커 설정
        val photoSpot_marker = MapPOIItem()
        photoSpot_marker.itemName = ""
        photoSpot_marker.tag = spotItemIdx //spotItem.photoSpotId
        photoSpot_marker.markerType = MapPOIItem.MarkerType.CustomImage
        photoSpot_marker.mapPoint =
            MapPoint.mapPointWithGeoCoord(spotItem.longitude, spotItem.latitude)
        photoSpot_marker.customImageBitmap = bitmap
        photoSpot_marker.isCustomImageAutoscale = false
        photoSpot_marker.setCustomImageAnchor(0.5f, 1.0f)

        if (spotItemIdx == 0) {
            selected_marker = photoSpot_marker
        }

        // 마커 추가
        //photoSpot_marker.userObject = "${spotItemIdx}"
        map_binding.mapView.addPOIItem(photoSpot_marker)
    }


     fun drawSelectPhotoSpot(marker : MapPOIItem?) {

         val maker_loc = marker!!.mapPoint.mapPointGeoCoord

        if (marker != null) {
            map_binding.mapView.removePOIItem(marker)
        }

        val photoSpot_marker = MapPOIItem()
        photoSpot_marker.itemName = marker.itemName
        photoSpot_marker.tag = selected_spot_idx //사용자 현재 위치 마커는 무조건 0 //포토 스팟 마커는 포토가이드 일련번호로 관리
        photoSpot_marker.mapPoint =
            MapPoint.mapPointWithGeoCoord(maker_loc.longitude,maker_loc.latitude)
        photoSpot_marker.markerType = MapPOIItem.MarkerType.CustomImage
        photoSpot_marker.customImageResourceId = R.drawable.sample_userloc_marker
        photoSpot_marker.isCustomImageAutoscale = false
        photoSpot_marker.setCustomImageAnchor(0.5f, 1.0f)

        selected_marker = photoSpot_marker
         selected_spot_idx = marker.tag

        map_binding.mapView.addPOIItem(photoSpot_marker)


        //네비게이션 올라옴
         /*
        val photoSpot_bottomSheet = BottomSheet()
        photoSpot_bottomSheet.setMyBottomSheetDialogListener(this)
        photoSpot_bottomSheet.show(supportFragmentManager, "")

          */
         photoSpot_bottomSheet = BottomSheet()
         photoSpot_bottomSheet.setMyBottomSheetDialogListener(this)
         photoSpot_bottomSheet.show(supportFragmentManager, "")

         viewModel.getPhotoSpotInfo(viewModel.spotsLiveData.value!![selected_spot_idx].photoSpotId,viewModel.spotsLiveData.value!![selected_spot_idx].latitude,viewModel.spotsLiveData.value!![selected_spot_idx].longitude)

    }


    fun dismissDialog() {
        map_binding.mapView.removePOIItem(selected_marker)
        drawNormalPhotoSpot(selected_spot_idx, viewModel.spotsLiveData.value!![selected_spot_idx])
    }

    override fun onPOIItemSelected(mapView: MapView?, marker: MapPOIItem?) {
        // 마커가 선택될 때 호출됩니다.
        drawSelectPhotoSpot(marker)
    }

    private fun moveToLoc(locaiotn : Location) {
        map_binding.mapView.setMapCenterPoint(
            MapPoint.mapPointWithGeoCoord(
                locaiotn.longitude,
                locaiotn.latitude
            ), true
        )
    }

    //현재 사용자의 위치 찾기
    private fun findCurrentUserLoc() : Location {

        val manager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("permission", "${ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)},${ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)}")
            requestPermissions(permission_list, 0)
        }

        val userLocation = Location("LocationManager.GPS_PROVIDER")

        val location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (location != null) {
            userLocation.longitude = location.latitude
            userLocation.latitude = location.longitude
        }

        return userLocation
    }

    // GPS가 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, marker: MapPOIItem?) {
        // 마커의 말풍선이 선택될 때 호출됩니다.

    }
    override fun onCalloutBalloonOfPOIItemTouched(
        mapView: MapView?,
        marker: MapPOIItem?,
        buttonType: MapPOIItem.CalloutBalloonButtonType?
    ) {
        // 마커의 말풍선의 버튼이 선택될 때 호출됩니다.
    }
    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
        TODO("Not yet implemented")
    }

    override fun onDismiss() {
        // 다이얼로그가 닫히는 시점에 처리할 내용을 여기에 작성합니다.
        dismissDialog()
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






