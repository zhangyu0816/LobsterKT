package com.yimi.rentme.vm.fragment

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.SystemClock
import android.view.View
import android.view.animation.Animation
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragMainHomeBinding
import com.yimi.rentme.fragment.FollowFrag
import com.yimi.rentme.fragment.MemberDiscoverFrag
import com.yimi.rentme.fragment.MemberVideoFrag
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.views.replaceFragment

class MainHomeViewModel : BaseViewModel() {

    lateinit var binding: FragMainHomeBinding
    private lateinit var pvhSY: PropertyValuesHolder
    private lateinit var pvhSX: PropertyValuesHolder
    private lateinit var pvhA: PropertyValuesHolder
    private var pvh: ObjectAnimator? = null

    override fun initViewModel() {
        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 0.5f, 1f)
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 0.5f, 1f)
        pvhA = PropertyValuesHolder.ofFloat("alpha", 1f, 0f)
        pvh = ObjectAnimator.ofPropertyValuesHolder(binding.circleView, pvhSY, pvhSX, pvhA)
            .setDuration(2000)
        pvh!!.repeatCount = Animation.INFINITE
        BaseApp.fixedThreadPool.execute {
            SystemClock.sleep(200)
            activity.runOnUiThread {
                binding.circleView.visibility = View.VISIBLE
                pvh!!.start()
            }
        }

        selectIndex(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (pvh != null)
            pvh!!.cancel()
        pvh = null
    }

    /**
     * 选择
     */
    fun selectIndex(index: Int) {
        binding.index = index
        when (index) {
            0 -> activity.replaceFragment(FollowFrag(), R.id.home_content)
            1 -> activity.replaceFragment(MemberDiscoverFrag(0L), R.id.home_content)
            2 -> activity.replaceFragment(MemberVideoFrag(0L), R.id.home_content)
        }
    }

    /**
     * 搜索
     */
    fun toSearch(view: View) {}

    /**
     * 上传动态
     */
    fun publishDiscover(view: View) {}
}