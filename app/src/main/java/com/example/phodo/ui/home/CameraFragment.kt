package com.example.phodo.ui.home

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.phodo.HomeActivity
import com.example.phodo.HomeViewModel
import com.example.phodo.PhotoGuide.PhotoGuideList
import com.example.phodo.databinding.FragmentHomeBinding
import java.io.*

//import com.sun.tools.javac.resources.ct


class CameraFragment : Fragment() {

    private lateinit var frag_home_binding : FragmentHomeBinding
    private val viewmodel  by activityViewModels<HomeViewModel> ()

    lateinit var homeActivity: HomeActivity
    private var cameraDevice : CameraDevice? = null
    lateinit var imageReader : ImageReader
    var cameraId : String? = null
    lateinit var imageDimension : Array<Size>
    var texture : SurfaceTexture? = null
    lateinit var surface : Surface
    lateinit var cameraCaptureSessions : CameraCaptureSession
    lateinit var captureRequestBuilder :CaptureRequest.Builder
    var fileCount = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        frag_home_binding = FragmentHomeBinding.inflate(layoutInflater)
        val view = frag_home_binding.root

        frag_home_binding.textureView.surfaceTextureListener = textureListener

        // 필터 적용시 조절 버튼
        viewmodel.isPhootGuide.observe(viewLifecycleOwner){
            if (it == true) {
                frag_home_binding.filterImage.setImageResource(viewmodel.photoGuide.value!!.photo)
                frag_home_binding.seekContainer.isVisible = true
                frag_home_binding.filterBtnContainer.isVisible = true
                frag_home_binding.filterDeleteBtn.isVisible = true
            }else {
                frag_home_binding.filterImage.setImageResource(0)
                frag_home_binding.seekContainer.isVisible = false
                frag_home_binding.filterBtnContainer.isVisible = false
                frag_home_binding.filterDeleteBtn.isVisible = false
            }
        }


        // 사진촬영 버튼
        frag_home_binding.button.setOnClickListener {
            takePicture()
        }

        // 갤러리 버튼
        frag_home_binding.circleGallery.setOnClickListener {

        }

        // 포토가이드 리스트 화면 이동
        frag_home_binding.imageView.setOnClickListener {
            val intent = Intent(context, PhotoGuideList::class.java)
            startActivity(intent)
        }

        // 포토가이드 적용 삭제 버튼
        frag_home_binding.filterDeleteBtn.setOnClickListener {
            viewmodel.isPhootGuide.value = false
        }

