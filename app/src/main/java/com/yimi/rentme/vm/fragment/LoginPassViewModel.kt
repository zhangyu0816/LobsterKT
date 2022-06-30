package com.yimi.rentme.vm.fragment

import android.os.Build
import android.os.CountDownTimer
import android.text.Html
import android.view.View
import android.widget.TextView
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.MainActivity
import com.yimi.rentme.databinding.FragLoginPassBinding
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.RemindDF
import com.zb.baselibs.utils.*
import kotlinx.coroutines.Job
import org.jetbrains.anko.startActivity
import org.simple.eventbus.EventBus

class LoginPassViewModel : BaseViewModel() {

    lateinit var binding: FragLoginPassBinding
    private val array = arrayOfNulls<TextView>(4)
    private var timer: CountDownTimer? = null
    private val second = 60
    var canGetCode = true
    private var passErrorCount = 0

    override fun initViewModel() {
        binding.phone = MineApp.registerInfo.phone
        binding.pass = MineApp.registerInfo.pass.ifEmpty { getString("loginPass") }
        binding.edPass.afterTextChanged {
            MineApp.registerInfo.pass = it
            binding.pass = it
            binding.canNext = !binding.isCode && it.length > 5
        }

        array[0] = binding.tvCode1
        array[1] = binding.tvCode2
        array[2] = binding.tvCode3
        array[3] = binding.tvCode4
        binding.edCode.afterTextChanged {
            for (i in 0..3) {
                if (i < it.length) {
                    array[i]!!.text = it[i].toString()
                } else {
                    array[i]!!.text = ""
                }
            }
            binding.canNext = binding.isCode && it.length == 4
            if (it.length == 4) {
                hintKeyBoard2()
                loginByCaptcha()
            }
        }

        timer = object : CountDownTimer((second * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                canGetCode = false
                binding.codeRemark = Html.fromHtml(
                    activity.resources.getString(
                        R.string.code_second,
                        millisUntilFinished / 1000
                    )
                ).toString()
            }

            override fun onFinish() {
                binding.codeRemark =
                    Html.fromHtml(activity.resources.getString(R.string.code_second_finish))
                        .toString()
                timer!!.cancel()
                canGetCode = true
            }
        }
    }

    /**
     * 重置验证码
     */
    fun resetCode(view: View) {
        if (canGetCode)
            loginCaptcha()
    }

    /**
     * 获取登录验证码
     */
    fun loginCaptcha() {
        mainDataSource.enqueueLoading({ loginCaptcha(MineApp.registerInfo.phone) }, "获取登录验证码...") {
            onSuccess {
                binding.codeRemark = Html.fromHtml(
                    activity.resources.getString(
                        R.string.code_second, second
                    )
                ).toString()
                timer!!.start()
            }
        }
    }

    /**
     * 验证码登录
     */
    private fun loginByCaptcha() {
        val map = HashMap<String, String>()
        map["userName"] = MineApp.registerInfo.phone
        map["captcha"] = binding.edCode.text.toString()
        map["device"] = "Android"
        map["deviceSysVersion"] = Build.VERSION.RELEASE
        map["deviceCode"] = getString("deviceCode")
        map["channelId"] = getString("channelId")
        map["usePl"] = "2"
        map["appVersion"] = BaseApp.context.versionName()
        map["deviceHardwareInfo"] = getString("deviceHardwareInfo")
        showLoading(Job(), "登录中...")
        mainDataSource.enqueue({ loginByCaptcha(map) }) {
            onSuccess {
                saveLong("userId", it.id)
                saveString("sessionId", it.sessionId)
                saveInteger("myIsThreeLogin", 0)
                saveString("loginName", it.userName)
                saveString("loginPass", "")
                timer!!.cancel()
                myInfo()
            }
            onFailed {
                dismissLoading()
                binding.codeRemark =
                    Html.fromHtml(activity.resources.getString(R.string.code_second_finish))
                        .toString()
                timer!!.cancel()
                canGetCode = true
            }
        }
    }

    /**
     * 密码登录
     */
    private fun loginByPass() {
        val map = HashMap<String, String>()
        map["userName"] = MineApp.registerInfo.phone
        map["passWord"] = binding.edPass.text.toString()
        map["device"] = "Android"
        map["deviceSysVersion"] = Build.VERSION.RELEASE
        map["deviceCode"] = getString("deviceCode")
        map["channelId"] = getString("channelId")
        map["usePl"] = "2"
        map["appVersion"] = BaseApp.context.versionName()
        map["deviceHardwareInfo"] = getString("deviceHardwareInfo")
        showLoading(Job(), "登录中...")
        mainDataSource.enqueue({ loginByPass(map) }) {
            onSuccess {
                saveLong("userId", it.id)
                saveString("sessionId", it.sessionId)
                saveInteger("myIsThreeLogin", 0)
                saveString("loginName", it.userName)
                saveString("loginPass", MineApp.registerInfo.pass)
                myInfo()
            }
            onFailed {
                dismissLoading()
                if (it.errorMessage == "用户名或密码错误") {
                    passErrorCount++
                    if (passErrorCount > 2)
                        RemindDF(activity).setTitle("忘记密码")
                            .setContent("如果忘记密码，请使用验证码登录，登录成功后前往我的--设置--修改密码，重设密码")
                            .setSureName("验证码登录").setCallBack(object : RemindDF.CallBack {
                                override fun sure() {
                                    EventBus.getDefault().post("", "lobsterLoginCode")
                                }
                            }).show(activity.supportFragmentManager)
                }
            }
        }
    }

    /**
     * 获取用户信息
     */
    private fun myInfo() {
        mainDataSource.enqueue({ myInfo() }) {
            onSuccess {
                dismissLoading()
                MineApp.mineInfo = it
                passErrorCount = 0
                activity.startActivity<MainActivity>()
                EventBus.getDefault().post("", "lobsterFinishLogin")
            }
            onFailed {
                dismissLoading()
            }
        }
    }

    /**
     * 开启虾菇
     */
    fun next(view: View) {
        if (!binding.canNext) return
        if (binding.isCode)
            loginByCaptcha()
        else {
            if (binding.edPass.text.length < 6) {
                SCToastUtil.showToast(activity, "请输入6位密码", 2)
                return
            }
            loginByPass()
        }

    }
}