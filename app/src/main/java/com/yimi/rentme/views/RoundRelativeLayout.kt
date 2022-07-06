package com.yimi.rentme.views

import android.content.Context
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.RelativeLayout
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.dip2px

open class RoundRelativeLayout : RelativeLayout {
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?) : super(context!!) {
        init()
    }

    private var dpValue = 10f

    /**
     *
     * 初始化
     *
     */
    private fun init() {
        val outlineProvider = RoundViewOutlineProvider(BaseApp.context.dip2px(dpValue))
        setOutlineProvider(outlineProvider)
        clipToOutline = true
    }

    /**
     *
     * 圆角ViewOutlineProvider
     *
     */
    private class RoundViewOutlineProvider(private val roundSize: Int) : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(0, 0, view.width, view.height, roundSize.toFloat())
        }
    }

    fun setDpValue(dpValue: Float) {
        this.dpValue = dpValue
        init()
    }

}