package com.example.phodo.PhotoMap

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.phodo.R
import com.example.phodo.databinding.ActivityMapBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView


class MapActivity : AppCompatActivity(), MapView.POIItemEventListener,BottomSheetListener {

    private lateinit var map_binding: ActivityMapBinding
    private val viewModel: MapViewModel by viewModels()

    val permission_list = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    private val ACCESS_FINE_LOCATION = 1000
    private lateinit var selected_spot: MapPOIItem
    private lateinit var spot_markerList: MutableList<MapPOIItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        map_binding = ActivityMapBinding.inflate(layoutInflater)
        val view = map_binding.root
        setContentView(view)

        map_binding.mapView.currentLocationTrackingMode =
            MapView.CurrentLocationTrackingMode.TrackingModeOff

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
            Toast.makeText(this, "GPS를 켜주세요.\nGPS를 켜져 있어야 정학한 지도 서비스가 가능합니다. ", Toast.LENGTH_SHORT)
                .show()
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

        map_binding.mapView.setPOIItemEventListener(this)

        val intent: Intent = intent
        if (intent.extras != null) {
            val photo_latitude = intent.getDoubleExtra("latitude", 0.0)
            val photo_longitude = intent.getDoubleExtra("longitude", 0.0)
            val location_name = intent.getStringExtra("location_name")

            val photoLocation = Location("LocationManager.GPS_PROVIDER")
            photoLocation.latitude = photo_latitude
            photoLocation.longitude = photo_longitude
            moveToSpotLoc(photoLocation, location_name)

        } else {
            moveToUserLoc()

        }

