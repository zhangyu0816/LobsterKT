package com.yimi.rentme.activity

import android.view.KeyEvent
import com.yimi.rentme.R
import com.yimi.rentme.bean.VideoInfo
import com.yimi.rentme.databinding.AcVideoListBinding
import com.yimi.rentme.vm.VideoListViewModel
import com.zb.baselibs.activity.BaseScreenActivity
import org.simple.eventbus.Subscriber

class VideoListActivity : BaseScreenActivity() {

    private val viewModel by getViewModel(VideoListViewModel::class.java) {
        binding = mBinding as AcVideoListBinding
        activity = this@VideoListActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_video_list
    }

    override fun initView() {
        needEvenBus = true
        val extras = intent.extras
        if (extras != null) {
            viewModel.pageNo = extras.getInt("pageNo")
            viewModel.position = extras.getInt("position")
        }
        viewModel.initViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    /**
     * 播放视频
     */
    @Subscriber(tag = "lobsterVideoPlay")
    private fun lobsterVideoPlay(data: VideoInfo) {
        viewModel.playAlphaOA(data)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            viewModel.onFinish(0)
            return true
        }
        return super.dispatchKeyEvent(event)
    }
}