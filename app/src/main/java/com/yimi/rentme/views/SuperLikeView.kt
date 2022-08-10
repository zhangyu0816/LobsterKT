package com.yimi.rentme.views

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.yimi.rentme.R
import com.yimi.rentme.databinding.SuperLikeBinding
import com.zb.baselibs.adapter.onClickDelayed
import com.zb.baselibs.utils.ObjectUtils
import java.util.*

class SuperLikeView : RelativeLayout {
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

    private lateinit var binding: SuperLikeBinding
    private lateinit var superLikeInterface: SuperLikeInterface
    private var pvhTY: PropertyValuesHolder? = null
    private var pvhTX: PropertyValuesHolder? = null
    private var pvhStar1: ObjectAnimator? = null
    private var pvhStar2: ObjectAnimator? = null
    private val time = 500L
    private val ra = Random()
    private var mHandler: Handler? = null
    private val runnable = Runnable { start() }

    fun setSuperLikeInterface(superLikeInterface: SuperLikeInterface) {
        this.superLikeInterface = superLikeInterface
    }

    private fun initView(context: Context) {
        binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(context), R.layout.super_like, null, false
        ) as SuperLikeBinding
        addView(binding.root)
        binding.likeLayout.onClickDelayed {
            superLikeInterface.superLike(null, null)
        }
        binding.returnLayout.onClickDelayed {
            superLikeInterface.returnBack()
        }
    }

    private fun createAnimator() {
        pvhTY = PropertyValuesHolder.ofFloat(
            "translationY", 0f,
            -(ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(100)) + 50).toFloat()
        )
        pvhTX = PropertyValuesHolder.ofFloat(
            "translationX", 0f,
            -(ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(100)) + 50).toFloat()
        )
        pvhStar1 = ObjectAnimator.ofPropertyValuesHolder(binding.ivStar1, pvhTY, pvhTX)
        pvhStar1!!.duration = time

        pvhTY = PropertyValuesHolder.ofFloat(
            "translationY", 0f,
            -(ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(100)) + 50).toFloat()
        )
        pvhTX = PropertyValuesHolder.ofFloat(
            "translationX", 0f,
            (ra.nextInt(ObjectUtils.getViewSizeByWidthFromMax(100)) + 50).toFloat()
        )
        pvhStar2 = ObjectAnimator.ofPropertyValuesHolder(binding.ivStar2, pvhTY, pvhTX)
        pvhStar2!!.duration = time
    }

    fun start() {
        createAnimator()
        if (pvhStar1 != null && !pvhStar1!!.isRunning && pvhStar2 != null && !pvhStar2!!.isRunning) {
            binding.ivStar1.visibility = VISIBLE
            binding.ivStar2.visibility = VISIBLE
            pvhStar1!!.start()
            pvhStar2!!.start()
            if (mHandler == null) mHandler = Handler()
            mHandler!!.postDelayed(runnable, time + 2000L)
            mHandler!!.postDelayed({
                binding.ivStar1.visibility = GONE
                binding.ivStar2.visibility = GONE
                pvhStar1!!.cancel()
                pvhStar2!!.cancel()
            }, time)
        }
    }

    fun stop() {
        if (mHandler != null) {
            mHandler!!.removeCallbacks(runnable)
        }
        mHandler = null
        if (pvhStar1 != null && pvhStar1!!.isRunning)
            pvhStar1!!.cancel()
        if (pvhStar2 != null && pvhStar2!!.isRunning)
            pvhStar2!!.cancel()
    }

}