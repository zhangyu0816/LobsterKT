package com.yimi.rentme.views

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Animation
import android.widget.ImageView
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.internal.InternalAbstract
import com.yimi.rentme.R

class MyHeaderView : InternalAbstract {

    private var animator: ObjectAnimator? = null

    constructor(context: Context?) : this(context!!, null) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0) {

    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.refresh_head, this)
        val progress = view.findViewById<ImageView>(R.id.progress)
        animator = ObjectAnimator.ofFloat(progress, "rotation", 0f, 360f).setDuration(700)
        animator!!.repeatMode = ValueAnimator.RESTART
        animator!!.repeatCount = Animation.INFINITE
    }

    override fun onFinish(layout: RefreshLayout, success: Boolean): Int {
        animator!!.cancel()
        super.onFinish(layout, success)
        return 200 //延迟500毫秒之后再弹回
    }

    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {
        animator!!.start()
    }
}