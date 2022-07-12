package com.yimi.rentme.utils

import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.DateUtil
import com.zb.baselibs.utils.ObjectUtils

object LobsterObjectUtil {

    @JvmStatic
    fun isRightVisibility(right: String?): Boolean {
        if (right == null)
            return false
        return right == BaseApp.context.resources.getString(R.string.all_read)
    }

    @JvmStatic
    fun getBottleBgHeight(height: Float): Int {
        return (height * BaseApp.W / 1095f).toInt()
    }

    @JvmStatic
    fun cameraImageSelect(image: String): Boolean {
        return MineApp.selectImageList.contains(image)
    }

    @JvmStatic
    fun getImageHeight(scale: Float, width: Int, height: Int): Int {
        return (ObjectUtils.getViewSizeByWidth(scale) * height.toFloat() / width.toFloat()).toInt()
    }

    @JvmStatic
    fun getLogoHeight(scale: Float): Int {
        return (ObjectUtils.getViewSizeByWidth(scale) * 510f / 345f).toInt()
    }

    @JvmStatic
    fun getAge(birthday: String, age: Int): String {
        return DateUtil.getAge(birthday, age).toString()
    }

    @JvmStatic
    fun getTimeToToday(strDate: String): String {
        return DateUtil.getTimeToToday(strDate)
    }
}