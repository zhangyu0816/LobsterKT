package com.yimi.rentme.activity

import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcPublishDiscoverBinding
import com.yimi.rentme.vm.PublishDiscoverViewModel
import com.zb.baselibs.activity.BaseScreenActivity

class PublishDiscoverActivity : BaseScreenActivity() {

    private val viewModel by getViewModel(PublishDiscoverViewModel::class.java) {
        binding = mBinding as AcPublishDiscoverBinding
        activity = this@PublishDiscoverActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_publish_discover
    }

    override fun initView() {
        viewModel.initViewModel()
    }
}