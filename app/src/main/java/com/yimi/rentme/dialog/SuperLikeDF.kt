package com.yimi.rentme.dialog

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.os.Handler
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.yimi.rentme.R
import com.yimi.rentme.databinding.DfSuperLikeBinding
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.BaseDialogFragment

class SuperLikeDF(activity: AppCompatActivity) : BaseDialogFragment(activity) {

    private lateinit var binding: DfSuperLikeBinding
    private var myHead = ""
    private var mySex = 0
    private var otherHead = ""
    private var otherSex = 0
    private var otherNick = ""
    private var isPair = false
    private var callBack: CallBack? = null

    private var otherLayoutX: ObjectAnimator? = null
    private var otherLayoutY: ObjectAnimator? = null
    private var mineLayoutX: ObjectAnimator? = null
    private var mineLayoutY: ObjectAnimator? = null
    private var ivSuperLike1X: ObjectAnimator? = null
    private var ivSuperLike1Y: ObjectAnimator? = null
    private var ivSuperLike2X: ObjectAnimator? = null
    private var ivSuperLike2Y: ObjectAnimator? = null
    private var scale1X: ObjectAnimator? = null
    private var scale1Y: ObjectAnimator? = null
    private var scale2X: ObjectAnimator? = null
    private var scale2Y: ObjectAnimator? = null
    private var animatorSet: AnimatorSet? = null

    private var translationX: ObjectAnimator? = null
    private var translationY: ObjectAnimator? = null
    private var animatorStarSet1: AnimatorSet? = null
    private var animatorStarSet2: AnimatorSet? = null
    private var animatorStarSet3: AnimatorSet? = null
    private var animatorStarSet4: AnimatorSet? = null
    private var animatorStarSet5: AnimatorSet? = null
    private var animatorStarSet6: AnimatorSet? = null

    private val leftX1 = -100f
    private val leftX2 = -400f
    private val leftY1 = -500f
    private val leftY2 = 600f
    private val rightX1 = 100f
    private val rightX2 = 400f
    private val rightY1 = -500f
    private val rightY2 = 600f
    private val time = 1000L

    private var mHandler: Handler? = null
    private val ra1 = Runnable {
        binding.ivSuperLike1.visibility = View.VISIBLE
        binding.ivSuperLike2.visibility = View.VISIBLE
    }
    private val ra2 = Runnable { this.start1() }
    private val ra3 = Runnable { binding.ivStar1.visibility = View.GONE }
    private val ra4 = Runnable {
        binding.ivStar2.visibility = View.VISIBLE
        start2()
    }
    private val ra5 = Runnable { binding.ivStar2.visibility = View.GONE }
    private val ra6 = Runnable {
        binding.ivStar3.visibility = View.VISIBLE
        start3()
    }
    private val ra7 = Runnable { binding.ivStar3.visibility = View.GONE }
    private val ra8 = Runnable {
        binding.ivStar4.visibility = View.VISIBLE
        start4()
    }
    private val ra9 = Runnable { binding.ivStar4.visibility = View.GONE }
    private val ra10 = Runnable {
        binding.ivStar5.visibility = View.VISIBLE
        start5()
    }
    private val ra11 = Runnable {
        binding.ivStar5.visibility = View.GONE
    }
    private val ra12 = Runnable {
        binding.ivStar6.visibility = View.VISIBLE
        start6()
    }
    private val ra13 = Runnable {
        binding.ivStar6.visibility = View.GONE
    }

    override val layoutId: Int
        get() = R.layout.df_super_like

    fun setMyHead(myHead: String): SuperLikeDF {
        this.myHead = myHead
        return this
    }

    fun setMySex(mySex: Int): SuperLikeDF {
        this.mySex = mySex
        return this
    }

    fun setOtherHead(otherHead: String): SuperLikeDF {
        this.otherHead = otherHead
        return this
    }

    fun setOtherSex(otherSex: Int): SuperLikeDF {
        this.otherSex = otherSex
        return this
    }

    fun setIsPair(isPair: Boolean): SuperLikeDF {
        this.isPair = isPair
        return this
    }

    fun setOtherNick(otherNick: String): SuperLikeDF {
        this.otherNick = otherNick
        return this
    }

    fun setCallBack(callBack: CallBack?): SuperLikeDF {
        this.callBack = callBack
        return this
    }

    override fun setDataBinding(viewDataBinding: ViewDataBinding?) {
        binding = viewDataBinding as DfSuperLikeBinding
    }

    fun show(manager: FragmentManager) {
        show(manager, "${BaseApp.projectName}_SuperLikeDF")
    }

    override fun onStart() {
        super.onStart()
        center(0.8)
    }

