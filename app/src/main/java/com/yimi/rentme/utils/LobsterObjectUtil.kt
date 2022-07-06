package com.yimi.rentme.utils

import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.zb.baselibs.app.BaseApp

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
}