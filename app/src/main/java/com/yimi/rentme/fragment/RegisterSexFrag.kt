package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragRegisterSexBinding
import com.yimi.rentme.vm.fragment.RegisterSexViewModel
import com.zb.baselibs.activity.BaseFragment

class RegisterSexFrag : BaseFragment() {

    private val viewModel by getViewModel(RegisterSexViewModel::class.java) {
        binding = mBinding as FragRegisterSexBinding
        activity = this@RegisterSexFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_register_sex
    }

    override fun initView() {
        viewModel.initViewModel()
    }
}