        /*
        map_binding.floatingActionButton.setOnClickListener {
            moveToUserLoc()
        }
         */


    }

    // GPS가 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    //현재 사용자의 위치 표시 및 이동
    private fun moveToUserLoc() {

        //기존 마커 제거
        /*
        if (::user_marker.isInitialized) {
            map_binding.mapView.removePOIItem(user_marker)
        }
         */

        val manager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(
                "permission",
                "${
                    ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                },${
                    ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                }"
            )
            return
        }

        val location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (location != null) {
            val latitude = location.latitude
            val longitude = location.longitude

            val userLocation = Location("LocationManager.GPS_PROVIDER")
            userLocation.latitude = latitude
            userLocation.longitude = longitude

            // 현재 위치로 맵 중심점 이동
            map_binding.mapView.setMapCenterPoint(
                MapPoint.mapPointWithGeoCoord(
                    latitude,
                    longitude
                ), true
            )

            // 현재 맵 중심으로 주변 포토 스팟을 보여줌
            moveToSpotLoc(userLocation,"")

        }

    }

    private fun moveToSpotLoc(photoLocation: Location, location_name: String?) {
        map_binding.mapView.setMapCenterPoint(
            MapPoint.mapPointWithGeoCoord(
                photoLocation.latitude,
                photoLocation.longitude
            ), true
        )

        val photoSpot_marker = MapPOIItem()
        photoSpot_marker.itemName = location_name
        photoSpot_marker.tag = 0 //사용자 현재 위치 마커는 무조건 0 //포토 스팟 마커는 포토가이드 일련번호로 관리
        photoSpot_marker.mapPoint =
            MapPoint.mapPointWithGeoCoord(photoLocation.latitude, photoLocation.longitude)
        photoSpot_marker.markerType = MapPOIItem.MarkerType.CustomImage
        photoSpot_marker.customImageResourceId = R.drawable.sample_userloc_marker
        photoSpot_marker.isCustomImageAutoscale = false
        photoSpot_marker.setCustomImageAnchor(0.5f, 1.0f)

        selected_spot = photoSpot_marker
        photoSpot_marker.userObject = "marker0"
        map_binding.mapView.addPOIItem(photoSpot_marker)

        // 현재 맵 중심으로 주변 포토 스팟을 보여줌
        //showPhotoSpot(photoLocation)

        //네비게이션 올라옴
        val photoSpot_bottomSheet = BottomSheet()
        photoSpot_bottomSheet.setMyBottomSheetDialogListener(this)
        photoSpot_bottomSheet.show(supportFragmentManager, "")

    }

    private fun showPhotoSpot(photoLocation: Location) {
        // 현재 맵의 중심점을 넘겨서 주변 포토스팟 갱신 요청
        viewModel.getPhotoSpotList(this, photoLocation)

        for (i in 0 until viewModel.spotLiveData.value!!.size) {

            // Drawable 이미지를 가져옵니다.
            val drawable = ContextCompat.getDrawable(this, R.drawable.sample_photo_spot_marker)

            // Drawable을 복제하여 수정 가능한 상태로 만듭니다.
            val mutableDrawable = drawable?.mutate()

            // 텍스트를 동적으로 추가합니다.
            val text = "텍스트"
            val textPaint = Paint().apply {
                color = Color.WHITE
                textSize = 30f
                textAlign = Paint.Align.CENTER
            }

            // Drawable에 텍스트를 그립니다.
            val bitmap = Bitmap.createBitmap(drawable?.intrinsicWidth ?: 0, drawable?.intrinsicHeight ?: 0, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            mutableDrawable?.setBounds(0, 0, canvas.width, canvas.height)
            mutableDrawable?.draw(canvas)
            canvas.drawText(text, canvas.width / 2f, canvas.height / 2f, textPaint)

            // 커스텀 마커 설정
            val photoSpot_marker = MapPOIItem()
            photoSpot_marker.itemName = "마커"
            photoSpot_marker.customImageResourceId = R.drawable.sample_photo_spot_marker // 원래의 이미지 리소스 ID
            photoSpot_marker.markerType = MapPOIItem.MarkerType.CustomImage
            photoSpot_marker.mapPoint =
                MapPoint.mapPointWithGeoCoord(viewModel.spotLiveData.value!![i].location.latitude, viewModel.spotLiveData.value!![i].location.longitude)
            photoSpot_marker.customImageBitmap = bitmap
            photoSpot_marker.isCustomImageAutoscale = false
            photoSpot_marker.setCustomImageAnchor(0.5f, 1.0f)

            // 마커 추가
            photoSpot_marker.userObject = "marker1"
            map_binding.mapView.addPOIItem(photoSpot_marker)

            /*
            val photoSpot_marker = MapPOIItem()

            photoSpot_marker.itemName = viewModel.spotLiveData.value!![i].location_name
            photoSpot_marker.tag = 0 //사용자 현재 위치 마커는 무조건 0 //포토 스팟 마커는 포토가이드 일련번호로 관리
            photoSpot_marker.mapPoint =
                MapPoint.mapPointWithGeoCoord(viewModel.spotLiveData.value!![i].location.latitude, viewModel.spotLiveData.value!![i].location.longitude)
            photoSpot_marker.markerType = MapPOIItem.MarkerType.CustomImage // 마커타입을 커스텀 마커로 지정.
            photoSpot_marker.customImageResourceId = R.drawable.sample_photo_spot_marker
            photoSpot_marker.isCustomImageAutoscale = false
            photoSpot_marker.setCustomImageAnchor(0.5f, 1.0f)

            photoSpot_marker.userObject = "marker1"
            map_binding.mapView.addPOIItem(photoSpot_marker)

             */
        }

    }


    fun dismissDialog() {

        val drawable = ContextCompat.getDrawable(this, R.drawable.sample_photo_spot_marker)

        // Drawable을 복제하여 수정 가능한 상태로 만듭니다.
        val mutableDrawable = drawable?.mutate()

        // 텍스트를 동적으로 추가합니다.
        val text = "3"
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 30f
            textAlign = Paint.Align.CENTER
        }

        // Drawable에 텍스트를 그립니다.
        val bitmap = Bitmap.createBitmap(drawable?.intrinsicWidth ?: 0, drawable?.intrinsicHeight ?: 0, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        mutableDrawable?.setBounds(0, 0, canvas.width, canvas.height)
        mutableDrawable?.draw(canvas)
        canvas.drawText(text, canvas.width / 2f + 5, canvas.height / 2f, textPaint)

        // 커스텀 마커 설정
        val photoSpot_marker = MapPOIItem()
        photoSpot_marker.itemName = "마커"
        photoSpot_marker.markerType = MapPOIItem.MarkerType.CustomImage
        //photoSpot_marker.customImageResourceId = R.drawable.sample_photo_spot_marker // 원래의 이미지 리소스 ID
        photoSpot_marker.mapPoint =
            MapPoint.mapPointWithGeoCoord(selected_spot.mapPoint.mapPointGeoCoord.latitude,selected_spot.mapPoint.mapPointGeoCoord.longitude)
        photoSpot_marker.customImageBitmap = bitmap
        photoSpot_marker.isCustomImageAutoscale = false
        photoSpot_marker.setCustomImageAnchor(0.5f, 1.0f)

        // 마커 추가
        photoSpot_marker.userObject = "marker1"

        map_binding.mapView.removePOIItem(selected_spot)

        map_binding.mapView.addPOIItem(photoSpot_marker)
    }

    override fun onPOIItemSelected(mapView: MapView?, marker: MapPOIItem?) {
        // 마커가 선택될 때 호출됩니다.
        val markerTag = marker?.userObject as? String
        if (markerTag == "marker0") { //이미 선택된 아이임
            // 첫 번째 마커를 클릭한 경우
        } else if (markerTag == "marker1") {
            // 두 번째 마커를 클릭한 경우
            map_binding.mapView.removePOIItem(marker)
            marker.customImageResourceId = R.drawable.sample_userloc_marker

            marker.userObject = "marker0"
            selected_spot = marker
            map_binding.mapView.addPOIItem(marker)

            //네비게이션 올라옴
            val photoSpot_bottomSheet = BottomSheet()
            photoSpot_bottomSheet.setMyBottomSheetDialogListener(this)
            photoSpot_bottomSheet.show(supportFragmentManager, "")

        }
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

    override fun onBackPressed() {
        //super.onBackPressed() // 부모 클래스의 메서드를 호출하기 때문에 해당 액티비티 종료됨
        finishActivity()
    }

    fun finishActivity() {
        finish()
        //커스텀 애니메이션 추가하기

    }

}






