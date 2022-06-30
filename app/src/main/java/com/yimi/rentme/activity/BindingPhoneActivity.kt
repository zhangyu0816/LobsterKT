package com.yimi.rentme.activity

import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcBindingPhoneBinding
import com.yimi.rentme.vm.BindingPhoneViewModel
import com.zb.baselibs.activity.BaseWhiteActivity

class BindingPhoneActivity : BaseWhiteActivity() {

    private val viewModel by getViewModel(BindingPhoneViewModel::class.java) {
        binding = mBinding as AcBindingPhoneBinding
        activity = this@BindingPhoneActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_binding_phone
    }

    override fun initView() {
        val extras = intent.extras
        if (extras != null) {
            viewModel.isRegister = extras.getBoolean("isRegister")
            viewModel.isFinish = extras.getBoolean("isFinish")
        }
        viewModel.initViewModel()
    }
}