    override fun initUI() {
        binding.dialog = this
        binding.myHead = myHead
        binding.mySex = mySex
        binding.otherHead = otherHead
        binding.otherSex = otherSex
        binding.otherNick = otherNick
        binding.isPair = isPair

        binding.ivSuperLike1.visibility = View.GONE
        binding.ivSuperLike2.visibility = View.GONE
        if (isPair) {
            binding.ivStar1.setBackgroundResource(R.mipmap.like_red_icon)
            binding.ivStar2.setBackgroundResource(R.mipmap.like_red_icon)
            binding.ivStar3.setBackgroundResource(R.mipmap.like_red_icon)
            binding.ivStar4.setBackgroundResource(R.mipmap.like_red_icon)
            binding.ivStar5.setBackgroundResource(R.mipmap.like_red_icon)
            binding.ivStar6.setBackgroundResource(R.mipmap.like_red_icon)
        }
        val time = 300L
        otherLayoutX =
            ObjectAnimator.ofFloat(binding.otherLayout, "translationX", -50f, 0f).setDuration(time)
        otherLayoutY =
            ObjectAnimator.ofFloat(binding.otherLayout, "translationY", 50f, 0f).setDuration(time)

        mineLayoutX =
            ObjectAnimator.ofFloat(binding.mineLayout, "translationX", 50f, 0f).setDuration(time)
        mineLayoutY =
            ObjectAnimator.ofFloat(binding.mineLayout, "translationY", 50f, 0f).setDuration(time)

        ivSuperLike1X =
            ObjectAnimator.ofFloat(binding.ivSuperLike1, "translationX", -200f, 0f)
                .setDuration(time)
        ivSuperLike1Y =
            ObjectAnimator.ofFloat(binding.ivSuperLike1, "translationY", 100f, 0f).setDuration(time)

        ivSuperLike2X =
            ObjectAnimator.ofFloat(binding.ivSuperLike2, "translationX", 200f, 0f).setDuration(time)
        ivSuperLike2Y =
            ObjectAnimator.ofFloat(binding.ivSuperLike2, "translationY", 100f, 0f).setDuration(time)

        scale1X = ObjectAnimator.ofFloat(binding.ivSuperLike1, "scaleX", 1f, 1.3f, 1f, 1.3f, 1f)
            .setDuration(time)
        scale1Y = ObjectAnimator.ofFloat(binding.ivSuperLike1, "scaleY", 1f, 1.3f, 1f, 1.3f, 1f)
            .setDuration(time)

        scale2X = ObjectAnimator.ofFloat(binding.ivSuperLike2, "scaleX", 1f, 1.3f, 1f, 1.3f, 1f)
            .setDuration(time)
        scale2Y = ObjectAnimator.ofFloat(binding.ivSuperLike2, "scaleY", 1f, 1.3f, 1f, 1.3f, 1f)
            .setDuration(time)

        animatorSet!!.interpolator = LinearInterpolator()
        animatorSet!!.play(otherLayoutX).with(otherLayoutY).with(mineLayoutX).with(mineLayoutY)
        animatorSet!!.play(ivSuperLike1X).with(ivSuperLike1Y).with(ivSuperLike2X).with(ivSuperLike2Y)
            .after(otherLayoutX)
        animatorSet!!.play(scale1X).with(scale1Y).with(scale2X).with(scale2Y).after(ivSuperLike1X)
        animatorSet!!.start()

        mHandler!!.postDelayed(ra1, time)
        mHandler!!.postDelayed(ra2, time * 3)
    }

    private fun start1() {
        binding.ivStar1.visibility = View.VISIBLE
        translationX = ObjectAnimator.ofFloat(binding.ivStar1, "translationX", 0f, leftX1, leftX2)
            .setDuration(time)
        translationY = ObjectAnimator.ofFloat(binding.ivStar1, "translationY", 0f, leftY1, leftY2)
            .setDuration(time)
        if (animatorStarSet1 == null) return
        animatorStarSet1!!.interpolator = LinearInterpolator()
        animatorStarSet1!!.playTogether(translationX, translationY)
        animatorStarSet1!!.start()
        mHandler!!.postDelayed(ra3, time)
        mHandler!!.postDelayed(ra4, 200)
    }

    private fun start2() {
        binding.ivStar2.visibility = View.VISIBLE
        translationX =
            ObjectAnimator.ofFloat(
                binding.ivStar2,
                "translationX",
                0f,
                rightX1 + 100,
                rightX2 + 350
            )
                .setDuration(time)
        translationY =
            ObjectAnimator.ofFloat(
                binding.ivStar2,
                "translationY",
                0f,
                rightY1 + 100,
                rightY2 + 160
            )
                .setDuration(time)
        if (animatorStarSet2 == null) return
        animatorStarSet2!!.interpolator = LinearInterpolator()
        animatorStarSet2!!.playTogether(translationX, translationY)
        animatorStarSet2!!.start()
        mHandler!!.postDelayed(ra5, time)
        mHandler!!.postDelayed(ra6, 200)
    }

