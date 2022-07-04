package com.yimi.rentme.activity

import android.view.KeyEvent
import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcLoginBinding
import com.yimi.rentme.vm.LoginViewModel
import com.zb.baselibs.activity.BaseScreenActivity
import kotlinx.android.synthetic.main.ac_login.*
import org.simple.eventbus.EventBus
import org.simple.eventbus.Subscriber

class LoginActivity : BaseScreenActivity() {

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

    /**
     * 检测是否注册
     */
    @Subscriber(tag = "lobsterCheckRegister")
    private fun lobsterCheckRegister(data: Int) {
        viewModel.checkRegister(data)
    }

    /**
     * 验证码登录
     */
    @Subscriber(tag = "lobsterLoginCode")
    private fun lobsterLoginCode(data: String) {
        viewModel.binding.right = "密码登录"
        EventBus.getDefault().post(true, "lobsterLoginPass")
    }

    /**
     * 选择性别
     */
    @Subscriber(tag = "lobsterRegisterSex")
    private fun lobsterRegisterSex(data: String) {
        viewModel.registerSex()
    }

    /**
     * 填写昵称
     */
    @Subscriber(tag = "lobsterRegisterNick")
    private fun lobsterRegisterNick(data: String) {
        viewModel.registerNick()
    }

    /**
     * 选择生日
     */
    @Subscriber(tag = "lobsterRegisterBirthday")
    private fun lobsterRegisterBirthday(data: String) {
        viewModel.registerBirthday()
    }

    /**
     * 上传图片
     */
    @Subscriber(tag = "lobsterRegisterImage")
    private fun lobsterRegisterImage(data: String) {
        viewModel.registerImage()
    }

    /**
     * 上传图片
     */
    @Subscriber(tag = "lobsterRegisterMember")
    private fun lobsterRegisterMember(data: String) {
        viewModel.registerMember()
    }

    /**
     * 关闭登录
     */
    @Subscriber(tag = "lobsterFinishLogin")
    private fun lobsterFinishLogin(data: String) {
        finish()
    }

    // 监听程序退出
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            viewModel.back(iv_back)
            return true
        }
        return super.dispatchKeyEvent(event)
    }
}