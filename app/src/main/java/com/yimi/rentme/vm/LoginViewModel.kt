package com.yimi.rentme.vm

import android.os.SystemClock
import android.view.View
import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcLoginBinding
import com.yimi.rentme.fragment.*
import com.zb.baselibs.activity.BaseFragment
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.SCToastUtil
import com.zb.baselibs.utils.initUtil
import com.zb.baselibs.views.addFragment
import com.zb.baselibs.views.removeFragment
import org.simple.eventbus.EventBus
import kotlin.system.exitProcess

class LoginViewModel : BaseViewModel() {

    lateinit var binding: AcLoginBinding
    private lateinit var fragment: BaseFragment
    private var exitTime: Long = 0

    override fun initViewModel() {
        binding.right = ""
        binding.showBack = false
        initUtil(activity)
        fragment = LoginNameFrag()
        activity.addFragment(fragment, R.id.login_content)
    }

    override fun back(view: View) {
        if (fragment is LoginNameFrag) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                SCToastUtil.showToast(activity, "再按一次退出程序", 2)
                exitTime = System.currentTimeMillis()
            } else {
                BaseApp.exit()
                exitProcess(0)
            }
        } else {
            activity.removeFragment(fragment)
            BaseApp.fixedThreadPool.execute {
                SystemClock.sleep(10)
                activity.runOnUiThread {
                    fragment =
                        activity.supportFragmentManager.findFragmentById(R.id.login_content) as BaseFragment
                    if (fragment is LoginNameFrag) {
                        binding.right = ""
                        binding.showBack = false
                    }
                }
            }
        }
    }

    override fun right(view: View) {
        if (binding.right!! == "验证码登录") {
            binding.right = "密码登录"
            EventBus.getDefault().post(true, "lobsterLoginPass")
        } else {
            binding.right = "验证码登录"
            EventBus.getDefault().post(false, "lobsterLoginPass")
        }
    }

    /**
     * 检测是否注册
     */
    fun checkRegister(isRegister: Int) {
        if (isRegister == 1) {
            fragment = LoginPassFrag()
            binding.right = "验证码登录"
        } else {
            fragment = RegisterCodeFrag()
            binding.right = ""
        }
        activity.addFragment(fragment, R.id.login_content)
        binding.showBack = true
    }

    /**
     * 选择性别
     */
    fun registerSex() {
        binding.right = ""
        fragment = RegisterSexFrag()
        activity.addFragment(fragment, R.id.login_content)
        binding.showBack = true
    }

    /**
     * 填写昵称
     */
    fun registerNick() {
        binding.right = ""
        fragment = RegisterSexFrag()
        activity.addFragment(fragment, R.id.login_content)
        binding.showBack = true
    }

    /**
     * 填写昵称
     */
    fun registerBirthday() {
        binding.right = ""
        fragment = RegisterBirthdayFrag()
        activity.addFragment(fragment, R.id.login_content)
        binding.showBack = true
    }

    /**
     * 选择图片
     */
    fun registerImage(){
        binding.right = ""
        fragment = RegisterImageFrag()
        activity.addFragment(fragment, R.id.login_content)
        binding.showBack = true
    }
}