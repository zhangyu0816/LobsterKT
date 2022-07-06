package com.yimi.rentme.views

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.yimi.rentme.R
import com.yimi.rentme.databinding.GoodLayoutBinding

class GoodView : RelativeLayout {
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

    private lateinit var binding: GoodLayoutBinding
    private lateinit var pvhSY: PropertyValuesHolder
    private lateinit var pvhSX: PropertyValuesHolder
    private var pvh_dislike: ObjectAnimator? = null
    private var pvh_circle: ObjectAnimator? = null
    private var pvh_like: ObjectAnimator? = null


    private fun initView(context: Context) {
        binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(context), R.layout.good_layout, null, false
        ) as GoodLayoutBinding
        addView(binding.root)
    }

    fun playUnlike() {
        binding.ivUnLike.visibility = VISIBLE
        binding.ivCircle.visibility = GONE
        binding.ivLike.visibility = GONE
        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 0f, 1f, 0.8f, 1f)
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 0f, 1f, 0.8f, 1f)
        pvh_dislike =
            ObjectAnimator.ofPropertyValuesHolder(binding.ivUnLike, pvhSY, pvhSX).setDuration(500)
        pvh_dislike!!.start()
        postDelayed({
            if (pvh_dislike != null)
                pvh_dislike!!.cancel()
            pvh_dislike = null
        }, 500)
    }

    fun playLike() {
        binding.ivUnLike.visibility = GONE
        binding.ivCircle.visibility = VISIBLE
        binding.ivLike.visibility = VISIBLE
        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 0f, 0.8f)
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 0f, 0.8f)
        pvh_circle =
            ObjectAnimator.ofPropertyValuesHolder(binding.ivCircle, pvhSY, pvhSX).setDuration(500)
        pvh_circle!!.start()
        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 0f, 1f, 0.8f, 1f)
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 0f, 1f, 0.8f, 1f)
        pvh_like =
            ObjectAnimator.ofPropertyValuesHolder(binding.ivLike, pvhSY, pvhSX).setDuration(500)
        pvh_like!!.start()
        postDelayed({
            if (pvh_circle != null) {
                pvh_circle!!.cancel()
            }
            pvh_circle = null
            if (pvh_like != null)
                pvh_like!!.cancel()
            pvh_like = null
            binding.ivCircle.visibility = GONE
        }, 500)
    }
}