package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragFollowBinding
import com.yimi.rentme.vm.fragment.FollowViewModel
import com.zb.baselibs.activity.BaseFragment

class FollowFrag : BaseFragment() {

    private val viewModel by getViewModel(FollowViewModel::class.java) {
        binding = mBinding as FragFollowBinding
        activity = this@FollowFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_follow
    }

    override fun initView() {
        viewModel.initViewModel()
    }
}