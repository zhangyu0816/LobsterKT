package com.yimi.rentme.activity

import android.view.KeyEvent
import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcOpenVipBinding
import com.yimi.rentme.vm.OpenVipViewModel
import com.zb.baselibs.activity.BaseScreenActivity
import kotlinx.android.synthetic.main.ac_login.*
import org.simple.eventbus.EventBus
import org.simple.eventbus.Subscriber

class OpenVipActivity : BaseScreenActivity() {

    private val viewModel by getViewModel(OpenVipViewModel::class.java) {
        binding = mBinding as AcOpenVipBinding
        activity = this@OpenVipActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_open_vip
    }

    override fun initView() {
        needEvenBus = true
        viewModel.initViewModel()
    }

    /**
     * 支付成功
     */
    @Subscriber(tag = "kotlin_pay_back")
    private fun payBack(data: String) {
        EventBus.getDefault().post("更新钱包", "lobsterUpdateWallet")
        EventBus.getDefault().post("更新我的信息", "lobsterUpdateMineInfo")
    }

    /**
     * 支付成功
     */
    @Subscriber(tag = "lobsterUpdateBtn")
    private fun lobsterUpdateBtn(data: String) {
        viewModel.setBtn()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            viewModel.back(iv_back)
            return true
        }
        return super.dispatchKeyEvent(event)
    }
}