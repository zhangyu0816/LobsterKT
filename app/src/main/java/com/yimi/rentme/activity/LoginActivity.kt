package com.yimi.rentme.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcLoginBinding
import com.yimi.rentme.vm.LoginViewModel
import com.zb.baselibs.activity.BaseActivity
import com.zb.baselibs.utils.StatusBarUtil
import org.simple.eventbus.Subscriber

class LoginActivity : BaseActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.RegisterTheme)
        super.onCreate(savedInstanceState)
        StatusBarUtil.transparencyBar(this)
    }

    private val viewModel by getViewModel(LoginViewModel::class.java) {
        binding = mBinding as AcLoginBinding
        activity = this@LoginActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_login
    }

    override fun initView() {
        needEvenBus = true
        viewModel.initViewModel()
    }

    @Subscriber(tag = "lobsterFinishLogin")
    private fun lobsterFinishLogin(data: String) {
       finish()
    }
}