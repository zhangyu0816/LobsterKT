package com.yimi.rentme.activity

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.yimi.rentme.R
import com.yimi.rentme.bean.VideoInfo
import com.yimi.rentme.databinding.AcVideoDetailBinding
import com.yimi.rentme.vm.VideoDetailViewModel
import com.zb.baselibs.activity.BaseActivity
import com.zb.baselibs.utils.RomUtils
import com.zb.baselibs.utils.StatusBarUtil
import kotlinx.android.synthetic.main.ac_video_detail.*
import org.simple.eventbus.Subscriber

class VideoDetailActivity : BaseActivity() {

    private val viewModel by getViewModel(VideoDetailViewModel::class.java) {
        binding = mBinding as AcVideoDetailBinding
        activity = this@VideoDetailActivity
        binding.viewModel = this
    }
    private var alphaOA: ObjectAnimator? = null

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
        return R.layout.ac_video_detail
    }

    override fun initView() {
        needEvenBus = true
        val extras = intent.extras
        if (extras != null)
            viewModel.friendDynId = extras.getLong("friendDynId")
        viewModel.initViewModel()

        alphaOA = ObjectAnimator.ofFloat(iv_image, "alpha", 1f, 0f).setDuration(500)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (alphaOA != null) {
            alphaOA!!.cancel()
        }
        alphaOA = null
    }

    /**
     * 播放视频
     */
    @Subscriber(tag = "lobsterVideoPlay")
    private fun lobsterVideoPlay(data: VideoInfo) {
        alphaOA!!.start()
    }
}