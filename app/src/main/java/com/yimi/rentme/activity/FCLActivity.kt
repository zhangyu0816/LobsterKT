package com.yimi.rentme.activity

import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcFclBinding
import com.yimi.rentme.vm.FCLViewModel
import com.zb.baselibs.activity.BaseWhiteActivity
import org.simple.eventbus.EventBus
import org.simple.eventbus.Subscriber

class FCLActivity : BaseWhiteActivity() {

    private val viewModel by getViewModel(FCLViewModel::class.java) {
        binding = mBinding as AcFclBinding
        activity = this@FCLActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_fcl
    }

    override fun initView() {
        needEvenBus = true
        val extras = intent.extras
        if (extras != null) {
            viewModel.index = extras.getInt("index")
            viewModel.otherUserId = extras.getLong("otherUserId")
        }
        viewModel.initViewModel()
    }

    /**
     * 支付成功
     */
    @Subscriber(tag = "lobsterUpdateFCL")
    private fun lobsterUpdateFCL(data: String) {
    }

    /**
     * 支付成功
     */
    @Subscriber(tag = "kotlin_pay_back")
    private fun payBack(data: String) {
        EventBus.getDefault().post("", "lobsterUpdateMineInfo")
        viewModel.binding.isVip = true
    }
}