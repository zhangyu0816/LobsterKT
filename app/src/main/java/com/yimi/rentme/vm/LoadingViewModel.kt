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
            SystemClock.sleep(2000L)
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
                            "\n 1.为了向您提供微信/QQ快捷登录功能、分享功能、微信支付功能，我们集成了友盟SDK。使用时，我们会获取设备信息（IMEI/Mac/android ID/IDFA/OPENUDID/GUID/SIM卡IMSI/地理位置信息）、存储、用户名称、头像及性别等内容。" +
                            "\n 2.为了向您提供支付宝支付功能，我们集成了AlipaySDK。我们会获取设备信息（IMEI/Mac/android ID/IDFA/OPENUDID/GUID/SIM卡IMSI/地理位置信息）、存储等内容。" +
                            "\n 3.为了向您提供推送功能，我们集成了个推SDK。使用时，我们会获取设备平台、设备厂商、设备品牌、设备识别码等设备信息，应用列表信息、网络信息以及位置相关信息。" +
                            "\n 4.为了向您提供聊天功能，我们集成了小米MIMC即时通讯SDK。使用时，我们会获取网络访问、访问网络状态、访问WLAN状态、获取手机设备识别码 （如imei、imsi、idfa、android ID）、读取/修改储存权限，录音权限。" +
                            "\n 5.为了向您提供地图功能，我们集成了高德SDK。使用时，我们会获取手机状态和身份、地理位置、存储卡内容。" +
                            "\n 6.您可以在我的--设置页面管理您的个人信息及您的授权。" +
                            "\n 7.我们会采用业界领先的安全技术保护好您的个人信息。" +
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
                MineApp.provinceId = it.provinceId
                MineApp.cityId = it.cityId
                MineApp.districtId = it.districtId
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
                        activity.startActivity<VideoPlayActivity>(
                            Pair(
                                "videoUrl",
                                "android.resource://" + BaseApp.context.packageName + "/" + R.raw.open
                            ),
                            Pair("videoType", 1)
                        )
                    activity.finish()
                } else if (it.isNoWIFI) {
                    SCToastUtil.showToast(activity, "服务器链接失败，请检测网络", 2)
                }
            }
        }
    }
}