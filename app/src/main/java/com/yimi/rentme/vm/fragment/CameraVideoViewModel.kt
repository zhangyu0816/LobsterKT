package com.yimi.rentme.vm.fragment

import android.media.MediaMetadataRetriever
import android.os.Handler
import android.os.SystemClock
import android.view.View
import com.yimi.rentme.MineApp
import com.yimi.rentme.activity.PublishDiscoverActivity
import com.yimi.rentme.bean.SelectImage
import com.yimi.rentme.databinding.FragCameraVideoBinding
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.SCToastUtil
import org.jetbrains.anko.startActivity
import org.simple.eventbus.EventBus

class CameraVideoViewModel : BaseViewModel() {

    lateinit var binding: FragCameraVideoBinding
    var isPublish = false
    var time = 0L
    val runnable = object : Runnable {
        override fun run() {
            time += 100
            val s = (time / 1000).toInt()
            val ms = (time - s * 1000).toInt() / 10
            binding.second = "已录制 $s.${if (ms < 10) "0$ms" else ms.toString()}S"
            mHandler!!.postDelayed(this, 100)
        }
    }
    var mHandler: Handler? = null

    override fun initViewModel() {
        binding.sizeIndex = 0
        binding.lightIndex = 0
        binding.isRecorder = false
        binding.videoUrl = ""
        binding.isUpload = false
        binding.second = ""
        mHandler = Handler()
    }

    override fun back(view: View) {
        super.back(view)
        activity.finish()
    }

    /**
     * 重置
     */
    fun reset(view: View) {
        binding.videoView.stopPlayback() //停止播放视频,并且释放
        binding.videoView.suspend() //在任何状态下释放媒体播放器
        binding.videoUrl = ""
        binding.isUpload = false
    }

    /**
     * 改变视频格式
     */
    fun changeSizeIndex(sizeIndex: Int) {
        binding.sizeIndex = sizeIndex
        EventBus.getDefault().post("改变视频格式", "lobsterChangeSize")
    }

    /**
     * 前后摄像头
     */
    fun changeCameraId(view: View) {
        EventBus.getDefault().post("前后摄像头", "lobsterChangeCameraId")
    }

    /**
     * 录制视频
     */
    fun createRecorder(view: View) {
        binding.isRecorder = true
        binding.second = "已录制 0S"
        EventBus.getDefault().post("开始录像", "lobsterCreateRecorder")
    }

    /**
     * 停止录制
     */
    fun stopRecorder(view: View) {
        if (time < 1000L) {
            SCToastUtil.showToast(activity, "视频文件录制太短，请重新录制", 2)
            return
        }
        mHandler!!.removeCallbacks(runnable)
        EventBus.getDefault().post("停止录像", "lobsterStopRecorder")
    }

    /**
     * 上传视频
     */
    fun upload(view: View) {
        MineApp.selectImageList.clear()
        val media = MediaMetadataRetriever()
        media.setDataSource(binding.videoUrl!!)
        val selectImage = SelectImage()
        selectImage.videoUrl = binding.videoUrl!!
        selectImage.bitmap = media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        selectImage.resTime = time
        MineApp.selectImageList.add(selectImage)
        if (isPublish)
            activity.startActivity<PublishDiscoverActivity>()
        else
            EventBus.getDefault().post(MineApp.selectImageList, "lobsterUploadImageList")
        BaseApp.fixedThreadPool.execute {
            SystemClock.sleep(200L)
            activity.runOnUiThread {
                back(binding.ivBack)
            }
        }
    }
}