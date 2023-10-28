package com.example.phodo.Home



import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.example.phodo.databinding.ActivityMainBinding
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.LiteModuleLoader
import org.pytorch.torchvision.TensorImageUtils

import java.io.File
import java.io.FileOutputStream
import java.io.IOException




class MainActivity : AppCompatActivity() {

    init{
        if (!OpenCVLoader.initDebug()) {
            Log.d("test", "OpenCV is not loaded!");
        } else {
            Log.d("test", "OpenCV is loaded successfully!");

        }
    }

    private val FROM_ALBUM = 1 // onActivityResult 식별자
    private var mModule: Module? = null
    var bitmap : Bitmap? = null

    private val CLASSNUM = 21
    private val DOG = 12
    private val PERSON = 15
    private val SHEEP = 17


    private val appBarConfiguration: AppBarConfiguration? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        /*
        if( !Python.isStarted() ) {
            Python.start( AndroidPlatform( this ) )
        }

        val py = Python.getInstance()
        val module = py.getModule( "yolov5" )

        val result = module.callAttr("sumOp")
        //println( "result: ${result?.get("s")}" )
        println( "result : ${ result }" )

         */


        //이미지 업로드 버튼 클릭 이벤트
        binding.button.setOnClickListener(View.OnClickListener { // 이미지 파일 선택 팝업창
            val intent = Intent()
            intent.type = "image/*" // 이미지만
            intent.action = Intent.ACTION_GET_CONTENT // 카메라(ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, FROM_ALBUM)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // 카메라를 다루지 않기 때문에 앨범 상수에 대해서 성공한 경우에 대해서만 처리
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != FROM_ALBUM || resultCode != RESULT_OK) return


        // PyTorch Tensor로 변환
        /*
        val pixels = IntArray(resizedBitmap.width * resizedBitmap.height)
        resizedBitmap.getPixels(pixels, 0, resizedBitmap.width, 0, 0, resizedBitmap.width, resizedBitmap.height)
        val floatPixels = pixels.map { (it shr 16 and 0xFF) / 255.0f } // R 채널만 사용해서 0~1 범위로 정규화

         */

        /*
        val pixels = IntArray(resizedBitmap.width * resizedBitmap.height)
        resizedBitmap.getPixels(pixels, 0, resizedBitmap.width, 0, 0, resizedBitmap.width, resizedBitmap.height)
        val floatPixels = pixels.flatMap {
            val blue = (it and 0xFF).toFloat()
            val green = (it shr 8 and 0xFF).toFloat()
            val red = (it shr 16 and 0xFF).toFloat()
            listOf(red, green, blue)
        }.toFloatArray()

         */


        try {
            val batchNum = 0
            val buf = contentResolver.openInputStream(data!!.data!!)
            bitmap = BitmapFactory.decodeStream(buf)
            buf!!.close()

            //이미지 뷰에 선택한 사진 띄우기
            binding.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER)
            binding.imageView.setImageBitmap(bitmap)

        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            mModule = LiteModuleLoader.load(assetFilePath(getApplicationContext(), "deeplabv3_scripted_optimized.ptl"))
        } catch (e: IOException) {
            Log.e("ImageSegmentation", "Error reading assets", e)
            finish()
        }

