package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragLogin1Binding
import com.yimi.rentme.vm.fragment.LoginFrag1ViewModel
import com.zb.baselibs.activity.BaseFragment
import org.simple.eventbus.Subscriber

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
        needEvenBus = true
        viewModel.initViewModel()
    }

    /**
     * 继续完成注册步骤
     */
    @Subscriber(tag = "lobsterVerifyCaptcha")
    private fun lobsterVerifyCaptcha(data: String) {
    }

    /**
     * 已注册，只需绑定手机号
     */
    @Subscriber(tag = "lobsterBindingPhone")
    private fun lobsterBindingPhone(data: String) {
        viewModel.myInfo()
    }
}