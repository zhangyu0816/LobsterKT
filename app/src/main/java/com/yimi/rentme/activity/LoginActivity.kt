package com.yimi.rentme.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcLoginBinding
import com.yimi.rentme.vm.LoginViewModel
import com.zb.baselibs.activity.BaseActivity
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.SCToastUtil
import com.zb.baselibs.utils.StatusBarUtil
import org.simple.eventbus.Subscriber
import kotlin.system.exitProcess

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

    // 监听程序退出
    private var exitTime: Long = 0

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                SCToastUtil.showToast(activity, "再按一次退出程序", 2)
                exitTime = System.currentTimeMillis()
            } else {
                BaseApp.exit()
                exitProcess(0)
            }
            return true
        }
        return super.dispatchKeyEvent(event)
    }
}