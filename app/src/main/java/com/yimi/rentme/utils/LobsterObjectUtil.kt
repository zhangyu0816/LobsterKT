package com.yimi.rentme.utils

import com.yimi.rentme.R
import com.zb.baselibs.app.BaseApp

object LobsterObjectUtil {

    @JvmStatic
    fun isRightVisibility(right: String): Boolean {
        return right == BaseApp.context.resources.getString(R.string.all_read)
    }
}