package com.yimi.rentme.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.yimi.rentme.R
import com.yimi.rentme.databinding.BottleBgBinding
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.ObjectUtils

class BottleBGView : RelativeLayout {
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

    private lateinit var binding: BottleBgBinding
    private var pvhTY: PropertyValuesHolder? = null
    private var pvhTX: PropertyValuesHolder? = null
    private var pvhA: PropertyValuesHolder? = null
    private var pvhR: PropertyValuesHolder? = null
    private var anim_fsxq: ObjectAnimator? = null
    private var anim_lsxq: ObjectAnimator? = null
    private var anim_hd_s: ObjectAnimator? = null
    private var anim_plp_d: ObjectAnimator? = null
    private var anim_bl: ObjectAnimator? = null
    private var pvh_plp: ObjectAnimator? = null
    private var pvh_hd_q: ObjectAnimator? = null
    private var pvh_jg: ObjectAnimator? = null
    private var anim_xx: ObjectAnimator? = null

    private var animatorSet: AnimatorSet? = null
    private var translateY: ObjectAnimator? = null
    private var translateX: ObjectAnimator? = null
    private var translateBackY: ObjectAnimator? = null
    private var translateBackX: ObjectAnimator? = null
    private var translateBackY2: ObjectAnimator? = null
    private var set: AnimatorSet? = null
    private var pvh: ObjectAnimator? = null

