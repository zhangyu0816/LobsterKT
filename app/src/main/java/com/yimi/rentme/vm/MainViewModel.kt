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
import com.yimi.rentme.roomdata.*
import com.yimi.rentme.utils.OpenNotice
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.RemindDF
import com.zb.baselibs.mimc.CustomMessageBody
import com.zb.baselibs.mimc.UserManager
import com.zb.baselibs.utils.*
import com.zb.baselibs.utils.permission.requestPermissionsForResult
import com.zb.baselibs.views.replaceFragment
import org.simple.eventbus.EventBus

class MainViewModel : BaseViewModel(), UserManager.OnHandleMIMCMsgListener {

    lateinit var binding: AcMainBinding

    override fun initViewModel() {
        selectIndex(1)
        MineApp.followDaoManager = FollowDaoManager(BaseApp.context)
        MineApp.goodDaoManager = GoodDaoManager(BaseApp.context)
        MineApp.likeTypeDaoManager = LikeTypeDaoManager(BaseApp.context)
        MineApp.imageSizeDaoManager = ImageSizeDaoManager(BaseApp.context)
        MineApp.chatListDaoManager = ChatListDaoManager(BaseApp.context)
        MineApp.historyDaoManager = HistoryDaoManager(BaseApp.context)

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
        initUtil(activity)
        walletInfo()
        giftList()
        rechargeDiscountList()
        comType()
        firstOpenMemberPage()
        openedMemberPriceList()
        driftBottleChatList(1)
        myImAccountInfo()
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
            SystemClock.sleep(500L)
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
                OpenNotice.remindNotice(activity)
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
                MineApp.provinceId = it.provinceId
                MineApp.cityId = it.cityId
                MineApp.districtId = it.districtId
                MineApp.sex = it.sex
                firstOpenMemberPage()
            }
        }
    }

    /**
     * 更新普通会话列表中的漂流瓶
     */
    fun updateCommonBottle() {
        BaseApp.fixedThreadPool.execute {
            var chatListInfo =
                MineApp.chatListDaoManager.getChatListInfo("common_${MineApp.bottleUserId}")
            if (chatListInfo == null) {
                chatListInfo = ChatListInfo()
                chatListInfo.chatId = "common_${MineApp.bottleUserId}"
                chatListInfo.otherUserId = MineApp.bottleUserId
                chatListInfo.nick = "漂流瓶"
                chatListInfo.image = "bottle_logo_icon"
                chatListInfo.creationDate = DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss)
                chatListInfo.stanza = if (MineApp.noReadBottleNum == 0) "茫茫人海中，需要流浪到何时" else "您有新消息"
                chatListInfo.msgType = 1
                chatListInfo.noReadNum = MineApp.noReadBottleNum
                chatListInfo.publicTag = ""
                chatListInfo.effectType = 1
                chatListInfo.authType = 1
                chatListInfo.msgChannelType = 1
                chatListInfo.chatType = 2
                chatListInfo.mainUserId = getLong("userId")
                MineApp.chatListDaoManager.insert(chatListInfo)
            } else {
                MineApp.chatListDaoManager.updateChatListInfo(
                    "漂流瓶", "bottle_logo_icon", DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss),
                    if (MineApp.noReadBottleNum == 0) "茫茫人海中，需要流浪到何时" else "您有新消息",
                    1, MineApp.noReadBottleNum, "common_${MineApp.bottleUserId}"
                )
            }
        }
    }

    /**
     * 我的聊天账号
     */
    private fun myImAccountInfo() {
        mainDataSource.enqueue({ myImAccountInfo(3) }) {
            onSuccess {
                BaseApp.userManager.setHandleMIMCMsgListener(this@MainViewModel)
                BaseApp.imUserId = it.imUserId
                BaseApp.mimcUser = BaseApp.userManager.newMIMCUser(it.imUserId)
                BaseApp.mimcUser!!.login()
            }
        }
    }

    /**
     * 漂流瓶会话列表
     */
    private fun driftBottleChatList(pageNo: Int) {
        mainDataSource.enqueue({ driftBottleChatList(1, pageNo) }) {
            onSuccess {
                BaseApp.fixedThreadPool.execute {
                    for (item in it) {
                        item.mainUserId = getLong("userId")
                        val chatListInfo = ChatListInfo()
                        chatListInfo.chatId = "drift_${item.driftBottleId}"
                        chatListInfo.otherUserId = item.userId
                        chatListInfo.nick = item.nick
                        chatListInfo.image = item.image
                        chatListInfo.creationDate = item.creationDate
                        chatListInfo.stanza = item.stanza
                        chatListInfo.msgType = item.msgType
                        chatListInfo.noReadNum = item.noReadNum
                        chatListInfo.publicTag = item.publicTag
                        chatListInfo.effectType = item.effectType
                        chatListInfo.authType = item.authType
                        chatListInfo.msgChannelType = 2
                        chatListInfo.chatType = 2
                        chatListInfo.mainUserId = item.mainUserId
                        MineApp.chatListDaoManager.insert(chatListInfo)
                    }
                    activity.runOnUiThread {
                        driftBottleChatList(pageNo + 1)
                    }
                }

            }
            onFailed {
                if (it.isNoData) {
                    BaseApp.fixedThreadPool.execute {
                        val chatListInfoList = MineApp.chatListDaoManager.getChatListInfoList(2)
                        MineApp.noReadBottleNum = 0
                        for (item in chatListInfoList) {
                            MineApp.noReadBottleNum += item.noReadNum
                        }
                        activity.runOnUiThread {
                            updateCommonBottle()
                        }
                    }
                }
            }
        }
    }

    /**
     * 消息回调
     */
    override fun onHandleMessage(customMessageBody: CustomMessageBody, thirdMessageId: String) {
        activity.runOnUiThread {
            customMessageBody.thirdMessageId = thirdMessageId
            customMessageBody.creationDate = DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss)
            val otherUserId =
                if (customMessageBody.mFromId == getLong("userId")) customMessageBody.mToId else customMessageBody.mFromId
            if (customMessageBody.mDriftBottleId != 0L) { // 漂流瓶
                customMessageBody.msgChannelType = 2
                BaseApp.fixedThreadPool.execute {
                    val chatListInfo =
                        MineApp.chatListDaoManager.getChatListInfo("drift_${customMessageBody.mDriftBottleId}")
                    if (chatListInfo == null) {
                        activity.runOnUiThread {
                            otherInfo(otherUserId,  customMessageBody)
                        }
                    } else {
                        val noReadNum =
                            if (MineApp.nowChatId == "drift_${customMessageBody.mDriftBottleId}") 0 else chatListInfo.noReadNum + 1

                        MineApp.chatListDaoManager.updateChatListInfo(
                            chatListInfo.nick, chatListInfo.image, customMessageBody.creationDate,
                            customMessageBody.mStanza, customMessageBody.mMsgType,
                            noReadNum, "drift_${customMessageBody.mDriftBottleId}"
                        )
                        updateBottle(customMessageBody)
                    }
                }
            }
        }
    }

    /**
     * 用户信息
     */
    private fun otherInfo(otherUserId: Long,  body: CustomMessageBody) {
        mainDataSource.enqueue({ otherInfo(otherUserId) }) {
            onSuccess {
                when (body.msgChannelType) {
                    1 -> {}
                    2 ->
                        BaseApp.fixedThreadPool.execute {
                            // 漂流瓶会话列表
                            val noReadNum =
                                if (MineApp.nowChatId == "drift_${body.mDriftBottleId}") 0 else 1
                            val chatListInfo = ChatListInfo()
                            chatListInfo.chatId = "drift_${body.mDriftBottleId}"
                            chatListInfo.otherUserId = otherUserId
                            chatListInfo.nick = it.nick
                            chatListInfo.image = it.image
                            chatListInfo.creationDate = body.creationDate
                            chatListInfo.stanza = body.mStanza
                            chatListInfo.msgType = body.mMsgType
                            chatListInfo.noReadNum = noReadNum
                            chatListInfo.msgChannelType = body.msgChannelType
                            chatListInfo.chatType = 2
                            chatListInfo.mainUserId = getLong("userId")
                            MineApp.chatListDaoManager.insert(chatListInfo)
                            updateBottle(body)
                        }
                    3 -> {}
                }
            }
        }
    }

    /**
     * 更新漂流瓶
     */
    private fun updateBottle(body: CustomMessageBody) {
        EventBus.getDefault()
            .post(body.mDriftBottleId.toString(), "lobsterSingleBottle")
        if (MineApp.nowChatId == "drift_${body.mDriftBottleId}")
            EventBus.getDefault().post(body, "lobsterUpdateChat")
        val chatListInfoList = MineApp.chatListDaoManager.getChatListInfoList(2)
        MineApp.noReadBottleNum = 0
        for (item in chatListInfoList) {
            MineApp.noReadBottleNum += item.noReadNum
        }
        EventBus.getDefault().post("", "lobsterBottleNoReadNum")
    }
}