package com.yimi.rentme

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.bean.MineInfo
import com.zb.baselibs.app.BaseApp
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
    }
}