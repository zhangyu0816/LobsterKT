package com.yimi.rentme.views

import android.view.View
import com.yimi.rentme.bean.PairInfo

interface SuperLikeInterface {
    fun superLike(view: View?, pairInfo: PairInfo?)

    fun returnBack(){}
}