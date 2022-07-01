package com.yimi.rentme.fragment

import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragLoginNameBinding
import com.yimi.rentme.vm.fragment.LoginNameViewModel
import com.zb.baselibs.activity.BaseFragment
import com.zb.baselibs.app.BaseApp
import org.simple.eventbus.EventBus
import org.simple.eventbus.Subscriber

class LoginNameFrag : BaseFragment() {

    private val viewModel by getViewModel(LoginNameViewModel::class.java) {
        binding = mBinding as FragLoginNameBinding
        activity = this@LoginNameFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_login_name
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
        val dataList = data.split(",")
        MineApp.registerInfo.bindPhone = dataList[0]
        MineApp.registerInfo.captcha = dataList[1]
        BaseApp.fixedThreadPool.execute {
            SystemClock.sleep(200)
            viewModel.activity.runOnUiThread {
                EventBus.getDefault().post("", "lobsterRegisterSex")
            }
        }
    }

    /**
     * 已注册，只需绑定手机号
     */
    @Subscriber(tag = "lobsterBindingPhone")
    private fun lobsterBindingPhone(data: String) {
        viewModel.myInfo()
    }
}