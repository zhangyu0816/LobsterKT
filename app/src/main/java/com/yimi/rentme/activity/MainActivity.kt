package com.yimi.rentme.activity

import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcMainBinding
import com.yimi.rentme.vm.MainViewModel
import com.zb.baselibs.activity.BaseScreenActivity
import org.simple.eventbus.Subscriber

class MainActivity : BaseScreenActivity() {

    private val viewModel by getViewModel(MainViewModel::class.java) {
        binding = mBinding as AcMainBinding
        activity = this@MainActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_main
    }

    override fun initView() {
        viewModel.initViewModel()
    }

    /**
     * 更新钱包
     */
    @Subscriber(tag = "lobsterUpdateWallet")
    private fun lobsterUpdateWallet(data: String) {
        viewModel.walletInfo()
    }

    /**
     * 更新自己信息
     */
    @Subscriber(tag = "lobsterUpdateMineInfo")
    private fun lobsterUpdateMineInfo(data: String) {
        viewModel.myInfo()
    }
}