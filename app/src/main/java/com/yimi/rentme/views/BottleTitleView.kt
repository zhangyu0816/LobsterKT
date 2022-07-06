package com.yimi.rentme.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.databinding.DataBindingUtil
import com.yimi.rentme.R
import com.yimi.rentme.databinding.BottleTitleBinding

class BottleTitleView : RoundRelativeLayout {
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

    private val animatorSet = AnimatorSet()
    private lateinit var binding: BottleTitleBinding

    private fun initView(context: Context) {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.bottle_title, null, false
        ) as BottleTitleBinding
        addView(binding.root)

        val y = 30f
        val ivBackY =
            ObjectAnimator.ofFloat(binding.ivBackWeak, "translationY", y, 0f, y, 0f, y, 0f, y)
        val ivFrontY =
            ObjectAnimator.ofFloat(binding.ivFrontWeak, "translationY", 0f, y, 0f, y, 0f, y, 0f)
        val ivBottleY =
            ObjectAnimator.ofFloat(binding.ivBottle, "translationY", y, 0f, y, 0f, y, 0f, y)
        val ivLightY =
            ObjectAnimator.ofFloat(binding.ivBottleLight, "translationY", y, 0f, y, 0f, y, 0f, y)

        val time = 10000L
        ivBackY.duration = time
        val repeat = Animation.INFINITE
        ivBackY.repeatCount = repeat
        ivBackY.repeatMode = ValueAnimator.RESTART

        ivFrontY.duration = time
        ivFrontY.repeatCount = repeat
        ivFrontY.repeatMode = ValueAnimator.RESTART

        ivBottleY.duration = time
        ivBottleY.repeatCount = repeat
        ivBottleY.repeatMode = ValueAnimator.RESTART

        ivLightY.duration = time
        ivLightY.repeatCount = repeat
        ivLightY.repeatMode = ValueAnimator.RESTART

        animatorSet.interpolator = LinearInterpolator()
        animatorSet.playTogether(ivBackY, ivFrontY, ivBottleY, ivLightY) //同时执行
    }

    fun start() {
        if (!animatorSet.isRunning) animatorSet.start()
    }

    fun stop() {
        if (animatorSet.isRunning) {
            animatorSet.removeAllListeners()
            animatorSet.end()
            animatorSet.cancel()
        }
    }
}