package com.yimi.rentme.vm

import android.view.View
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.LoginActivity
import com.yimi.rentme.databinding.AcVideoPlayBinding
import com.yimi.rentme.utils.DebuggerUtils
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.RomUtils
import org.jetbrains.anko.startActivity

class VideoPlayViewModel : BaseViewModel() {

    lateinit var binding: AcVideoPlayBinding

    override fun initViewModel() {
        binding.videoUrl =  "android.resource://" + MineApp.videoPlayActivity.packageName + "/" + R.raw.open
        if (!RomUtils.isHuawei) {
            fitComprehensiveScreen()
        }
        functionSwitch()
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

    fun toLogin(view: View) {
        binding.videoView.stopPlayback() //停止播放视频,并且释放
        binding.videoView.suspend() //在任何状态下释放媒体播放器
        activity.startActivity<LoginActivity>()
        activity.finish()
    }
}