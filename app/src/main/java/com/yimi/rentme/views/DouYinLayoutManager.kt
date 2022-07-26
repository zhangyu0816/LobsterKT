package com.yimi.rentme.views

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
import androidx.recyclerview.widget.RecyclerView.Recycler

class DouYinLayoutManager : LinearLayoutManager, OnChildAttachStateChangeListener {
    //判断是否上滑还是下滑
    var drift = 0
        private set
    private var onViewPagerListener: OnViewPagerListener? = null

    //吸顶，吸底
    private var pagerSnapHelper: PagerSnapHelper? = null
    fun getOnViewPagerListener(): OnViewPagerListener? {
        return onViewPagerListener
    }

    fun setOnViewPagerListener(onViewPagerListener: OnViewPagerListener?) {
        this.onViewPagerListener = onViewPagerListener
    }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    ) {
        pagerSnapHelper = PagerSnapHelper()
    }

    /**
     * 当manager完全添加到recycleview中是会被调用
     *
     * @param view
     */
    override fun onAttachedToWindow(view: RecyclerView) {
        view.addOnChildAttachStateChangeListener(this)
        pagerSnapHelper!!.attachToRecyclerView(view)
        super.onAttachedToWindow(view)
    }

    override fun canScrollVertically(): Boolean {
        return super.canScrollVertically()
    }

    override fun scrollVerticallyBy(dy: Int, recycler: Recycler, state: RecyclerView.State): Int {
        drift = dy
        onViewPagerListener!!.onScroll()
        return super.scrollVerticallyBy(dy, recycler, state)
    }

    override fun onChildViewAttachedToWindow(view: View) {
        if (drift >= 0) {
            //向上滑
            if (onViewPagerListener != null && Math.abs(drift) == view.height) {
                onViewPagerListener!!.onPageSelected(false, view)
            }
        } else {
            //向下滑
            if (onViewPagerListener != null && Math.abs(drift) == view.height) {
                onViewPagerListener!!.onPageSelected(true, view)
            }
        }
    }

    override fun onChildViewDetachedFromWindow(view: View) {
        if (drift >= 0) {
            //向上滑
            if (onViewPagerListener != null) {
                onViewPagerListener!!.onPageRelease(true, view)
            }
        } else {
            //向下滑
            if (onViewPagerListener != null) {
                onViewPagerListener!!.onPageRelease(false, view)
            }
        }
    }

    override fun onScrollStateChanged(state: Int) {
        if (state == RecyclerView.SCROLL_STATE_IDLE) { //当前显示的item
            val snapView = pagerSnapHelper!!.findSnapView(this)
            if (onViewPagerListener != null) {
                onViewPagerListener!!.onPageSelected(false, snapView)
            }
        }
        super.onScrollStateChanged(state)
    }
}