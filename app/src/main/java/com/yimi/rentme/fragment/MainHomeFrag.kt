package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragMainHomeBinding
import com.yimi.rentme.vm.fragment.MainHomeViewModel
import com.zb.baselibs.activity.BaseFragment

class MainHomeFrag : BaseFragment() {

    private val viewModel by getViewModel(MainHomeViewModel::class.java) {
        binding = mBinding as FragMainHomeBinding
        activity = this@MainHomeFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_main_home
    }

    override fun initView() {
        viewModel.initViewModel()
    }
}