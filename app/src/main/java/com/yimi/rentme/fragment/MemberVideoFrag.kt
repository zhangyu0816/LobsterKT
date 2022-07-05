package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragMemberVideoBinding
import com.yimi.rentme.vm.fragment.MemberVideoViewModel
import com.zb.baselibs.activity.BaseFragment

class MemberVideoFrag(private val userId: Long) : BaseFragment() {

    private val viewModel by getViewModel(MemberVideoViewModel::class.java) {
        binding = mBinding as FragMemberVideoBinding
        activity = this@MemberVideoFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_member_video
    }

    override fun initView() {
        viewModel.userId = userId
        viewModel.initViewModel()
    }
}