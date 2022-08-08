package com.yimi.rentme.adapter

import android.view.View
import android.widget.ImageView
import com.yimi.rentme.R
import com.yimi.rentme.bean.PairInfo
import com.yimi.rentme.views.card.BaseCardAdapter
import com.zb.baselibs.adapter.loadImage
import com.zb.baselibs.utils.ObjectUtils

class CardAdapter : BaseCardAdapter<PairInfo>() {
    private var dataList = ArrayList<PairInfo>()

    override val count: Int
        get() = dataList.size

    override val cardLayoutId: Int
        get() = R.layout.item_pair

    fun setDataList(dataList: ArrayList<PairInfo>) {
        this.dataList = dataList
    }

    override fun onBindData(position: Int, cardview: View) {
        val item = dataList[position]
        val ivPhoto = cardview.findViewById<ImageView>(R.id.iv_photo)
        loadImage(
            ivPhoto, item.singleImage, 0, R.mipmap.empty_icon,
            ObjectUtils.getViewSizeByWidth(0.94f), ObjectUtils.getViewSizeByHeight(0.86f), false,
            10f, null, false, 0, false, 0f
        )
    }
}