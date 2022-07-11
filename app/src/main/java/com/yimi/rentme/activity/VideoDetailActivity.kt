package com.yimi.rentme.activity

import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcVideoDetailBinding
import com.yimi.rentme.vm.VideoDetailViewModel
import com.zb.baselibs.activity.BaseScreenActivity

class VideoDetailActivity : BaseScreenActivity() {

    private val viewModel by getViewModel(VideoDetailViewModel::class.java) {
        binding = mBinding as AcVideoDetailBinding
        activity = this@VideoDetailActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_video_detail
    }

    override fun initView() {
        val extras = intent.extras
        if (extras != null)
            viewModel.friendDynId = extras.getLong("friendDynId")
        viewModel.initViewModel()
    }
}