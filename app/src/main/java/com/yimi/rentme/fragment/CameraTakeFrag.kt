package com.yimi.rentme.fragment

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragCameraTakeBinding
import com.yimi.rentme.vm.fragment.CameraTakeViewModel
import com.zb.baselibs.activity.BaseFragment
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.SCToastUtil
import com.zb.baselibs.utils.uploadFile
import kotlinx.android.synthetic.main.frag_camera_take.*
import org.simple.eventbus.Subscriber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraTakeFrag(private val isMore: Boolean, private val isPublish: Boolean) : BaseFragment() {

    private lateinit var cameraProvider: ProcessCameraProvider // 相机信息
    private lateinit var preview: Preview // 预览对象
    private var camera: Camera? = null // 相机对象
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA //当前相机 -- 后置
    private lateinit var imageCamera: ImageCapture // 拍照
    private lateinit var videoCapture: VideoCapture // 录像
    private var cameraControl: CameraControl? = null
    private val lensFacing = CameraSelector.LENS_FACING_BACK
    private lateinit var outputDirectory: File

    @AspectRatio.Ratio
    var aspectRatio = AspectRatio.RATIO_16_9

    private val viewModel by getViewModel(CameraTakeViewModel::class.java) {
        binding = mBinding as FragCameraTakeBinding
        activity = this@CameraTakeFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_camera_take
    }

    override fun initView() {
        needEvenBus = true
        viewModel.isMore = isMore
        viewModel.isPublish = isPublish
        viewModel.initViewModel()
        outputDirectory = getOutputDirectory()
        initCamera()
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
     * 打开闪光灯
     */
    @Subscriber(tag = "lobsterChangeLight")
    private fun lobsterChangeLight(data: String) {
        if (camera != null) {
            if (cameraControl == null)
                cameraControl = camera!!.cameraControl

            if (viewModel.binding.lightIndex == 0)
                cameraControl!!.enableTorch(false)
            else
                cameraControl!!.enableTorch(true)
        }

    }

    /**
     * 拍照
     */
    @Subscriber(tag = "lobsterTakePhoto")
    private fun lobsterTakePhoto(data: String) {
        val photoFile =
            createFile(outputDirectory, "yyyy-MM-dd HH.mm.ss", ".jpg")
        val metadata = ImageCapture.Metadata().apply {
            isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
        }
        val outputOptions =
            ImageCapture.OutputFileOptions.Builder(photoFile).setMetadata(metadata).build()
        imageCamera.takePicture(outputOptions, ContextCompat.getMainExecutor(BaseApp.context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    SCToastUtil.showToast(
                        this@CameraTakeFrag.activity as AppCompatActivity,
                        " 拍照失败 ${exc.message}",
                        2
                    )
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                    BaseApp.context.uploadFile(savedUri.toFile().absolutePath)
                    viewModel.binding.imageUrl = savedUri.toFile().absolutePath
                    viewModel.binding.isUpload = true
                }
            })
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