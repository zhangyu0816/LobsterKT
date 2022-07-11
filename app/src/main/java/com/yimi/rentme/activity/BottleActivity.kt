package com.yimi.rentme.activity

import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcBottleBinding
import com.yimi.rentme.vm.BottleViewModel
import com.zb.baselibs.activity.BaseScreenActivity

class BottleActivity : BaseScreenActivity() {

    private val viewModel by getViewModel(BottleViewModel::class.java) {
        binding = mBinding as AcBottleBinding
        activity = this@BottleActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_bottle
    }

    override fun initView() {
        viewModel.initViewModel()
    }
}