package com.yimi.rentme.activity

import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcNewsBinding
import com.yimi.rentme.vm.NewsViewModel
import com.zb.baselibs.activity.BaseWhiteActivity

class NewsActivity : BaseWhiteActivity() {

    private val viewModel by getViewModel(NewsViewModel::class.java) {
        binding = mBinding as AcNewsBinding
        activity = this@NewsActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_news
    }

    override fun initView() {
        val extras = intent.extras
        if (extras != null)
            viewModel.reviewType = extras.getInt("reviewType")
        viewModel.initViewModel()
    }
}