package com.yimi.rentme.activity

import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import androidx.annotation.RequiresApi
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcVideoPlayBinding
import com.yimi.rentme.vm.VideoPlayViewModel
import com.zb.baselibs.activity.BaseActivity
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.RomUtils
import com.zb.baselibs.utils.SCToastUtil
import com.zb.baselibs.utils.StatusBarUtil
import kotlin.system.exitProcess

class VideoPlayActivity : BaseActivity() {

    private val viewModel by getViewModel(VideoPlayViewModel::class.java) {
        binding = mBinding as AcVideoPlayBinding
        activity = this@VideoPlayActivity
        binding.viewModel = this
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        if (RomUtils.isHuawei) {
            StatusBarUtil.setStatusBarColor(activity, R.color.black)
        } else {
            StatusBarUtil.statusBarLightMode(this)
        }
    }

    override fun getRes(): Int {
        return R.layout.ac_video_play
    }

    override fun initView() {
        MineApp.videoPlayActivity = this
        viewModel.initViewModel()
    }

    // 监听程序退出
    private var exitTime: Long = 0

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                SCToastUtil.showToast(activity, "再按一次退出程序", 2)
                exitTime = System.currentTimeMillis()
            } else {
                BaseApp.exit()
                exitProcess(0)
            }
            return true
        }
        return super.dispatchKeyEvent(event)
    }
}