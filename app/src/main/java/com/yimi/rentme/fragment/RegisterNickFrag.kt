package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragRegisterNickBinding
import com.yimi.rentme.vm.fragment.RegisterNickViewModel
import com.zb.baselibs.activity.BaseFragment

class RegisterNickFrag : BaseFragment() {

    private val viewModel by getViewModel(RegisterNickViewModel::class.java) {
        binding = mBinding as FragRegisterNickBinding
        activity = this@RegisterNickFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_register_nick
    }

    override fun initView() {
        viewModel.initViewModel()
    }
}