    private fun start3() {
        binding.ivStar3.visibility = View.VISIBLE
        translationX =
            ObjectAnimator.ofFloat(binding.ivStar3, "translationX", 0f, leftX1 + 50, leftX2 - 150)
                .setDuration(time)
        translationY =
            ObjectAnimator.ofFloat(binding.ivStar3, "translationY", 0f, leftY1 - 150, leftY2 + 150)
                .setDuration(time)
        if (animatorStarSet3 == null) return
        animatorStarSet3!!.interpolator = LinearInterpolator()
        animatorStarSet3!!.playTogether(translationX, translationY)
        animatorStarSet3!!.start()
        mHandler!!.postDelayed(ra7, time)
        mHandler!!.postDelayed(ra8, 200)
    }

    private fun start4() {
        binding.ivStar4.visibility = View.VISIBLE
        translationX =
            ObjectAnimator.ofFloat(binding.ivStar4, "translationX", 0f, rightX1 - 60, rightX2 + 90)
                .setDuration(time)
        translationY =
            ObjectAnimator.ofFloat(
                binding.ivStar4,
                "translationY",
                0f,
                rightY1 - 160,
                rightY2 + 190
            )
                .setDuration(time)
        if (animatorStarSet4 == null) return
        animatorStarSet4!!.interpolator = LinearInterpolator()
        animatorStarSet4!!.playTogether(translationX, translationY)
        animatorStarSet4!!.start()
        mHandler!!.postDelayed(ra9, time)
        mHandler!!.postDelayed(ra10, 200)
    }

    private fun start5() {
        binding.ivStar5.visibility = View.VISIBLE
        translationX =
            ObjectAnimator.ofFloat(binding.ivStar5, "translationX", 0f, leftX1 - 70, leftX2 + 170)
                .setDuration(time)
        translationY =
            ObjectAnimator.ofFloat(binding.ivStar5, "translationY", 0f, leftY1 + 180, leftY2 + 100)
                .setDuration(time)
        if (animatorStarSet5 == null) return
        animatorStarSet5!!.interpolator = LinearInterpolator()
        animatorStarSet5!!.playTogether(translationX, translationY)
        animatorStarSet5!!.start()
        mHandler!!.postDelayed(ra11, time)
        mHandler!!.postDelayed(ra12, 200)
    }

    private fun start6() {
        binding.ivStar6.visibility = View.VISIBLE
        translationX =
            ObjectAnimator.ofFloat(binding.ivStar6, "translationX", 0f, rightX1 - 40, rightX2 + 180)
                .setDuration(time)
        translationY =
            ObjectAnimator.ofFloat(binding.ivStar6, "translationY", 0f, rightY1 + 60, rightY2 - 90)
                .setDuration(time)
        if (animatorStarSet6 == null) return
        animatorStarSet6!!.interpolator = LinearInterpolator()
        animatorStarSet6!!.playTogether(translationX, translationY)
        animatorStarSet6!!.start()
        mHandler!!.postDelayed(ra13, time)
    }

    fun toChat(view: View) {
        if (callBack != null) callBack!!.sure()
        dismiss()
    }

    fun cancel(view: View) {
        dismiss()
    }

    override fun dismiss() {
        destroy()
        super.dismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        destroy()
        super.onCancel(dialog)
    }

    interface CallBack {
        fun sure()
    }

    private fun destroy() {
        otherLayoutX = null
        otherLayoutY = null
        mineLayoutX = null
        mineLayoutY = null
        ivSuperLike1X = null
        ivSuperLike1Y = null
        ivSuperLike2X = null
        ivSuperLike2Y = null
        scale1X = null
        scale1Y = null
        scale2X = null
        scale2Y = null
        translationX = null
        translationY = null
        animatorSet = null
        animatorStarSet1 = null
        animatorStarSet2 = null
        animatorStarSet3 = null
        animatorStarSet4 = null
        animatorStarSet5 = null
        animatorStarSet6 = null
        if (mHandler != null) {
            mHandler!!.removeCallbacks(ra1)
            mHandler!!.removeCallbacks(ra2)
            mHandler!!.removeCallbacks(ra3)
            mHandler!!.removeCallbacks(ra4)
            mHandler!!.removeCallbacks(ra5)
            mHandler!!.removeCallbacks(ra6)
            mHandler!!.removeCallbacks(ra7)
            mHandler!!.removeCallbacks(ra8)
            mHandler!!.removeCallbacks(ra9)
            mHandler!!.removeCallbacks(ra10)
            mHandler!!.removeCallbacks(ra11)
            mHandler!!.removeCallbacks(ra12)
            mHandler!!.removeCallbacks(ra13)
        }
        mHandler = null
    }
}