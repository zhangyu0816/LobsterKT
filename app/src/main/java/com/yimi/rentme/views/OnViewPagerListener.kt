package com.yimi.rentme.views

import android.view.View

interface OnViewPagerListener {
    //停止播放的监听
    fun onPageRelease(isNest: Boolean, view: View?)

    //播放的监听
    fun onPageSelected(isButton: Boolean, view: View?)
    fun onScroll()
}