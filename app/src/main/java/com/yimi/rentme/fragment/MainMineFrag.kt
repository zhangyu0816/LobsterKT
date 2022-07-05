package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragMainMineBinding
import com.yimi.rentme.vm.fragment.MainMineViewModel
import com.zb.baselibs.activity.BaseFragment

class MainMineFrag : BaseFragment() {

    private val viewModel by getViewModel(MainMineViewModel::class.java) {
        binding = mBinding as FragMainMineBinding
        activity = this@MainMineFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_main_mine
    }

    override fun initView() {
        viewModel.initViewModel()
    }
}