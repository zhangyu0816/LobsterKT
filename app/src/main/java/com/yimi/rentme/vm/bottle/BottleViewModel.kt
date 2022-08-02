package com.yimi.rentme.vm.bottle

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.os.SystemClock
import android.view.View
import android.view.animation.LinearInterpolator
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.bottle.BottleActivity
import com.yimi.rentme.activity.bottle.BottleListActivity
import com.yimi.rentme.bean.BottleInfo
import com.yimi.rentme.bean.MemberInfo
import com.yimi.rentme.databinding.AcBottleBinding
import com.yimi.rentme.dialog.BottleEditDF
import com.yimi.rentme.dialog.BottleQuestionDF
import com.yimi.rentme.views.BottleBGView
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.DateUtil
import com.zb.baselibs.utils.ObjectUtils
import com.zb.baselibs.utils.SCToastUtil
import org.jetbrains.anko.startActivity
import java.io.IOException

class BottleViewModel : BaseViewModel() {

    lateinit var binding: AcBottleBinding
    private var mPlayer: MediaPlayer? = null
    private var isFirst = true
    private var canClose = true
    private var friendDynId = 0L
    private var translateX: ObjectAnimator? = null
    private var scaleX: ObjectAnimator? = null
    private var scaleY: ObjectAnimator? = null
    private var alpha: ObjectAnimator? = null
    private var animatorSet: AnimatorSet? = null
    private var time = 1500L
    private lateinit var bottleInfo: BottleInfo
    private var throwIndex = 0

    override fun initViewModel() {
        binding.title = "我的漂流瓶"
        binding.noReadNum = MineApp.noReadBottleNum
        binding.memberInfo = MemberInfo()
        binding.info = ""
        binding.showBtn = true
        binding.edContent.typeface = MineApp.QingSongShouXieTiType

        mPlayer = MediaPlayer.create(activity, R.raw.sea_wave)
        BaseApp.fixedThreadPool.execute {
            SystemClock.sleep(200L)
            activity.runOnUiThread {
                appSound()
            }
        }

        animatorSet = AnimatorSet()
        translateX = ObjectAnimator.ofFloat(
            binding.ivStar, "translationX", 0f,
            (BaseApp.W - ObjectUtils.getViewSizeByWidthFromMax(250)).toFloat()
        ).setDuration(time)

        scaleX = ObjectAnimator.ofFloat(binding.ivBg, "scaleX", 1f, 1.5f).setDuration(time)
        scaleY = ObjectAnimator.ofFloat(binding.ivBg, "scaleY", 1f, 1.5f).setDuration(time)
        alpha = ObjectAnimator.ofFloat(binding.firstLayout, "alpha", 1f, 0f).setDuration(time)
        animatorSet!!.interpolator = LinearInterpolator()
        animatorSet!!.playTogether(scaleX, scaleY, translateX, alpha) //同时执行
        animatorSet!!.start()

        BaseApp.fixedThreadPool.execute {
            SystemClock.sleep(time)
            activity.runOnUiThread {
                binding.firstLayout.visibility = View.GONE
                binding.bottleWhiteBack.bottleBg.startBg()
            }
        }
    }

    override fun back(view: View) {
        super.back(view)
        if (binding.title == "扔一个瓶子"||binding.title == "漂流瓶") {
            close(null)
        } else
            activity.finish()
    }

    override fun right(view: View) {
        super.right(view)
        if (canClose) {
            close(null)
            BottleQuestionDF(activity).show(activity.supportFragmentManager)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mPlayer != null) {
            mPlayer!!.stop()
        }
        binding.bottleWhiteBack.bottleBg.stopBg()
        binding.bottleWhiteBack.bottleBg.setDestroy()
        translateX = null
        scaleX = null
        scaleY = null
        alpha = null
        animatorSet!!.cancel()
        animatorSet = null
    }

    fun onResume() {
        if (BaseApp.mActivityList[0] is BottleActivity) {
            if (!mPlayer!!.isPlaying) appSound()
            if (!isFirst) {
                binding.bottleWhiteBack.bottleBg.startBg()
            }
            isFirst = false
        }
    }

    fun onPause() {
        if (mPlayer != null) {
            mPlayer!!.stop()
        }
    }

