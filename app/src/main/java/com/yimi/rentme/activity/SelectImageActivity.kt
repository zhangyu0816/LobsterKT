package com.yimi.rentme.activity

import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcSelectImageBinding
import com.yimi.rentme.vm.SelectImageViewModel
import com.zb.baselibs.activity.BaseColorActivity
import org.simple.eventbus.Subscriber

class SelectImageActivity : BaseColorActivity(com.zb.baselibs.R.color.black_252) {

    private val viewModel by getViewModel(SelectImageViewModel::class.java) {
        binding = mBinding as AcSelectImageBinding
        activity = this@SelectImageActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_select_image
    }

    override fun initView() {
        needEvenBus = true
        val extras = intent.extras
        if (extras != null) {
            viewModel.isMore = extras.getBoolean("isMore")
            viewModel.showVideo = extras.getBoolean("showVideo")
            viewModel.isPublish = extras.getBoolean("isPublish")
        }
        viewModel.initViewModel()
    }

    /**
     * 上传视频
     */
    @Subscriber(tag = "lobsterUploadVideo")
    private fun lobsterUploadVideo(data: String) {
        finish()
    }
}