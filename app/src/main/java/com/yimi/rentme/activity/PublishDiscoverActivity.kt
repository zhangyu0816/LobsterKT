package com.yimi.rentme.activity

import android.view.KeyEvent
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.bean.SelectImage
import com.yimi.rentme.databinding.AcPublishDiscoverBinding
import com.yimi.rentme.vm.PublishDiscoverViewModel
import com.zb.baselibs.activity.BaseWhiteActivity
import kotlinx.android.synthetic.main.ac_login.*
import org.simple.eventbus.Subscriber

class PublishDiscoverActivity : BaseWhiteActivity() {

    private val viewModel by getViewModel(PublishDiscoverViewModel::class.java) {
        binding = mBinding as AcPublishDiscoverBinding
        activity = this@PublishDiscoverActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_publish_discover
    }

    override fun initView() {
        needEvenBus = true
        viewModel.initViewModel()
    }

    /**
     * 上传图片
     */
    @Subscriber(tag = "lobsterUploadImageList")
    private fun lobsterUploadImageList(data: ArrayList<SelectImage>) {
        viewModel.uploadImageList(data)
    }

    /**
     * 删除视频
     */
    @Subscriber(tag = "lobsterDeleteVideo")
    private fun lobsterDeleteVideo(data: String) {
        viewModel.deleteVideo()
    }

    /**
     * 上传视频
     */
    @Subscriber(tag = "lobsterUploadVideo")
    private fun lobsterUploadVideo(data: String) {
        viewModel.uploadImageList(MineApp.selectImageList)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            viewModel.back(iv_back)
            return true
        }
        return super.dispatchKeyEvent(event)
    }
}