        val thread1 = object : Thread() {
            override fun run() {
                super.run()

                val inputTensor: Tensor = TensorImageUtils.bitmapToFloat32Tensor(
                    bitmap,
                    TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                    TensorImageUtils.TORCHVISION_NORM_STD_RGB
                )

                val inputs = inputTensor.dataAsFloatArray
                val outTensors = mModule!!.forward(IValue.from(inputTensor)).toDictStringKey()
                val outputTensor: Tensor = outTensors["out"]!!.toTensor()
                val result = outputTensor.dataAsFloatArray


                val width: Int = bitmap!!.getWidth()
                val height: Int = bitmap!!.getHeight()


                // result 결과 토대로 intValues에 픽셀 저장 (사람만 하양)
                val intValues = IntArray(width * height)
                for (j in 0 until height) {
                    for (k in 0 until width) {
                        var maxi = 0
                        var maxj = 0
                        var maxk = 0
                        var maxnum = -Double.MAX_VALUE
                        for (i in 0 until CLASSNUM) {
                            val score: Float = result[i * (width * height) + j * width + k]
                            //Log.d("score","${score}")
                            if (score > maxnum) {
                                maxnum = score.toDouble()
                                maxi = i
                                maxj = j
                                maxk = k
                                /*
                                if(i > 15){ // 사람 보다 큰거 걸리면 그냥 바로 다음
                                    break
                                }

                                 */
                            }
                        }
                        if (maxi == PERSON) {
                            //Log.d("PERSON","PERSON")
                            intValues[maxj * width + maxk] = 0xFFFFFFFF.toInt()
                        } else {
                            //Log.d("BackGround","BackGround")
                            intValues[maxj * width + maxk] = 0xFF000000.toInt()
                        }
                    }
                }

                val bmpSegmentation = Bitmap.createScaledBitmap(bitmap!!, width, height, true)
                val outputBitmap = bmpSegmentation.copy(bmpSegmentation.config, true)

                // 빈 이미지에 픽셀 표시
                outputBitmap.setPixels(
                    intValues,
                    0,
                    outputBitmap.width,
                    0,
                    0,
                    outputBitmap.width,
                    outputBitmap.height
                )

                // 픽셀이 표시된 이미지를 원래 원본 이미지 크기로 변환
                val transferredBitmap =
                    Bitmap.createScaledBitmap(outputBitmap, bitmap!!.getWidth(), bitmap!!.getHeight(), true)

                // 최종 이미지를 Mat 이미지로 변환
                val mat = Mat(transferredBitmap!!.height, transferredBitmap!!.width, CvType.CV_8UC1)
                //val mat = Mat()
                Utils.bitmapToMat(transferredBitmap, mat)

                // 이진화 수행
                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY) // 컬러 이미지를 그레이스케일로 변환
                Imgproc.threshold(mat, mat, 128.0, 255.0, Imgproc.THRESH_BINARY) // 이진화 수행


                // 2. 컨투어 추출
                val contours = mutableListOf<MatOfPoint>()
                val hierarchy = Mat()
                Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
                Log.d("contours","${contours}")

                val ori_mat = Mat(bitmap!!.height, bitmap!!.width, CvType.CV_8UC1)
                //val mat = Mat()
                Utils.bitmapToMat(bitmap, ori_mat)
                Imgproc.drawContours(ori_mat, contours, -1, Scalar(0.0, 255.0, 0.0), 4)

                val result_bitmap = Bitmap.createBitmap(
                    bitmap!!.width, bitmap!!.height,
                    Bitmap.Config.ARGB_8888
                )
                Utils.matToBitmap(ori_mat, result_bitmap)

                // 메인 스레드에서 처리
                runOnUiThread { // 고차함수로 처리하는 방법
                    binding.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER)
                    binding.imageView.setImageBitmap(result_bitmap)
                }

            }
        }

        thread1.start()


        /*
        //각 모델에 따른 input , output shape 각자 맞게 변환
        val input = Array(1) {
            Array(640) {
                Array(640) {
                    FloatArray(3)
                }
            }
        }
        val output = Array(1) {
            Array(25200) {
                FloatArray(117)
            }
        }


        try {
            val batchNum = 0
            val buf = contentResolver.openInputStream(data!!.data!!)
            val bitmap = BitmapFactory.decodeStream(buf)
            buf!!.close()

            //이미지 뷰에 선택한 사진 띄우기
            binding.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER)
            binding.imageView.setImageBitmap(bitmap)


            // x,y 최댓값 사진 크기에 따라 달라짐 (조절)
            for (x in 0..639) {
                for (y in 0..639) {
                    val pixel = bitmap.getPixel(x, y)
                    input[batchNum][x][y][0] = pixel.toFloat()
                }
            }

            // 모델 돌리기
            val lite: Interpreter? = getTfliteInterpreter("yolov5s-seg-fp16.tflite")
            if (lite != null) {
                lite.run(input, output)
            }
            Log.d("output","output : ${output.javaClass}")
            /*
            for (i in 0..1) {
                if (output[0][0] == 0.0) {
                    binding.textDiagnosis.setText("음성")
                    binding.textDiagnosis.setTextColor(Color.parseColor("#000000"))
                } else if (output[0][0] == 1.0) {
                    binding.textDiagnosis.setText("양성")
                    binding.textDiagnosis.setTextColor(Color.parseColor("#FF4400"))
                }
            }

             */


        } catch (e: IOException) {
            e.printStackTrace()
        }

         */
    }




    @Throws(IOException::class)
    fun assetFilePath(context: Context, assetName: String?): String? {
        val file = File(context.getFilesDir(), assetName)
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath()
        }
        if (assetName != null) {
            context.assets.open(assetName).use { `is` ->
                FileOutputStream(file).use { os ->
                    val buffer = ByteArray(4 * 1024)
                    var read: Int
                    while (`is`.read(buffer).also { read = it } != -1) {
                        os.write(buffer, 0, read)
                    }
                    os.flush()
                }
                return file.getAbsolutePath()
            }
        }
        return ""
    }



    /*
    private fun getTfliteInterpreter(modelPath: String): Interpreter? {
        try {
            return loadModelFile(this@MainActivity, modelPath)?.let { Interpreter(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(IOException::class)
    fun loadModelFile(activity: Activity, modelPath: String?): MappedByteBuffer? {
        val fileDescriptor = activity.assets.openFd(modelPath!!)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

     */


    /*
    fun nonMaxSuppression(
        prediction : Torch,
        confThres: Float = 0.25f,
        iouThres: Float = 0.45f,
        classes: IntArray? = null,
        agnostic: Boolean = false,
        multiLabel: Boolean = false,
        labels: Array<FloatArray> = emptyArray(),
        maxDet: Int = 300,
        nm: Int = 0 // number of masks
    ): List<Tensor> {
        println("prediction.shape : ${prediction.shape()}")

        // Checks
        assert(0f <= confThres && confThres <= 1f) { "Invalid Confidence threshold $confThres, valid values are between 0.0 and 1.0" }
        assert(0f <= iouThres && iouThres <= 1f) { "Invalid IoU $iouThres, valid values are between 0.0 and 1.0" }
        val predictionTensor = if (prediction is List<*> || prediction is Array<*>) prediction[0] as Tensor else prediction

        val device = predictionTensor.device()
        val mps = "mps" in device.type() // Apple MPS
        val prediction = if (mps) predictionTensor.cpu() else predictionTensor
        val bs = prediction.shape()[0] // batch size
        val nc = prediction.shape()[2] - nm - 5 // number of classes
        val xc = prediction.get(DimsOfSlice.indices(bs.toLong()), DimsOfSlice.all(), DimsOfSlice.point(4L)) > confThres // candidates

        // Settings
        // min_wh = 2 // (pixels) minimum box width and height
        val maxWh = 7680 // (pixels) maximum box width and height
        val maxNms = 30000 // maximum number of boxes into torchvision.ops.nms()
        val timeLimit = 0.5 + 0.05 * bs // seconds to quit after
        val redundant = true // require redundant detections
        val multiLabel = multiLabel && nc > 1 // multiple labels per box (adds 0.5ms/img)
        val merge = false // use merge-NMS

        val t = System.currentTimeMillis()
        val mi = 5 + nc // mask start index
        val output = List(bs.toInt()) { Tensor.zeros(floatArrayOf(0f, 6 + nm), prediction.device()) }
        for ((xi, x) in prediction.dataAsFloatArray().withIndex()) { // image index, image inference
            // Apply constraints
            // x[((x[..., 2:4] < min_wh) or (x[..., 2:4] > max_wh)).any(1), 4] = 0 // width-height
            val x = x[xc[xi]] // confidence

            // Cat apriori labels if autolabelling
            if (labels.isNotEmpty() && labels.size > xi) {
                val lb = labels[xi]
                val v = FloatArray(lb.size) { 0f }
                v[0] = lb[0] // x, y, w, h
                v[1] = lb[1]
                v[2] = lb[2]
                v[3] = lb[3]
                v[4] = 1.0f // conf
                v[5 + lb[4].toInt()] = 1.0f // cls
                x += v
            }

            // If none remain process next image
            if (x.isEmpty()) {
                continue
            }

            // Compute conf
            x[DimsOfSlice.all(), DimsOfSlice.indices(5L until mi.toLong())] *= x[DimsOfSlice.all(), DimsOfSlice.point(4L)]

            // Box/Mask
            val box = xywh2xyxy(x[DimsOfSlice.all(), DimsOfSlice.indices(0L until 4L)]) // center_x, center_y, width, height) to (x1, y1, x2, y2)
            val mask = x[DimsOfSlice.all(), DimsOfSlice.indices(mi.toLong() until x.shape()[1])] // zero columns if no masks

            // Detections matrix nx6 (xyxy, conf, cls)
            x = if (multiLabel) {
                val (i, j) = (x[DimsOfSlice.all(), DimsOfSlice.indices(5L until mi.toLong())] > confThres).nonZeroIndices()
                Tensor.cat(box[DimsOfSlice.indices(i)], x[DimsOfSlice.indices(i), DimsOfSlice.indices(5L + j.toLong())].unsqueeze(-1), j.unsqueeze(-1).toType(ScalarType.Float), mask[DimsOfSlice.indices(i)], 1)
            } else { // best class only
                val (conf, j) = max(x[DimsOfSlice.all(), DimsOfSlice.indices(5L until mi.toLong())], 1, keepdim = true)
                Tensor.cat(box, conf, j.toType(ScalarType.Float), mask, 1)[conf.view(-1) > confThres]
            }

            // Filter by class
            if (classes != null) {
                x = x[(x[DimsOfSlice.all(), DimsOfSlice.indices(5L until 6L)] == classes.toTensor()).any(1)]
            }

            // Check shape
            val n = x.shape()[0] // number of boxes
            if (n == 0) { // no boxes
                continue
            }
            x = x[x[DimsOfSlice.all(), DimsOfSlice.indices(4L)].argsort(descending = true).slice(0, maxNms)] // sort by confidence and remove excess boxes

            // Batched NMS
            val c = x[DimsOfSlice.all(), DimsOfSlice.indices(5L until 6L)] * (if (agnostic) 0f else maxWh) // classes
            val boxes = x[DimsOfSlice.all(), DimsOfSlice.indices(0L until 4L)] + c // boxes (offset by class)
            val scores = x[DimsOfSlice.all(), DimsOfSlice.indices(4L)] // scores
            val i = torchvision.ops.nms(boxes, scores, iouThres) // NMS
            val i = i.slice(0, maxDet) // limit detections
            if (merge && 1 < n && n < 3E3) { // Merge NMS (boxes merged using weighted mean)
                // update boxes as boxes(i, 4) = weights(i, n) * boxes(n, 4)
                val iou = boxIoU(boxes[DimsOfSlice.indices(i.toLong())], boxes) > iouThres // iou matrix
                val weights = iou * scores.unsqueeze(-1) // box weights
                x[DimsOfSlice.indices(i.toLong()), DimsOfSlice.indices(0L until 4L)] = torch.mm(weights, x[DimsOfSlice.indices(0L until 4L)]).toType(ScalarType.Float) / weights.sum(1, true) // merged boxes
                if (redundant) {
                    val i = i[iou[DimsOfSlice.all(), DimsOfSlice.indices(i.toLong())].sum(1) > 1] // require redundancy
                }
            }

            output[xi] = x[DimsOfSlice.indices(i.toLong())]
            if (mps) {
                output[xi] = output[xi].to(device)
            }
            if ((System.currentTimeMillis() - t) > timeLimit) {
                println("WARNING ⚠️ NMS time limit $timeLimit exceeded")
                break // time limit exceeded
            }
        }

        return output
    }


    fun xywh2xyxy(x: Tensor): Tensor {
        val y = Tensor(x.shape()).to(x.device())
        y.select(1, 0).copy_(x.select(1, 0) - x.select(1, 2).div(2))
        y.select(1, 1).copy_(x.select(1, 1) - x.select(1, 3).div(2))
        y.select(1, 2).copy_(x.select(1, 0) + x.select(1, 2).div(2))
        y.select(1, 3).copy_(x.select(1, 1) + x.select(1, 3).div(2))
        return y
    }

    fun iou(box1: Tensor, box2: Tensor): Float {
        val b1 = box1.select(0, 0, 2).sub(box1.select(0, 0, 0)).add(1)
        val b2 = box2.select(0, 0, 2).sub(box2.select(0, 0, 0)).add(1)
        val intersection = (b1.min(b2).to(device)).clamp(0.0, Float.MAX_VALUE)
        val area1 = b1.prod()
        val area2 = b2.prod()
        return intersection.prod().div(area1.add(area2).sub(intersection.prod())).toFloat()
    }

     */

}