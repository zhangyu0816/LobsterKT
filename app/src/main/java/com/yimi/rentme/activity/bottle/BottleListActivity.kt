package com.yimi.rentme.activity.bottle

import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcBottleListBinding
import com.yimi.rentme.vm.bottle.BottleListViewModel
import com.zb.baselibs.activity.BaseScreenActivity

class BottleListActivity : BaseScreenActivity() {

    private val viewModel by getViewModel(BottleListViewModel::class.java) {
        binding = mBinding as AcBottleListBinding
        activity = this@BottleListActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_bottle_list
    }

    override fun initView() {
        viewModel.initViewModel()
    }
}