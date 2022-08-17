package com.yimi.rentme.activity

import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcSystemNewsBinding
import com.yimi.rentme.vm.SystemNewsViewModel
import com.zb.baselibs.activity.BaseWhiteActivity

class SystemNewsActivity : BaseWhiteActivity() {

    private val viewModel by getViewModel(SystemNewsViewModel::class.java) {
        binding = mBinding as AcSystemNewsBinding
        activity = this@SystemNewsActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_system_news
    }

    override fun initView() {
        viewModel.initViewModel()
    }
}