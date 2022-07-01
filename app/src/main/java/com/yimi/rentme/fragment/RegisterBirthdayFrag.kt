package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragRegisterBirthdayBinding
import com.yimi.rentme.vm.fragment.RegisterBirthdayViewModel
import com.zb.baselibs.activity.BaseFragment

class RegisterBirthdayFrag : BaseFragment() {

    private val viewModel by getViewModel(RegisterBirthdayViewModel::class.java) {
        binding = mBinding as FragRegisterBirthdayBinding
        activity = this@RegisterBirthdayFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_register_birthday
    }

    override fun initView() {
        viewModel.initViewModel()
    }
}