    /**
     * 播放省音
     */
    private fun appSound() {
        try {
            if (mPlayer != null) {
                mPlayer!!.stop()
                mPlayer!!.prepare()
                mPlayer!!.isLooping = true
                mPlayer!!.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 获取漂流瓶
     */
    fun findBottle(view: View) {
        canClose = false
        binding.showBtn = false
        binding.bottleWhiteBack.bottleBg.stopBg()
        binding.bottleWhiteBack.bottleBg.startWang(object : BottleBGView.CallBack {
            override fun success() {
                findBottle()
            }
        })
    }

    /**
     * 扔掉瓶子
     */
    fun throwBottle(view: View) {
        bottleInfo = BottleInfo()
        binding.title = "扔一个瓶子"
        binding.isBottle = true
        binding.showBtn = false
        binding.showBottleTop = false
        binding.edContent.setText("")
        binding.edContent.isEnabled = true
        throwIndex = 0
        binding.throwIndex = throwIndex
        binding.ivThrow.setBackgroundResource(R.mipmap.throw_icon)
        binding.bottleWhiteBack.bottleBg.stopBg()
    }

    /**
     * 我的漂流瓶
     */
    fun myBottle(view: View) {
        if (mPlayer != null) {
            mPlayer!!.stop()
        }
        binding.bottleWhiteBack.bottleBg.stopBg()
        openBottle()
        activity.startActivity<BottleListActivity>()
    }

    /**
     * 关闭
     */
    fun close(view: View?) {
        hintKeyBoard2()
        binding.title = "我的漂流瓶"
        binding.isBottle = false
        binding.showBtn = true
        binding.bottleWhiteBack.bottleBg.startBg()
        friendDynId = 0
    }

    fun sure(view: View) {
        if (friendDynId == 0L) pickBottle(2) else {
            BottleEditDF(activity).setFriendDynId(friendDynId).setMainDataSource(mainDataSource)
                .show(activity.supportFragmentManager)
            close(null)
        }
    }

    fun cancel(view: View) {
        when (throwIndex) {
            0 -> {
                if (binding.edContent.getText().toString().trim().isEmpty()) {
                    SCToastUtil.showToast(activity, "漂流瓶内容不能为空", 2)
                    return
                }
                castBottle()
            }
            1 -> {
                pickBottle(1)
            }
            2 -> {
                binding.isBottle = false
                binding.bottleWhiteBack.bottleBg.throwBottle(object : BottleBGView.CallBack {
                    override fun success() {
                        close(null)
                    }
                })
            }
        }
    }

    /**
     * 寻找漂流瓶
     */
    private fun findBottle() {
        binding.title = "漂流瓶"
        val map = HashMap<String, String>()
        if (MineApp.sex != -1) map["sex"] = MineApp.sex.toString()
        mainDataSource.enqueue({ findBottle(map) }) {
            onSuccess {
                bottleInfo = it
                canClose = true
                otherInfo(bottleInfo.userId)
                binding.edContent.setText(bottleInfo.text)
                throwIndex = 1
                binding.throwIndex = throwIndex
                binding.edContent.isEnabled = false
                binding.ivThrow.setBackgroundResource(R.mipmap.throw_back_icon)
                binding.isBottle = true
                binding.showBottleTop = true
            }
            onFailed {
                randomNewDyn()
            }
        }
    }

    /**
     * 随机获取一条动态
     */
    private fun randomNewDyn() {
        val map = HashMap<String, String>()
        if (MineApp.sex != -1) map["sex"] = MineApp.sex.toString()
        mainDataSource.enqueue({ randomNewDyn(map) }) {
            onSuccess {
                canClose = true
                friendDynId = it.friendDynId
                binding.isBottle = true
                binding.showBottleTop = true
                throwIndex = 2
                binding.throwIndex = throwIndex
                binding.edContent.isEnabled = false
                binding.ivThrow.setBackgroundResource(R.mipmap.throw_fan_icon)
                binding.isBottle = true
                binding.edContent.setText(
                    it.text.ifEmpty { it.friendTitle }
                )
                otherInfo(it.userId)
            }
        }
    }

    /**
     * 用户信息
     */
    private fun otherInfo(otherUserId: Long) {
        mainDataSource.enqueue({ otherInfo(otherUserId) }) {
            onSuccess {
                binding.memberInfo = it
                binding.info =
                    "${if (it.sex == 0) "女 " else "男 "}${
                        DateUtil.getAge(it.birthday, it.age)
                    }岁 ${DateUtil.getConstellations(it.birthday)}"
            }
        }
    }

    /**+
     * 漂流瓶状态 .1.漂流中  2.被拾起  3.销毁
     */
    private fun pickBottle(driftBottleType: Int) {
        mainDataSource.enqueue({ pickBottle(bottleInfo.driftBottleId, driftBottleType) }) {
            onSuccess {
                if (driftBottleType == 1) {
                    binding.isBottle = false
                    binding.bottleWhiteBack.bottleBg.throwBottle(object : BottleBGView.CallBack {
                        override fun success() {
                            close(null)
                        }
                    })
                } else if (driftBottleType == 2) {
//                    ActivityUtils.getBottleChat(bottleInfo.getDriftBottleId(), false)
                    close(null)
                }
            }
        }
    }

    /**
     * 创建漂流瓶
     */
    private fun castBottle() {
        mainDataSource.enqueueLoading(
            { castBottle(binding.edContent.text.toString()) },
            "提交漂流瓶..."
        ) {
            onSuccess {
                binding.isBottle = false
                binding.bottleWhiteBack.bottleBg.throwBottle(object : BottleBGView.CallBack {
                    override fun success() {
                        close(null)
                    }
                })
            }
        }
    }

    /**
     *  打开漂流瓶
     */
    private fun openBottle() {
        val mPlayer: MediaPlayer? = MediaPlayer.create(activity, R.raw.open_bottle)
        try {
            if (mPlayer != null) {
                mPlayer.stop()
                mPlayer.prepare()
                mPlayer.start()
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        BaseApp.fixedThreadPool.execute {
            SystemClock.sleep(500L)
            activity.runOnUiThread {
                if (mPlayer != null) {
                    mPlayer.stop()
                    mPlayer.release() //释放资源
                }
            }
        }
    }
}