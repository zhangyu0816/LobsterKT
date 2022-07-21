package com.yimi.rentme.fragment

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragCameraVideoBinding
import com.yimi.rentme.vm.fragment.CameraVideoViewModel
import com.zb.baselibs.activity.BaseFragment
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.SCToastUtil
import com.zb.baselibs.utils.uploadFile
import kotlinx.android.synthetic.main.frag_camera_take.*
import org.simple.eventbus.Subscriber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class CameraVideoFrag(private val isPublish: Boolean) : BaseFragment() {

    private lateinit var cameraProvider: ProcessCameraProvider // 相机信息
    private lateinit var preview: Preview // 预览对象
    private var camera: Camera? = null // 相机对象
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA //当前相机 -- 后置
    private lateinit var imageCamera: ImageCapture // 拍照
    private lateinit var videoCapture: VideoCapture // 录像
    private lateinit var outputDirectory: File

    @AspectRatio.Ratio
    var aspectRatio = AspectRatio.RATIO_16_9

    private val viewModel by getViewModel(CameraVideoViewModel::class.java) {
        binding = mBinding as FragCameraVideoBinding
        activity = this@CameraVideoFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_camera_video
    }

    override fun initView() {
        needEvenBus = true
        viewModel.isPublish = isPublish
        viewModel.initViewModel()
        outputDirectory = getOutputDirectory()
        initCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.mHandler!!.removeCallbacks(viewModel.runnable)
        viewModel.mHandler = null
    }

    /**
     * 改变照片格式
     */
    @Subscriber(tag = "lobsterChangeSize")
    private fun lobsterChangeSize(data: String) {
        aspectRatio =
            if (viewModel.binding.sizeIndex == 0) AspectRatio.RATIO_16_9 else AspectRatio.RATIO_4_3
        initCamera()
    }

    /**
     * 前后摄像头
     */
    @Subscriber(tag = "lobsterChangeCameraId")
    private fun lobsterChangeCameraId(data: String) {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        initCamera()
    }

    /**
     * 开始录像
     */
    @Subscriber(tag = "lobsterCreateRecorder")
    @SuppressLint("MissingPermission", "RestrictedApi")
    private fun lobsterCreateRecorder(data: String) {
        val photoFile =
            createFile(outputDirectory, "yyyy-MM-dd HH.mm.ss", ".mp4")
        val outputOptions =
            VideoCapture.OutputFileOptions.Builder(photoFile).build()
        viewModel.time = 0L
        viewModel.mHandler!!.postDelayed(viewModel.runnable, 100)
        //开始录像
        videoCapture.startRecording(
            outputOptions, Executors.newSingleThreadExecutor(),
            object : VideoCapture.OnVideoSavedCallback {
                override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                    BaseApp.context.uploadFile(savedUri.toFile().absolutePath)
                    viewModel.binding.videoUrl = savedUri.toFile().absolutePath
                    viewModel.binding.isUpload = true
                    viewModel.binding.isRecorder = false
                }

                override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                    Log.e("onError", " 录像失败 $message")
                    SCToastUtil.showToast(
                        this@CameraVideoFrag.activity as AppCompatActivity, " 录像失败 $message", 2
                    )
                }
            })
    }

    /**
     * 关闭录制
     */
    @SuppressLint("RestrictedApi")
    @Subscriber(tag = "lobsterStopRecorder")
    private fun lobsterStopRecorder(data: String) {
        videoCapture.stopRecording()//停止录制
    }

    /**
     * 初始化相机
     */
    @SuppressLint("RestrictedApi")
    fun initCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(BaseApp.context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()//获取相机信息

            //预览配置
            preview = Preview.Builder().build().also {
                it.setSurfaceProvider(view_finder.surfaceProvider)
            }

            imageCamera = ImageCapture.Builder()
                .setTargetAspectRatio(aspectRatio)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            videoCapture = VideoCapture.Builder()//录像用例配置
                .setTargetAspectRatio(aspectRatio) //设置高宽比
                .setTargetRotation(view_finder.display!!.rotation)//设置旋转角度
                .build()
            try {
                cameraProvider.unbindAll()//先解绑所有用例
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCamera, videoCapture
                )//绑定用例
            } catch (exc: Exception) {
            }

        }, ContextCompat.getMainExecutor(BaseApp.context))
    }

    private fun getOutputDirectory(): File {
        val mediaDir = BaseApp.context.externalMediaDirs.firstOrNull()?.let {
            File(it, BaseApp.projectName).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else BaseApp.context.filesDir
    }

    private fun createFile(baseFolder: File, format: String, extension: String) =
        File(
            baseFolder, SimpleDateFormat(format, Locale.US)
                .format(System.currentTimeMillis()) + extension
        )
}