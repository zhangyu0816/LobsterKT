package com.yimi.rentme.vm

import android.view.View
import com.yimi.rentme.activity.LoginActivity
import com.yimi.rentme.activity.PublishDiscoverActivity
import com.yimi.rentme.databinding.AcVideoPlayBinding
import com.yimi.rentme.utils.DebuggerUtils
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.RomUtils
import org.jetbrains.anko.startActivity
import org.simple.eventbus.EventBus

class VideoPlayViewModel : BaseViewModel() {

    lateinit var binding: AcVideoPlayBinding
    var videoUrl = ""
    var videoType = 0 // 1：启动页视频  2：播放视频
    var isDelete = false
    var isUpload = false

    override fun initViewModel() {
        binding.videoUrl = videoUrl
        binding.videoType = videoType
        binding.isDelete = isDelete
        binding.isUpload = isUpload
        if (!RomUtils.isHuawei) {
            fitComprehensiveScreen()
        }
        if (videoType == 1)
            functionSwitch()
    }

    override fun back(view: View) {
        super.back(view)
        activity.finish()
    }

    override fun right(view: View) {
        super.right(view)
        if (isUpload) {
            activity.startActivity<PublishDiscoverActivity>()
            EventBus.getDefault().post("上传视频", "lobsterUploadVideo")
        }
        if (isDelete)
            EventBus.getDefault().post("删除视频", "lobsterDeleteVideo")
        activity.finish()
    }

    /**
     * 功能开关
     */
    private fun functionSwitch() {
        mainDataSource.enqueue({ functionSwitch() }) {
            onSuccess {
                if (it.androidCommonSwitch == 1)
                    DebuggerUtils.checkDebuggableInNotDebugModel(BaseApp.context)
            }
        }
    }

    /**
     * 去登录
     */
    fun toLogin(view: View) {
        stopVideo()
        activity.startActivity<LoginActivity>()
        activity.finish()
    }

    /**
     * 关闭视频
     */
    private fun stopVideo() {
        binding.videoView.stopPlayback() //停止播放视频,并且释放
        binding.videoView.suspend() //在任何状态下释放媒体播放器
    }
}