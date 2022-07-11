package com.yimi.rentme.activity

import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcSearchBinding
import com.yimi.rentme.vm.SearchViewModel
import com.zb.baselibs.activity.BaseLightColorActivity

class SearchActivity : BaseLightColorActivity(com.zb.baselibs.R.color.black_f5) {

    private val viewModel by getViewModel(SearchViewModel::class.java) {
        binding = mBinding as AcSearchBinding
        activity = this@SearchActivity
        binding.viewModel = this
    }


    override fun getRes(): Int {
        return R.layout.ac_search
    }

    override fun initView() {
        viewModel.initViewModel()
    }
}