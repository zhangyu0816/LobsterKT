package com.yimi.rentme.utils

import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.dip2px

object LobsterObjectUtil {

    @JvmStatic
    fun isRightVisibility(right: String?): Boolean {
        if (right == null)
            return false
        return right == BaseApp.context.resources.getString(R.string.all_read)
    }

    @JvmStatic
    fun getPhone(phone: String): String {
        return if (phone.length < 11) phone
        else
            "${phone.substring(0, 3)} ${phone.substring(3, 7)} ${phone.substring(7)}"
    }

    @JvmStatic
    fun getCodeWidth(): Int {
        return ((BaseApp.W - BaseApp.context.dip2px(106f)) / 4)
    }

    @JvmStatic
    fun cameraImageSelect(image: String): Boolean {
        return MineApp.selectImageList.contains(image)
    }
}