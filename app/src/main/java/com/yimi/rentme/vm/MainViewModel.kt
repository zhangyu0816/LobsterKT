package com.yimi.rentme.vm

import android.Manifest
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import android.telephony.TelephonyManager
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcMainBinding
import com.yimi.rentme.fragment.MainCardFrag
import com.yimi.rentme.fragment.MainChatFrag
import com.yimi.rentme.fragment.MainHomeFrag
import com.yimi.rentme.fragment.MainMineFrag
import com.yimi.rentme.roomdata.FollowDaoManager
import com.yimi.rentme.roomdata.GoodDaoManager
import com.yimi.rentme.roomdata.ImageSizeDaoManager
import com.yimi.rentme.roomdata.LikeTypeDaoManager
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.RemindDF
import com.zb.baselibs.utils.*
import com.zb.baselibs.utils.permission.requestPermissionsForResult
import com.zb.baselibs.views.replaceFragment
import org.simple.eventbus.EventBus

class MainViewModel : BaseViewModel() {

    lateinit var binding: AcMainBinding

    override fun initViewModel() {
        selectIndex(0)
        MineApp.followDaoManager = FollowDaoManager(BaseApp.context)
        MineApp.goodDaoManager = GoodDaoManager(BaseApp.context)
        MineApp.likeTypeDaoManager = LikeTypeDaoManager(BaseApp.context)
        MineApp.imageSizeDaoManager = ImageSizeDaoManager(BaseApp.context)
        MineApp.QingSongShouXieTiType = Typeface.createFromAsset(
            BaseApp.context.assets, "fonts/QingSongShouXieTi.ttf"
        )
        // 设备信息
        if (checkPermissionGranted(Manifest.permission.READ_PHONE_STATE)) {
            setDeviceCode()
        } else {
            if (getInteger("phone_permission") == 0) {
                saveInteger("phone_permission", 1)
                RemindDF(activity)
                    .setTitle("权限说明")
                    .setContent(
                        "当您注册并使用虾菇时，我们希望知道您设备的一些信息（包括设备序列号、设备MAC地址、唯一设备识别码（IMEI/android ID/OPENUDID等）），因此我们将会申请电话权限：" +
                                "\n 1、申请电话权限--获取设备信息（包括设备序列号、设备MAC地址、唯一设备识别码（IMEI/android ID/OPENUDID等））。" +
                                "\n 2、我们的保护：" +
                                "\n\t ①、请注意，单独的设备信息是无法识别特定自然人身份的信息。" +
                                "\n\t ②、如果我们将这类信息与其他信息结合用于识别特定自然人身份，或者将其与个人信息结合使用，则在结合使用期间，这类信息将被视为个人信息，除取得您授权或法律法规另有规定外，我们会将该类个人信息做匿名化、去标识化处理。" +
                                "\n 3、我们的用途：" +
                                "\n\t ①、保障用户账号安全、平台安全、运营安全，便于辨别不同的设备，方便错误原因精准定位。" +
                                "\n\t ②、您同意开启通知功能，即代表您同意将设备信息共享给每日互动股份有限公司，以便提供个推推送服务。" +
                                "\n\t ③、您使用支付功能，即代表您同意将设备信息共享给支付宝和微信，以便提供app支付功能。" +
                                "\n\t ④、为了保障及时了解软件使用情况及修复bug，我们将与友盟+共享设备信息，以便提供统计功能。" +
                                "\n\t ⑤、您同意开启定位功能，即代表您同意将设备信息共享给高德地图，以便提供地图功能和精准定位功能。" +
                                "\n 4、若您点击“同意”按钮，我们方可正式申请上述权限，以便获取设备信息，" +
                                "\n 5、若您点击“拒绝”按钮，我们将不再主动弹出该提示，也不会获取设备信息，不影响使用其他的虾菇功能/服务，" +
                                "\n 6、您也可以通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭手机权限。"
                    )
                    .setCallBack(object : RemindDF.CallBack {
                        override fun sure() {
                            setDeviceCode()
                        }
                    }).show(activity.supportFragmentManager)
            }
        }

        walletInfo()
        giftList()
        rechargeDiscountList()
        comType()
        firstOpenMemberPage()
        openedMemberPriceList()

        if (getInteger(
                "toLikeCount_${getLong("userId")}_${DateUtil.getNow(DateUtil.yyyy_MM_dd)}",
                -1
            ) == -1
        )
            MineApp.likeCount = MineApp.mineInfo.surplusToDaySuperLikeNumber
        else
            MineApp.likeCount = getInteger(
                "toLikeCount_${getLong("userId")}_${DateUtil.getNow(DateUtil.yyyy_MM_dd)}",
                -1
            )
        BaseApp.fixedThreadPool.execute {
            SystemClock.sleep(500)
            activity.runOnUiThread {
                if (getInteger("love_activity") == 0) {
                    saveInteger("love_activity", 1)
                    RemindDF(activity).setTitle("《爱情盲盒》")
                        .setContent("一,爱情盲盒玩法。\n1，把你的微信号存入盲盒中，等待异性用户来拆盲盒。\n2，你也可以取出一个异性盲盒，然后添加TA的微信。\n3，入口在“我的->爱情盲盒")
                        .setSureName("去玩一玩").setCallBack(object : RemindDF.CallBack {
                            override fun sure() {
//                                ActivityUtils.getLoveHome()
                            }
                        })
                        .show(activity.supportFragmentManager)
                }
            }
        }
    }

