package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragRegisterMemberBinding
import com.yimi.rentme.vm.fragment.RegisterMemberViewModel
import com.zb.baselibs.activity.BaseFragment

class RegisterMemberFrag : BaseFragment() {

    private val viewModel by getViewModel(RegisterMemberViewModel::class.java) {
        binding = mBinding as FragRegisterMemberBinding
        activity = this@RegisterMemberFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_register_member
    }

    override fun initView() {
        viewModel.initViewModel()
    }
}