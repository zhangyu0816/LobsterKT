package com.yimi.rentme.activity

import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcVideoListBinding
import com.yimi.rentme.vm.VideoListViewModel
import com.zb.baselibs.activity.BaseScreenActivity

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
        viewModel.initViewModel()
    }
}