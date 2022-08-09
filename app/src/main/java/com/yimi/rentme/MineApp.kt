package com.yimi.rentme

import android.annotation.SuppressLint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.bean.*
import com.yimi.rentme.roomdata.*
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.bean.ThreeInfo
import com.zb.baselibs.utils.getIntegerByAllName

class MineApp : BaseApp() {
    override fun getBaseUrl(): String {
        return when {
            getIntegerByAllName("lobster_selectBase") == 0 -> "https://xgapi.zuwo.la/" // 正式
//            getIntegerByAllName("lobster_selectBase") == 0 -> "http://xuminyao.gnway.cc/" // 正式
            else -> "http://192.168.110.205:8090/" // 敏耀
        }
    }

    override fun getImageUrl(): String {
        return "http://img.zuwo.la/"
    }

    override fun isHorizontal(): Boolean {
        return false
    }

    override fun getAppType(): String {
        return "203"
    }

    override fun getProjectName(): String {
        return "lobster"
    }

    override fun getYMData(): Array<String> {
        return arrayOf(
            "55cac14467e58e8bd7000359",
            "wxb83427622a6740f6",
            "97f837c0ae8b11af734041828ba4a737",
            "101928546",
            "a8d76c68d7590b71f5254aa87c4b24c8"
        )
    }

    override fun getNotificationChannelName(): String {
        return "${context.resources.getString(R.string.app_name)}推送"
    }

    override fun getNoticeClassList(): Array<String> {
        return arrayOf("${context.packageName}.activity.MainActivity","${context.packageName}.activity.NoticeActivity")
    }

    override fun getNoticeLogo(): Int {
        return R.mipmap.ic_launcher
    }

    companion object {
        const val CHAT_URL = "http://cimg.zuwo.la/"
        lateinit var videoPlayActivity: AppCompatActivity
        lateinit var mineInfo: MineInfo
        var registerInfo = RegisterInfo()
        var threeInfo = ThreeInfo()
        var walletInfo = WalletInfo()
        var discoverInfoList = ArrayList<DiscoverInfo>()
        var giftInfoList = ArrayList<GiftInfo>()
        var rechargeInfoList = ArrayList<RechargeInfo>()
        var reportList = ArrayList<Report>()
        var vipInfoList = ArrayList<VipInfo>()
        var provinceId = 0L
        var cityId = 0L
        var districtId = 0L
        var sex = 0
        var minAge = 0
        var maxAge = 100
        var likeCount = 30
        var noReadBottleNum = 0
        lateinit var QingSongShouXieTiType: Typeface
        const val bottleUserId = 1002L // 普通会话列表--漂流瓶
        const val systemUserId = 10000L
        var nowChatId = "" // 正在聊天的人
        var hasLocation = false

        @JvmField
        val selectImageList = ArrayList<SelectImage>()

        @JvmField
        var isFirstOpen = false

        @SuppressLint("StaticFieldLeak")
        lateinit var followDaoManager: FollowDaoManager

        @SuppressLint("StaticFieldLeak")
        lateinit var goodDaoManager: GoodDaoManager

        @SuppressLint("StaticFieldLeak")
        lateinit var likeTypeDaoManager: LikeTypeDaoManager

        @SuppressLint("StaticFieldLeak")
        lateinit var imageSizeDaoManager: ImageSizeDaoManager

        @SuppressLint("StaticFieldLeak")
        lateinit var chatListDaoManager: ChatListDaoManager

        @SuppressLint("StaticFieldLeak")
        lateinit var historyDaoManager: HistoryDaoManager
    }
}