package com.yimi.rentme.views

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Animation
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.databinding.ProgressBinding

class ProgressView : RelativeLayout {
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

    private lateinit var binding: ProgressBinding
    private var pvh: ObjectAnimator? = null
    private var pvhSY: PropertyValuesHolder? = null
    private var pvhSX: PropertyValuesHolder? = null

    private fun initView(context: Context) {
        binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(context), R.layout.progress, null, false
        ) as ProgressBinding
        addView(binding.root)
        binding.logo = MineApp.mineInfo.image
        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 1f, 2.3f)
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 1f, 2.3f)
        pvh = ObjectAnimator.ofPropertyValuesHolder(binding.ivProgress, pvhSY, pvhSX)
            .setDuration(1000)
        pvh!!.repeatCount = Animation.INFINITE
    }

    fun play() {
        if (pvh != null && !pvh!!.isRunning) pvh!!.start()
    }

    fun stop() {
        if (pvh != null && pvh!!.isRunning) pvh!!.cancel()
    }
}