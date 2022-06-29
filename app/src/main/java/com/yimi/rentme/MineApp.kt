package com.yimi.rentme

import com.zb.baselibs.app.BaseApp

class MineApp : BaseApp() {
    override fun getBaseUrl(): String {
        TODO("Not yet implemented")
    }

    override fun getImageUrl(): String {
        TODO("Not yet implemented")
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
}