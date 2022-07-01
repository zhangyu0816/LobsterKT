package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragRegisterCodeBinding
import com.yimi.rentme.vm.fragment.RegisterCodeViewModel
import com.zb.baselibs.activity.BaseFragment

class RegisterCodeFrag : BaseFragment() {

    private val viewModel by getViewModel(RegisterCodeViewModel::class.java) {
        binding = mBinding as FragRegisterCodeBinding
        activity = this@RegisterCodeFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_register_code
    }

    override fun initView() {
        viewModel.initViewModel()
    }
}