        val homeViewModel = ViewModelProvider(this).get(CameraViewModel::class.java)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        closeCamera()

    }

    private var textureListener: TextureView.SurfaceTextureListener = object : TextureView.SurfaceTextureListener {

        override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
            //카메라 사용 가능
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {
        }

        override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
            return false
        }

        override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
        }

    }

    // openCamera() 메서드는 TextureListener 에서 SurfaceTexture 가 사용 가능하다고 판단했을 시 실행된다
    private fun openCamera() {
        Log.e("camera", "openCamera() : openCamera()메서드가 호출되었음")

        // 카메라의 정보를 가져와서 cameraId 와 imageDimension 에 값을 할당하고, 카메라를 열어야 하기 때문에
        // CameraManager 객체를 가져온다
        homeActivity = context as HomeActivity
        val manager = homeActivity.getSystemService(Context.CAMERA_SERVICE) as CameraManager


        try {
            // CameraManager 에서 cameraIdList 의 값을 가져온다
            // FaceCamera 값이 true 이면 후면(0), 아니면 전면(1) 카메라
            cameraId = if (true) {
                manager.cameraIdList[0]
            }else {
                manager.cameraIdList[1]
            }

            val characteristics = manager.getCameraCharacteristics(cameraId!!)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

            // SurfaceTexture 에 사용할 Size 값을 map 에서 가져와 imageDimension 에 할당해준다
            imageDimension = map!!.getOutputSizes<SurfaceTexture>(SurfaceTexture::class.java)

            val desiredAspectRatio = Rational(9, 16) // 원하는 비율을 정의합니다. 예: 16:9

            var bestSize: Size? = null
            var bestAspectRatio: Rational? = null

            for (size in imageDimension) {
                val aspectRatio = Rational(size.width, size.height)
                if (aspectRatio == desiredAspectRatio) {
                    // 원하는 비율이 지원되는 경우
                    bestSize = size
                    bestAspectRatio = aspectRatio
                    break
                } else if (bestSize == null || aspectRatio.denominator * bestAspectRatio!!.numerator >
                    bestAspectRatio.denominator * aspectRatio.numerator
                ) {
                    // 지원되는 비율 중에서 애플리케이션에 가장 적합한 비율 선택
                    bestSize = size
                    bestAspectRatio = aspectRatio
                }
            }

            /*
            // 프리뷰 크기가 선택되었을 경우에만 설정
            if (bestSize != null) {
                val layoutParams = frag_home_binding.textureView.layoutParams
                layoutParams.width = bestSize.width
                layoutParams.height = bestSize.height
                frag_home_binding.textureView.layoutParams = layoutParams
            }

             */


            // 카메라를 열기전에 카메라 권한, 쓰기 권한이 있는지 확인한다
            if(ActivityCompat.checkSelfPermission(homeActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED // 카메라 권한없음
                && ActivityCompat.checkSelfPermission(homeActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { // 쓰기권한 없음
                // 카메라 권한이 없는 경우 권한을 요청한다
                ActivityCompat.requestPermissions(homeActivity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 100) //REQUEST_CAMERA_PERMISSION
                //return
            }

            // 이때, stateCallback 은 카메라를 실행할때 호출되는 콜백메서드이며, cameraDevice 에 값을 할달해주고, 카메라 미리보기를 생성한다
            manager.openCamera(cameraId!!, stateCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    // openCamera() 메서드에서 CameraManager.openCamera() 를 실행할때 인자로 넘겨주어야하는 콜백메서드
    // 카메라가 제대로 열렸으면, cameraDevice 에 값을 할당해주고, 카메라 미리보기를 생성한다
    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            Log.d("camera", "stateCallback : onOpened")

            // MainActivity 의 cameraDevice 에 값을 할당해주고, 카메라 미리보기를 시작한다
            // 나중에 cameraDevice 리소스를 해지할때 해당 cameraDevice 객체의 참조가 필요하므로,
            // 인자로 들어온 camera 값을 전역변수 cameraDevice 에 넣어 준다
            cameraDevice = camera

            // createCameraPreview() 메서드로 카메라 미리보기를 생성해준다
            createCameraPreviewSession()
        }

        override fun onDisconnected(camera: CameraDevice) {
            Log.d("camera", "stateCallback : onDisconnected")

            // 연결이 해제되면 cameraDevice 를 닫아준다
            cameraDevice!!.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            Log.d("camera", "stateCallback : onError")

            // 에러가 뜨면, cameraDevice 를 닫고, 전역변수 cameraDevice 에 null 값을 할당해 준다
            cameraDevice!!.close()
            cameraDevice = null
        }

    }
    // openCamera() 에 넘겨주는 stateCallback 에서 카메라가 제대로 연결되었으면
    // createCameraPreviewSession() 메서드를 호출해서 카메라 미리보기를 만들어준다
    private fun createCameraPreviewSession() {
        try {

            // 캡쳐세션을 만들기 전에 프리뷰를 위한 Surface 를 준비한다
            // 레이아웃에 선언된 textureView 로부터 surfaceTexture 를 얻을 수 있다
            texture = frag_home_binding.textureView.surfaceTexture

            val desiredAspectRatio = Rational(3, 4) // 원하는 비율을 정의합니다. 예: 16:9

            var bestSize: Size? = null
            var bestAspectRatio: Rational? = null

            for (size in imageDimension) {
                val aspectRatio = Rational(size.width, size.height)
                if (aspectRatio == desiredAspectRatio) {
                    // 원하는 비율이 지원되는 경우
                    bestSize = size
                    bestAspectRatio = aspectRatio
                    break
                } else if (bestSize == null || aspectRatio.denominator * bestAspectRatio!!.numerator >
                    bestAspectRatio.denominator * aspectRatio.numerator
                ) {
                    // 지원되는 비율 중에서 애플리케이션에 가장 적합한 비율 선택
                    bestSize = size
                    bestAspectRatio = aspectRatio
                }
            }

            var width = imageDimension!![0].width
            var height = imageDimension!![0].height - 10

            if (bestSize != null) {
                width = bestSize.height
                height = bestSize.width - 10
            }


            // 미리보기를 위한 Surface 기본 버퍼의 크기는 카메라 미리보기크기로 구성
            texture!!.setDefaultBufferSize(width, height)

            // 미리보기를 시작하기 위해 필요한 출력표면인 surface
            surface = Surface(texture)

            // 미리보기 화면을 요청하는 RequestBuilder 를 만들어준다.
            // 이 요청은 위에서 만든 surface 를 타겟으로 한다
            captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)

            // 위에서 만든 surface 에 미리보기를 보여주기 위해 createCaptureSession() 메서드를 시작한다
            // createCaptureSession 의 콜백메서드를 통해 onConfigured 상태가 확인되면
            // CameraCaptureSession 을 통해 미리보기를 보여주기 시작한다
            cameraDevice!!.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.d("camera", "Configuration change")
                }

                override fun onConfigured(session: CameraCaptureSession) {
                    if(cameraDevice == null) {
                        // 카메라가 이미 닫혀있는경우, 열려있지 않은 경우
                        return
                    }
                    // session 이 준비가 완료되면, 미리보기를 화면에 뿌려주기 시작한다
                    cameraCaptureSessions = session
                    captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)

                    try {
                        cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, null)
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                    }
                }

            }, null)

        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    // 사진찍을 때 호출하는 메서드
    private fun takePicture() {

        try {
            val manager = homeActivity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val characteristics = manager.getCameraCharacteristics(cameraDevice!!.id)
            var jpegSizes: Array<Size>? = null
            jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!.getOutputSizes(ImageFormat.JPEG)

            var width = jpegSizes[0].width
            var height = jpegSizes[0].height

            imageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1)

            val outputSurface = ArrayList<Surface>(2)
            outputSurface.add(imageReader!!.surface)
            outputSurface.add(Surface(frag_home_binding.textureView!!.surfaceTexture))

            val captureBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder.addTarget(imageReader!!.surface)

            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)

            // 사진의 rotation 을 설정해준다
            val rotation = homeActivity.windowManager.defaultDisplay.rotation
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION,getJpegOrientation(characteristics,rotation)) //ORIENTATIONS.get(rotation)

            val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) //DIRECTORY_DCIM
            var file = File(storageDir.toString() + "/pic${fileCount}.jpg")
            Log.d("camera","${storageDir.toString()}")
            val readerListener = object : ImageReader.OnImageAvailableListener {
                override fun onImageAvailable(reader: ImageReader?) {
                    var image : Image? = null

                    try {
                        image = imageReader!!.acquireLatestImage()

                        val buffer = image!!.planes[0].buffer
                        val bytes = ByteArray(buffer.capacity())
                        buffer.get(bytes)

                        val flippedBytes = ByteArray(bytes.size)
                        Log.d("camera","${bytes.size}")

                        /*
                        val width = image.width
                        val height = image.height
                        for (row in 0 .. height) {
                            for (col in 0 .. width) {
                                val flippedRow = height - row - 1 //
                                val flippedCol = width - col - 1
                                val srcOffset = (flippedRow * width + col) * 3 // 이미지의 픽셀 크기에 따라 조정할 수 있음
                                val destOffset = (row * width + col) * 3 // 이미지의 픽셀 크기에 따라 조정할 수 있음
                                System.arraycopy(bytes, srcOffset, flippedBytes, destOffset, 3)
                            }
                        }

                         */


                        var output: OutputStream? = null
                        try {
                            output = FileOutputStream(file)
                            output.write(bytes)
                        } finally {
                            output?.close()

                            var uri = Uri.fromFile(file)
                            Log.d("camera", "uri 제대로 잘 바뀌었는지 확인 ${uri}")

                            // 프리뷰 이미지에 set 해줄 비트맵을 만들어준다
                            var bitmap: Bitmap = BitmapFactory.decodeFile(file.path)

                            // 비트맵 사진이 90도 돌아가있는 문제를 해결하기 위해 rotate 해준다
                            var rotateMatrix = Matrix()
                            rotateMatrix.postRotate(90F)
                            var rotatedBitmap: Bitmap = Bitmap.createBitmap(bitmap, 0,0, bitmap.width, bitmap.height, rotateMatrix, false)

                            MediaScannerConnection.scanFile(context, arrayOf(file.path), null, null)
                            Log.d("camera","사진 촬영 성공!!")


                            // 90도 돌아간 비트맵을 이미지뷰에 set 해준다
                            //img_previewImage.setImageBitmap(rotatedBitmap)

                            // 리사이클러뷰 갤러리로 보내줄 uriList 에 찍은 사진의 uri 를 넣어준다
                            //uriList.add(0, uri.toString())

                            fileCount++ //사용자 갤러리의 마지막 인덱스 가져옴
                        }

                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        image?.close()
                    }
                }

            }

            // imageReader 객체에 위에서 만든 readerListener 를 달아서, 이미지가 사용가능하면 사진을 저장한다
            imageReader!!.setOnImageAvailableListener(readerListener, null)

            val captureListener = object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
                    super.onCaptureCompleted(session, request, result)
                    /*Toast.makeText(this@MainActivity, "Saved:$file", Toast.LENGTH_SHORT).show()*/
                    Toast.makeText(context, "사진이 촬영되었습니다", Toast.LENGTH_SHORT).show()
                    createCameraPreviewSession()
                }
            }

            // outputSurface 에 위에서 만든 captureListener 를 달아, 캡쳐(사진 찍기) 해주고 나서 카메라 미리보기 세션을 재시작한다
            cameraDevice!!.createCaptureSession(outputSurface, object : CameraCaptureSession.StateCallback() {
                override fun onConfigureFailed(session: CameraCaptureSession) {}

                override fun onConfigured(session: CameraCaptureSession) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, null)
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                    }
                }

            }, null)


        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun getJpegOrientation(cameraCharacteristics: CameraCharacteristics, deviceOrientation: Int): Int {
        val sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
        val (isFrontFacing, degrees) = when (deviceOrientation) {
            Surface.ROTATION_0 -> false to 0
            Surface.ROTATION_90 -> false to 90
            Surface.ROTATION_180 -> true to 180
            Surface.ROTATION_270 -> true to 270
            else -> false to 0
        }

        var jpegOrientation = (sensorOrientation!! + degrees + 360) % 360
        if (isFrontFacing) {
            jpegOrientation = (360 - jpegOrientation) % 360
        }

        return jpegOrientation
    }



    // 카메라 객체를 시스템에 반환하는 메서드
    // 카메라는 싱글톤 객체이므로 사용이 끝나면 무조건 시스템에 반환해줘야한다
    // 그래야 다른 앱이 카메라를 사용할 수 있다
    private fun closeCamera() {
        if (null != cameraDevice) {
            cameraDevice!!.close()
            cameraDevice = null
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, // requestPermissions() 에서 전달하는 requestCode 값 -> 이 코드에 따라 분기
        permissions: Array<out String>, // 확인할 권한들
        grantResults: IntArray  // 허용 or 거부 값
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        /*
        for (r1 in grantResults) {
            if (r1 == PackageManager.PERMISSION_DENIED) {
                return
                }

            }

         */
        for (r1 in grantResults) {
            if (r1 == PackageManager.PERMISSION_DENIED) {

                // 권한 거절 (다시 한 번 물어봄)
                val builder = AlertDialog.Builder(homeActivity)
                builder.setMessage("카메라를 사용하시려면 카메라 사용 권한을 허용해주세요.")
                builder.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(homeActivity, arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE),100)
                }
                builder.setNegativeButton("취소") { dialog, which ->

                }
                builder.show()
            }

        }

        }

    private fun adjustTransparency(bitmap: Bitmap, alpha: Int): Bitmap? {
        val transparentBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val width = transparentBitmap.width
        val height = transparentBitmap.height
        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = transparentBitmap.getPixel(x, y)
                val modifiedColor =
                    Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
                transparentBitmap.setPixel(x, y, modifiedColor)
            }
        }
        return transparentBitmap
    }


}


