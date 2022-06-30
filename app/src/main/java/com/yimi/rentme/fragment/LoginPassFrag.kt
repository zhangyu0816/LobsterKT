package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragLoginPassBinding
import com.yimi.rentme.vm.fragment.LoginPassViewModel
import com.zb.baselibs.activity.BaseFragment
import org.simple.eventbus.Subscriber

class LoginPassFrag : BaseFragment() {

    private val viewModel by getViewModel(LoginPassViewModel::class.java) {
        binding = mBinding as FragLoginPassBinding
        activity = this@LoginPassFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_login_pass
    }

    override fun initView() {
        needEvenBus = true
        viewModel.initViewModel()
    }

    /**
     * 登录方式
     */
    @Subscriber(tag = "lobsterLoginPass")
    private fun lobsterLoginPass(data: Boolean) {
        viewModel.binding.isCode = data // true 验证码登录
        if (data && viewModel.canGetCode)
            viewModel.loginCaptcha()
    }
}