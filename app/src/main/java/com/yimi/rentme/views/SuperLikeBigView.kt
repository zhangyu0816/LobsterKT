package com.yimi.rentme.views

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.yimi.rentme.R
import com.yimi.rentme.databinding.SuperLikeBigBinding
import com.zb.baselibs.adapter.onClickDelayed
import com.zb.baselibs.utils.ObjectUtils
import java.util.*

class SuperLikeBigView : RelativeLayout {
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        initView(context)
    }

    constructor(context: Context?) : super(context!!) {
        initView(context)
    }

    private lateinit var binding: SuperLikeBigBinding
    private var pvhTY: PropertyValuesHolder? = null
    private var pvhTX: PropertyValuesHolder? = null
    private var pvhStar1: ObjectAnimator? = null
    private var pvhStar2: ObjectAnimator? = null
    private lateinit var superLikeInterface: SuperLikeInterface
    private val ra = Random()
    private val time = 500L
    private var mHandler: Handler? = null
    private val runnable = Runnable { play() }

    fun setSuperLikeInterface(superLikeInterface: SuperLikeInterface) {
        this.superLikeInterface = superLikeInterface
    }

    private fun initView(context: Context) {
        binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(context), R.layout.super_like_big, null, false
        ) as SuperLikeBigBinding
        addView(binding.root)
        binding.ivSuperLike.onClickDelayed {
            superLikeInterface.superLike(null, null)
        }
        createAnimator()
    }

    private fun createAnimator() {
        pvhTY = PropertyValuesHolder.ofFloat(
            "translationY", 0f,
            -(ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(200)) + 50).toFloat()
        )
        pvhTX = PropertyValuesHolder.ofFloat(
            "translationX", 0f,
            -(ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(200)) + 50).toFloat()
        )
        pvhStar1 =
            ObjectAnimator.ofPropertyValuesHolder(binding.ivStar1, pvhTY, pvhTX).setDuration(time)
        pvhTY = PropertyValuesHolder.ofFloat(
            "translationY", 0f,
            -(ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(200)) + 50).toFloat()
        )
        pvhTX = PropertyValuesHolder.ofFloat(
            "translationX", 0f,
            (ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(200)) + 50).toFloat()
        )
        pvhStar2 =
            ObjectAnimator.ofPropertyValuesHolder(binding.ivStar2, pvhTY, pvhTX).setDuration(time)
    }

    fun play() {
        if (pvhStar1 != null && !pvhStar1!!.isRunning && pvhStar2 != null && !pvhStar2!!.isRunning) {
            binding.ivStar1.visibility = View.VISIBLE
            binding.ivStar2.visibility = View.VISIBLE
            pvhTY = PropertyValuesHolder.ofFloat(
                "translationY", 0f,
                -(ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(200)) + 50).toFloat()
            )
            pvhTX = PropertyValuesHolder.ofFloat(
                "translationX", 0f,
                -(ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(200)) + 50).toFloat()
            )
            pvhStar1!!.setValues(pvhTY, pvhTX)
            pvhStar1!!.start()
            pvhTY = PropertyValuesHolder.ofFloat(
                "translationY", 0f,
                -(ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(200)) + 50).toFloat()
            )
            pvhTX = PropertyValuesHolder.ofFloat(
                "translationX", 0f,
                (ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(200)) + 50).toFloat()
            )
            pvhStar2!!.setValues(pvhTY, pvhTX)
            pvhStar2!!.start()
            if (mHandler == null) {
                mHandler = Handler()
            }
            mHandler!!.postDelayed(
                runnable,
                time + 2000
            )
            mHandler!!.postDelayed(Runnable {
                binding.ivStar1.visibility = View.GONE
                binding.ivStar2.visibility = View.GONE
            }, time)
        }
    }

    fun stop() {
        mHandler?.removeCallbacks(runnable)
        mHandler = null
        if (pvhStar1 != null && pvhStar1!!.isRunning) pvhStar1!!.cancel()
        if (pvhStar2 != null && pvhStar2!!.isRunning) pvhStar2!!.cancel()
    }

}