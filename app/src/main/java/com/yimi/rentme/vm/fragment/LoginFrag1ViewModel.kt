package com.yimi.rentme.vm.fragment

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import com.yimi.rentme.MineApp
import com.yimi.rentme.databinding.FragLogin1Binding
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.activity.WebActivity
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.bean.ThreeInfo
import com.zb.baselibs.dialog.SelectDF
import com.zb.baselibs.utils.*
import kotlinx.coroutines.Job
import org.jetbrains.anko.startActivity

class LoginFrag1ViewModel : BaseViewModel() {

    lateinit var binding: FragLogin1Binding
    private var baseUrlList = ArrayList<String>()
    private lateinit var threeLogin: ThreeLogin

    override fun initViewModel() {
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
        showLoading(Job(), "正在发起微信登录...")
        threeLogin.startThreeLogin(1)
    }

    /**
     * QQ登录
     */
    fun toQQ(view: View) {
        showLoading(Job(), "正在发起QQ登录...")
        threeLogin.startThreeLogin(2)
    }
}