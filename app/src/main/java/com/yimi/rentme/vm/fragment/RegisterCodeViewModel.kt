package com.yimi.rentme.vm.fragment

import android.os.CountDownTimer
import android.text.Html
import android.view.View
import android.widget.TextView
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragRegisterCodeBinding
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.utils.afterTextChanged
import org.simple.eventbus.EventBus

class RegisterCodeViewModel : BaseViewModel() {

    lateinit var binding: FragRegisterCodeBinding
    private val array = arrayOfNulls<TextView>(4)
    private var timer: CountDownTimer? = null
    private val second = 60
    var canGetCode = true

    override fun initViewModel() {
        binding.phone = MineApp.registerInfo.phone
        binding.canNext = false
        binding.codeRemark = ""
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
            binding.canNext = it.length == 4
            if (it.length == 4) {
                hintKeyBoard2()
                verifyCaptcha()
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
        registerCaptcha()
    }

    /**
     * 重置验证码
     */
    fun resetCode(view: View) {
        if (canGetCode)
            registerCaptcha()
    }

    /**
     * 获取注册验证码
     */
    private fun registerCaptcha() {
        mainDataSource.enqueueLoading({ registerCaptcha(binding.phone!!) }, "获取注册验证码...") {
            onSuccess {
                binding.codeRemark = Html.fromHtml(
                    activity.resources.getString(R.string.code_second, second)
                ).toString()
                timer!!.start()
            }
        }
    }

    /**
     * 验证 注册验证码
     */
    private fun verifyCaptcha() {
        mainDataSource.enqueueLoading({
            verifyCaptcha(binding.phone!!, binding.edCode.text.toString())
        }) {
            onSuccess {
                MineApp.registerInfo.captcha = binding.edCode.text.toString()
                EventBus.getDefault().post("","lobsterRegisterSex")
            }
            onFailed {
                binding.codeRemark =
                    Html.fromHtml(activity.resources.getString(R.string.code_second_finish))
                        .toString()
                timer!!.cancel()
                canGetCode = true
            }
        }
    }

    /**
     * 下一步
     */
    fun next(view: View) {
        if (binding.canNext) {
            verifyCaptcha()
        }
    }
}