    /**
     * 选择
     */
    fun selectIndex(index: Int) {
        if (binding.index == index)
            return
        when (index) {
            0 -> activity.replaceFragment(MainHomeFrag(), R.id.main_content)
            1 -> activity.replaceFragment(MainCardFrag(), R.id.main_content)
            2 -> activity.replaceFragment(MainChatFrag(), R.id.main_content)
            3 -> activity.replaceFragment(MainMineFrag(), R.id.main_content)
        }
        binding.index = index
    }

    /**
     * 获取设备信息
     */
    private fun setDeviceCode() {
        launchMain {
            activity.requestPermissionsForResult(
                Manifest.permission.READ_PHONE_STATE, rationale = "为了更好的提供服务，需要获取电话权限"
            )
            val tm = activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val ANDROID_ID =
                Settings.System.getString(activity.contentResolver, Settings.System.ANDROID_ID)
            var imei: String?
            imei =
                if (BaseApp.context.applicationInfo.targetSdkVersion >= 29 && Build.VERSION.SDK_INT >= 29) {
                    //大于等于29使用特殊方法
                    UniqueID.getUniqueID(BaseApp.context)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tm.imei
                } else {
                    tm.deviceId
                }
            if (imei == null) {
                imei = ANDROID_ID
            }

            if (imei != null) {
                if (getString("deviceCode") != imei) {
                    saveString("deviceCode", imei)
                    BaseApp.fixedThreadPool.execute {
                        SystemClock.sleep(2000L)
                        modifyPushInfo()
                    }
                }
            }

            initUtil(activity)
        }
    }

    /**
     * 提交设备信息
     */
    private fun modifyPushInfo() {
        mainDataSource.enqueue({
            modifyPushInfo(
                getString("deviceCode"),
                getString("channelId"),
                getString("deviceHardwareInfo"),
                2
            )
        }) {
            onFailToast { false }
        }
    }

    /**
     * 钱包
     */
    fun walletInfo() {
        mainDataSource.enqueue({ walletAndPop() }) {
            onSuccess {
                MineApp.walletInfo = it
            }
        }
    }

    /**
     * 礼物列表
     */
    private fun giftList() {
        mainDataSource.enqueue({ giftList() }) {
            onSuccess {
                MineApp.giftInfoList.clear()
                MineApp.giftInfoList.addAll(it)
            }
        }
    }

    /**
     * 充值列表
     */
    private fun rechargeDiscountList() {
        mainDataSource.enqueue({ rechargeDiscountList(1) }) {
            onSuccess {
                MineApp.rechargeInfoList.clear()
                for (item in it) {
                    if (item.moneyType == 0) {
                        if (item.extraGiveMoney > 0.0)
                            item.content = String.format("送%.1f虾菇币", item.extraGiveMoney)
                    } else if (item.moneyType == 1) {
                        item.content = "最受欢迎"
                    } else {
                        item.content = "优惠最大"
                    }
                    MineApp.rechargeInfoList.add(item)
                }
            }
        }
    }

    /**
     * 举报类型
     */
    private fun comType() {
        mainDataSource.enqueue({ comType() }) {
            onSuccess {
                MineApp.reportList.clear()
                MineApp.reportList.addAll(it)
            }
        }
    }

    /**
     * 首充
     */
    private fun firstOpenMemberPage() {
        mainDataSource.enqueue({ firstOpenMemberPage() }) {
            onSuccess {
                MineApp.isFirstOpen = it == 1
                EventBus.getDefault().post("更新开通按钮", "lobsterUpdateBtn")
            }
        }
    }

    /**
     * 会员价格
     */
    private fun openedMemberPriceList() {
        mainDataSource.enqueue({ openedMemberPriceList() }) {
            onSuccess {
                MineApp.vipInfoList.clear()
                MineApp.vipInfoList.addAll(it)
            }
        }
    }

    /**
     * 自己的信息
     */
    fun myInfo() {
        mainDataSource.enqueue({ myInfo() }) {
            onSuccess {
                MineApp.mineInfo = it
                MineApp.sex = it.sex
                firstOpenMemberPage()
            }
        }
    }
}