package com.yimi.rentme.vm

import android.os.SystemClock
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.LoginActivity
import com.yimi.rentme.activity.MainActivity
import com.yimi.rentme.activity.VideoPlayActivity
import com.yimi.rentme.databinding.AcLoadingBinding
import com.zb.baselibs.activity.WebActivity
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.RuleDF
import com.zb.baselibs.utils.SCToastUtil
import com.zb.baselibs.utils.getInteger
import com.zb.baselibs.utils.saveInteger
import org.jetbrains.anko.startActivity

class LoadingViewModel : BaseViewModel() {

    lateinit var binding: AcLoadingBinding

    override fun initViewModel() {
        BaseApp.fixedThreadPool.execute {
            SystemClock.sleep(2000)
            if (getInteger("rule") == 0)
                RuleDF(activity).setContent(
                    "欢迎您使用${BaseApp.context.resources.getString(R.string.app_name)}！${
                        BaseApp.context.resources.getString(
                            R.string.app_name
                        )
                    }是由${BaseApp.context.resources.getString(R.string.company_name)}（以下简称“我们”）研发和运营的在线交友平台。我们将通过${
                        BaseApp.context.resources.getString(
                            R.string.rule_1
                        )
                    }和${
                        BaseApp.context.resources.getString(
                            R.string.rule_2
                        )
                    }帮助您了解我们收集、使用、存储和共享个人信息的情况，以及您所享有的相关权利。" +
                            "\n 1.为了向您提供好友推荐、好友聊天、动态互动等服务，我们需要收集您的设备信息、好友偏好、通知设置等个人信息。" +
                            "\n 2.您可以在我的--设置页面管理您的个人信息及您的授权。" +
                            "\n 3.我们会采用业界领先的安全技术保护好您的个人信息。" +
                            "\n\n您可以通过阅读完整版${BaseApp.context.resources.getString(R.string.rule_1)}和${
                                BaseApp.context.resources.getString(
                                    R.string.rule_2
                                )
                            }了解详细信息。" +
                            "\n如您同意，请点击“同意”开始接受我们的服务。"
                ).setClickContent(
                    "请阅读${BaseApp.context.resources.getString(R.string.rule_1)}和${
                        BaseApp.context.resources.getString(
                            R.string.rule_2
                        )
                    }并勾选"
                ).setPrivacyRule(BaseApp.context.resources.getString(R.string.rule_2))
                    .setRegisterRule(BaseApp.context.resources.getString(R.string.rule_1))
                    .setCallBack(object : RuleDF.CallBack {
                        override fun sure() {
                            saveInteger("rule", 1)
                            myInfo()
                        }

                        override fun cancel() {
                            activity.finish()
                        }

                        override fun registerUrlBack() {
                            activity.startActivity<WebActivity>(
                                Pair("webTitle", "注册协议"),
                                Pair(
                                    "webUrl",
                                    "${BaseApp.baseUrl}mobile/xiagu_reg_protocol.html"
                                )
                            )
                        }

                        override fun privacyUrlBack() {
                            activity.startActivity<WebActivity>(
                                Pair("webTitle", "隐私政策"),
                                Pair(
                                    "webUrl",
                                    "${BaseApp.baseUrl}mobile/xiagu_privacy_protocol.html"
                                )
                            )
                        }
                    }).show(activity.supportFragmentManager)
            else {
                myInfo()
            }
        }
    }

    private fun myInfo() {
        mainDataSource.enqueue({ myInfo() }) {
            onSuccess {
                MineApp.mineInfo = it
                MineApp.sex = it.sex
                activity.startActivity<MainActivity>()
                activity.finish()
            }
            onFailToast { false }
            onFailed {
                if (it.isNoLogin) {
                    if (getInteger("isVideoPlay") == 1)
                        activity.startActivity<LoginActivity>()
                    else
                        activity.startActivity<VideoPlayActivity>()
                    activity.finish()
                } else if (it.isNoWIFI) {
                    SCToastUtil.showToast(activity, "服务器链接失败，请检测网络", 2)
                }
            }
        }
    }
}