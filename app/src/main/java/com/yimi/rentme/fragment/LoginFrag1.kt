package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragLogin1Binding
import com.yimi.rentme.vm.fragment.LoginFrag1ViewModel
import com.zb.baselibs.activity.BaseFragment

class LoginFrag1 : BaseFragment() {

    private val viewModel by getViewModel(LoginFrag1ViewModel::class.java) {
        binding = mBinding as FragLogin1Binding
        activity = this@LoginFrag1.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_login_1
    }

    override fun initView() {
        viewModel.initViewModel()
    }
}