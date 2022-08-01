package com.yimi.rentme

import android.annotation.SuppressLint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.bean.*
import com.yimi.rentme.roomdata.FollowDaoManager
import com.yimi.rentme.roomdata.GoodDaoManager
import com.yimi.rentme.roomdata.ImageSizeDaoManager
import com.yimi.rentme.roomdata.LikeTypeDaoManager
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.bean.ThreeInfo
import com.zb.baselibs.utils.getIntegerByAllName

class MineApp : BaseApp() {
    override fun getBaseUrl(): String {
        return when {
            getIntegerByAllName("lobster_selectBase") == 0 -> "https://xgapi.zuwo.la/" // 正式
            else -> "http://192.168.1.88:8090/" // 敏耀
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
        return arrayOf("${context.packageName}.activity.MainActivity")
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
    }
}