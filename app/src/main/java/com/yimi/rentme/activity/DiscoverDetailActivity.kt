package com.yimi.rentme.activity

import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcDiscoverDetailBinding
import com.yimi.rentme.vm.DiscoverDetailViewModel
import com.zb.baselibs.activity.BaseScreenActivity

class DiscoverDetailActivity : BaseScreenActivity() {

    private val viewModel by getViewModel(DiscoverDetailViewModel::class.java) {
        binding = mBinding as AcDiscoverDetailBinding
        activity = this@DiscoverDetailActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_discover_detail
    }

    override fun initView() {
        val extras = intent.extras
        if (extras != null)
            viewModel.friendDynId = extras.getLong("friendDynId")
        viewModel.initViewModel()
    }
}