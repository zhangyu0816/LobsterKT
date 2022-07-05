package com.yimi.rentme.vm.fragment

import android.graphics.Color
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import com.yimi.rentme.MineApp
import com.yimi.rentme.activity.BindingPhoneActivity
import com.yimi.rentme.activity.MainActivity
import com.yimi.rentme.activity.SelectImageActivity
import com.yimi.rentme.bean.RegisterInfo
import com.yimi.rentme.databinding.FragLoginNameBinding
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.activity.WebActivity
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.bean.ThreeInfo
import com.zb.baselibs.dialog.SelectDF
import com.zb.baselibs.utils.*
import kotlinx.coroutines.Job
import org.jetbrains.anko.startActivity
import org.simple.eventbus.EventBus

class LoginNameViewModel : BaseViewModel() {

    lateinit var binding: FragLoginNameBinding
    private var baseUrlList = ArrayList<String>()
    private lateinit var threeLogin: ThreeLogin

    override fun initViewModel() {
        MineApp.registerInfo.serviceTags = "#旅行#摄影#乐观主义#老实孩子#简单#音乐会#"
        baseUrlList.add("正式服")
        baseUrlList.add("敏耀")
        binding.clickSelect = false
        binding.tvTitle.setOnLongClickListener {
            SelectDF(activity).setData(baseUrlList).setSelectBack(object : SelectDF.SelectBack {
                override fun selectPosition(position: Int) {
                    saveInteger("selectBase", position)
                    BaseApp.baseUrl = when {
                        getInteger("selectBase") == 0 -> "https://xgapi.zuwo.la/"
                        else -> "http://192.168.1.88:8090/"// 敏耀
                    }
                }
            }).show(activity.supportFragmentManager)
            false
        }
        binding.phone = MineApp.registerInfo.phone.ifEmpty { getString("loginName") }
        binding.edPhone.afterTextChanged {
            MineApp.registerInfo.phone = it
            binding.phone = it
        }
        setUrl()
        threeLogin = ThreeLogin(activity).setCallBack(object : ThreeLogin.CallBack {
            override fun success(threeInfo: ThreeInfo) {
                MineApp.threeInfo = threeInfo
                loginByUnion(threeInfo)
            }

            override fun fail() {
                dismissLoading()
            }
        })
    }

    /**
     * 清楚手机号
     */
    fun cleanPhone(view: View) {
        binding.phone = ""
        MineApp.registerInfo.phone = ""
        binding.edPhone.setText("")
    }

    /**
     * 点击
     */
    fun clickSelect(view: View) {
        binding.clickSelect = !binding.clickSelect
        saveInteger("clickSelect", if (binding.clickSelect) 1 else 0)
    }

    /**
     * 跳协议
     */
    private fun setUrl() {
        binding.clickSelect = getInteger("clickSelect") == 1

        val style = SpannableString("请阅读《用户注册协议》和《隐私政策》并勾选")
        style.setSpan(object : ClickableSpan() {
            override fun onClick(view: View) {
                activity.startActivity<WebActivity>(
                    Pair("webTitle", "注册协议"),
                    Pair(
                        "webUrl",
                        "${BaseApp.baseUrl}mobile/xiagu_reg_protocol.html"
                    )
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }, 3, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        style.setSpan(object : ClickableSpan() {
            override fun onClick(view: View) {
                activity.startActivity<WebActivity>(
                    Pair("webTitle", "隐私政策"),
                    Pair(
                        "webUrl",
                        "${BaseApp.baseUrl}mobile/xiagu_privacy_protocol.html"
                    )
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }, 12, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        style.setSpan(
            ForegroundColorSpan(Color.parseColor("#0d88c1")),
            3, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        style.setSpan(
            ForegroundColorSpan(Color.parseColor("#0d88c1")),
            12, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvClick.text = style
        binding.tvClick.movementMethod = LinkMovementMethod.getInstance()
    }

    /**
     * 微信登录
     */
    fun toWX(view: View) {
        if (!binding.clickSelect) {
            SCToastUtil.showToast(activity, "请仔细阅读底部协议，并勾选", 2)
            return
        }
        showLoading(Job(), "正在发起微信登录...")
        threeLogin.startThreeLogin(1)
    }

    /**
     * QQ登录
     */
    fun toQQ(view: View) {
        if (!binding.clickSelect) {
            SCToastUtil.showToast(activity, "请仔细阅读底部协议，并勾选", 2)
            return
        }
        showLoading(Job(), "正在发起QQ登录...")
        threeLogin.startThreeLogin(2)
    }

    /**
     * 第三方登录
     */
    private fun loginByUnion(threeInfo: ThreeInfo) {
        val map = HashMap<String, String>()
        map["openId"] = threeInfo.openId
        map["unionId"] = threeInfo.unionId
        map["unionNick"] = threeInfo.unionNick
        map["unionImage"] = threeInfo.unionImage
        map["unionSex"] = threeInfo.unionSex.toString()
        map["unionType"] = threeInfo.unionType.toString()
        map["device"] = "android"
        map["deviceSysVersion"] = Build.VERSION.RELEASE
        map["deviceCode"] = getString("deviceCode")
        map["channelId"] = getString("channelId")
        map["usePl"] = "2"
        map["appVersion"] = activity.versionName()
        map["deviceHardwareInfo"] = getString("deviceHardwareInfo")

        mainDataSource.enqueue({ loginByUnion(map) }) {
            onSuccess {
                saveLong("userId", it.id)
                saveString("sessionId", it.sessionId)
                saveInteger("myIsThreeLogin", 1)
                saveString("loginName", "")
                saveString("loginPass", "")
                if (it.phoneNum.isEmpty()) {
                    // 已注册，只需绑定手机号
                    activity.startActivity<BindingPhoneActivity>()
                    dismissLoading()
                } else
                    myInfo()
            }

            onFailed {
                dismissLoading()
                if (it.isNotRegister) {
                    // 继续完成注册步骤
                    MineApp.registerInfo.image = threeInfo.unionImage
                    activity.startActivity<BindingPhoneActivity>(
                        Pair("isRegister", true)
                    )
                }
            }
        }
    }

    /**
     * 获取用户信息
     */
    fun myInfo() {
        mainDataSource.enqueue({ myInfo() }) {
            onSuccess {
                dismissLoading()
                MineApp.mineInfo = it
                MineApp.registerInfo = RegisterInfo()
                activity.startActivity<MainActivity>()
                EventBus.getDefault().post("", "lobsterFinishLogin")
            }
            onFailed {
                dismissLoading()
            }
        }
    }

    /**
     * 下一步
     */
    fun next(view: View) {
//        if (binding.phone!!.length < 11) return
////        if (!binding.phone!!.matches(BaseApp.phoneRegex)) {
////            SCToastUtil.showToast(activity, "请输入正确手机号", 2)
////            return
////        }
//        if (!binding.clickSelect) {
//            SCToastUtil.showToast(activity, "请仔细阅读底部协议，并勾选", 2)
//            return
//        }
//        mainDataSource.enqueueLoading({ checkUserName(binding.phone!!) }, "检测是否注册...") {
//            onSuccess {
//                MineApp.threeInfo = ThreeInfo()
//                EventBus.getDefault().post(it.isRegister, "lobsterCheckRegister")
//            }
//        }
        activity.startActivity<SelectImageActivity>(
            Pair("showBottom", true)
        )
    }
}