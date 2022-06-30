package com.yimi.rentme.vm

import android.os.CountDownTimer
import android.text.Html
import android.view.View
import com.yimi.rentme.R
import com.yimi.rentme.bean.ImageCaptcha
import com.yimi.rentme.databinding.AcBindingPhoneBinding
import com.yimi.rentme.dialog.ImageCaptchaDF
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.SCToastUtil
import org.simple.eventbus.EventBus

class BindingPhoneViewModel : BaseViewModel() {

    lateinit var binding: AcBindingPhoneBinding
    var isRegister = false
    var isFinish = false

    private val second = 120
    private var timer: CountDownTimer? = null
    private var isTimer = false

    override fun initViewModel() {
        binding.title = "手机绑定"
        binding.phone = ""
        binding.code = ""
        binding.remark = "获取验证码"
        timer = object : CountDownTimer((second * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                isTimer = true
                binding.remark = Html.fromHtml(
                    activity.resources.getString(
                        R.string.code_second, millisUntilFinished / 1000
                    )
                ).toString()
            }

            override fun onFinish() {
                isTimer = false
                binding.remark = "获取验证码"
                timer!!.cancel()
            }
        }
    }

    override fun back(view: View) {
        super.back(view)
        activity.finish()
    }

    /**
     * 获取绑定验证码
     */
    fun getCode(view: View) {
        if (isTimer) return
        if (!binding.phone!!.matches(BaseApp.phoneRegex)) {
            SCToastUtil.showToast(activity, "请输入正确的手机号", 2)
            return
        }
        if (isRegister)
            registerCaptcha()
        else
            ImageCaptchaDF(activity).setMainDataSource(mainDataSource)
                .setCallBack(object : ImageCaptchaDF.CallBack {
                    override fun success(imageCaptcha: ImageCaptcha, code: String) {
                        banderCaptcha(imageCaptcha, code)
                    }

                    override fun fail() {
                        isTimer = false
                        binding.remark = "获取验证码"
                        timer!!.cancel()
                    }
                }).show(activity.supportFragmentManager)
    }

    /**
     * 获取注册验证码
     */
    private fun registerCaptcha() {
        mainDataSource.enqueueLoading({ registerCaptcha(binding.phone!!) }, "获取注册验证码...") {
            onSuccess {
                SCToastUtil.showToast(activity, "短信验证码发送成功，请注意查看", 2)
                binding.remark = Html.fromHtml(
                    activity.resources.getString(R.string.code_second, second)
                ).toString()
                timer!!.start()
            }
            onFailed {
                isTimer = false
                binding.remark = "获取验证码"
                timer!!.cancel()
            }
        }
    }

    /**
     * 获取绑定手机号短信验证码
     */
    private fun banderCaptcha(imageCaptcha: ImageCaptcha, code: String) {
        mainDataSource.enqueueLoading({
            banderCaptcha(
                binding.phone!!,
                imageCaptcha.imageCaptchaToken,
                code
            )
        }, "获取短信验证码...") {
            onSuccess {
                SCToastUtil.showToast(activity, "短信验证码发送成功，请注意查看", 2)
                binding.remark = Html.fromHtml(
                    activity.resources.getString(R.string.code_second, second)
                ).toString()
                timer!!.start()
            }
            onFailed {
                isTimer = false
                binding.remark = "获取验证码"
                timer!!.cancel()
            }
        }
    }

    /**
     * 绑定
     */
    fun binding(view: View) {
        if (!binding.phone!!.matches(BaseApp.phoneRegex)) {
            SCToastUtil.showToast(activity, "请输入正确的手机号", 2)
            return
        }
        if (binding.code!!.length < 4) {
            SCToastUtil.showToast(activity, "请输入4位短信验证码", 2)
            return
        }
        if (isRegister)
            verifyCaptcha()
        else
            bindingPhone()
    }

    /**
     * 验证注册验证码
     */
    private fun verifyCaptcha() {
        mainDataSource.enqueueLoading(
            { verifyCaptcha(binding.phone!!, binding.code!!) },
            "提交注册信息..."
        ) {
            onSuccess {
                EventBus.getDefault()
                    .post("${binding.phone!!},${binding.code!!}", "lobsterVerifyCaptcha")
                activity.finish()
            }
            onFailed {
                isTimer = false
                binding.remark = "获取验证码"
                timer!!.cancel()
            }
        }
    }

    /**
     * 绑定手机号
     */
    private fun bindingPhone() {
        mainDataSource.enqueueLoading(
            { bindingPhone(binding.phone!!, binding.code!!) },
            "提交绑定信息..."
        ) {
            onSuccess {
                EventBus.getDefault().post("", "lobsterBindingPhone")
                activity.finish()
            }
            onFailed {
                isTimer = false
                binding.remark = "获取验证码"
                timer!!.cancel()
            }
        }
    }
}