    private fun initView(context: Context) {
        binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(context), R.layout.bottle_bg, null, false
        ) as BottleBgBinding
        addView(binding.root)
        ivBlueMonth()
        ivFsxq()
        ivHai()
        ivPlpD()
        ivBoLan()
        ivPlp()
        ivHDQ()
        ivStarLight()
        ivXX()
        if (set == null)
            set = AnimatorSet()
        set!!.playTogether(anim_lsxq, anim_xx, anim_fsxq, anim_hd_s, anim_plp_d, anim_bl)
    }

    /**
     * 蓝色月亮
     */
    private fun ivBlueMonth() {
        anim_lsxq = ObjectAnimator.ofFloat(binding.lsxq, "translationY", 0f, 50f, 0f)
        anim_lsxq!!.duration = 5000
        anim_lsxq!!.repeatCount = Animation.INFINITE
    }

    /**
     * 粉色星球
     */
    private fun ivFsxq() {
        anim_fsxq = ObjectAnimator.ofFloat(binding.fsxq, "translationY", 0f, -50f, 0f)
            .setDuration(4000)
        anim_fsxq!!.repeatCount = Animation.INFINITE
        binding.showStar = false
        postDelayed({
            binding.showStar = true
        }, 1300)
    }

    /**
     * 海水
     */
    private fun ivHai() {
        anim_hd_s = ObjectAnimator.ofFloat(binding.hdS, "translationY", 0f, 30f, 0f)
            .setDuration(4000)
        anim_hd_s!!.repeatCount = Animation.INFINITE
    }

    /**
     * 海底瓶子
     */
    private fun ivPlpD() {
        anim_plp_d = ObjectAnimator.ofFloat(binding.plpD, "translationY", 0f, 30f, 0f)
            .setDuration(4000)
        anim_plp_d!!.repeatCount = Animation.INFINITE
    }

    /**
     * 波浪
     */
    private fun ivBoLan() {
        anim_bl = ObjectAnimator.ofFloat(binding.bl, "translationY", 0f, 30f, 0f).setDuration(4000)
        anim_bl!!.repeatCount = Animation.INFINITE
    }

    /**
     * 漂流瓶
     */
    private fun ivPlp() {
        pvhTY = PropertyValuesHolder.ofFloat("translationY", 0f, -30f, 0f)
        pvhTX = PropertyValuesHolder.ofFloat("translationX", 0f, 30f, 0f)
        pvh_plp = ObjectAnimator.ofPropertyValuesHolder(binding.plp, pvhTY, pvhTX).setDuration(4000)
        pvh_plp!!.repeatCount = Animation.INFINITE
    }

    /**
     * 海底
     */
    private fun ivHDQ() {
        pvhTY = PropertyValuesHolder.ofFloat("translationY", 0f, 30f, 0f)
        pvhA = PropertyValuesHolder.ofFloat("alpha", 1f, 0f, 1f)
        pvh_hd_q = ObjectAnimator.ofPropertyValuesHolder(binding.hdQ, pvhTY, pvhA).setDuration(4000)
        pvh_hd_q!!.repeatCount = Animation.INFINITE
    }

    /**
     * 星光
     */
    private fun ivStarLight() {
        pvhA = PropertyValuesHolder.ofFloat("alpha", 1f, 0f, 1f)
        pvhTY = PropertyValuesHolder.ofFloat("translationY", 0f, -30f, 0f)
        pvhTX = PropertyValuesHolder.ofFloat("translationX", 0f, 30f, 0f)
        pvh_jg =
            ObjectAnimator.ofPropertyValuesHolder(binding.jg, pvhTY, pvhTX, pvhA).setDuration(4000)
        pvh_jg!!.repeatCount = Animation.INFINITE
    }

    /**
     * 星星
     */
    private fun ivXX() {
        anim_xx = ObjectAnimator.ofFloat(binding.xx, "alpha", 1f, 0.5f, 1f)
            .setDuration(2000)
        anim_xx!!.repeatCount = Animation.INFINITE
    }

    /**
     * 捞瓶子
     */
    fun startWang(callBack: CallBack) {
        if (animatorSet == null)
            animatorSet = AnimatorSet()
        binding.ivWang.visibility = View.VISIBLE
        translateY = ObjectAnimator.ofFloat(
            binding.ivWang, "translationY", 0f,
            ObjectUtils.getViewSizeByWidthFromMax(600).toFloat()
        ).setDuration(1000)
        translateX = ObjectAnimator.ofFloat(
            binding.ivWang, "translationX", 0f,
            ObjectUtils.getViewSizeByWidthFromMax(100).toFloat(), 0f
        ).setDuration(1000)
        translateBackY = ObjectAnimator.ofFloat(
            binding.ivWangBack, "translationY", 0f,
            ObjectUtils.getViewSizeByWidthFromMax(600).toFloat()
        ).setDuration(1000)
        translateBackX = ObjectAnimator.ofFloat(
            binding.ivWangBack, "translationX", 0f,
            ObjectUtils.getViewSizeByWidthFromMax(100).toFloat(), 0f
        ).setDuration(1000)
        translateBackY2 = ObjectAnimator.ofFloat(
            binding.ivWangBack, "translationY",
            ObjectUtils.getViewSizeByWidthFromMax(600).toFloat(), 0f
        ).setDuration(1000)

        animatorSet!!.interpolator = LinearInterpolator()
        animatorSet!!.play(translateY).with(translateBackY)
        animatorSet!!.play(translateX).with(translateBackX).after(translateY)
        animatorSet!!.play(translateBackY2).after(translateX)
        animatorSet!!.start()

        postDelayed({
            binding.ivWangBack.visibility = View.VISIBLE
            binding.ivWang.visibility = View.GONE
        }, 2000)
        postDelayed({ callBack.success() }, 2500)
        postDelayed({
            binding.ivWangBack.visibility = View.GONE
            translateY = null
            translateX = null
            translateBackY = null
            translateBackX = null
            translateBackY2 = null
            animatorSet = null
        }, 3200)
    }

    /**
     * 扔瓶子
     */
    fun throwBottle(callBack: CallBack) {
        binding.ivBottle.visibility = View.VISIBLE
        val time = 1000L
        pvhTY = PropertyValuesHolder.ofFloat(
            "translationY", 0f,
            -ObjectUtils.getViewSizeByWidthFromMax(400).toFloat(), BaseApp.H.toFloat()
        )
        pvhTX = PropertyValuesHolder.ofFloat(
            "translationX", 0f, -(BaseApp.W / 2f), -BaseApp.W.toFloat()
        )
        pvhR = PropertyValuesHolder.ofFloat("rotation", 0f, -900f)
        pvh = ObjectAnimator.ofPropertyValuesHolder(binding.ivBottle, pvhTY, pvhTX, pvhR)
            .setDuration(time)
        pvh!!.start()

        postDelayed(
            {
                pvhTY = null
                pvhTX = null
                pvhR = null
                pvh = null
                callBack.success()
                binding.ivBottle.visibility = View.GONE
            }, time
        )
    }

    interface CallBack {
        fun success()
    }

    fun startBg() {
        if (set != null) set!!.start()
        if (pvh_hd_q != null) pvh_hd_q!!.start()
        if (pvh_jg != null) pvh_jg!!.start()
        if (pvh_plp != null) pvh_plp!!.start()
    }

    fun stopBg() {
        if (set != null) set!!.cancel()
        if (pvh_hd_q != null) pvh_hd_q!!.cancel()
        if (pvh_jg != null) pvh_jg!!.cancel()
        if (pvh_plp != null) pvh_plp!!.cancel()
    }

    fun setDestroy() {
        anim_lsxq = null
        anim_xx = null
        anim_fsxq = null
        anim_hd_s = null
        anim_plp_d = null
        anim_bl = null
        pvh_hd_q = null
        pvh_jg = null
        pvh_plp = null
        set = null
    }
}