package com.yimi.rentme.vm.fragment

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.view.animation.Animation
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.FCLActivity
import com.yimi.rentme.activity.OpenVipActivity
import com.yimi.rentme.databinding.FragMainMineBinding
import com.yimi.rentme.dialog.VipAdDF
import com.yimi.rentme.fragment.MemberDiscoverFrag
import com.yimi.rentme.fragment.MemberVideoFrag
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.getInteger
import com.zb.baselibs.utils.getLong
import com.zb.baselibs.utils.saveInteger
import com.zb.baselibs.views.replaceFragment
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.startActivity

class MainMineViewModel : BaseViewModel() {

    lateinit var binding: FragMainMineBinding
    private var pvh: ObjectAnimator? = null
    private var pvhSY: PropertyValuesHolder? = null
    private var pvhSX: PropertyValuesHolder? = null
    private var pvhA: PropertyValuesHolder? = null

    private var index = 1
    private val mHandler = Handler()
    private val ra: Runnable = object : Runnable {
        override fun run() {
            index++
            if (index % 2 == 0)
                binding.vipLinear.setBackgroundResource(R.mipmap.mine_big_vip_item_bg)
            else
                binding.vipLinear.setBackgroundResource(R.mipmap.mine_vip_item_bg)
            mHandler.postDelayed(this, 500L)
        }
    }

    override fun initViewModel() {
        binding.mineInfo = MineApp.mineInfo
        binding.contactNum = MineApp.contactNum
        binding.hasNewBeLike =
            MineApp.contactNum.beLikeCount > getInteger("beLikeCount_${getLong("userId")}")
        binding.hasNewVisitor =
            MineApp.contactNum.visitorCount > getInteger("visitorCount_${getLong("userId")}")
        binding.isFirstOpen = MineApp.isFirstOpen

        getAi()

        selectIndex(0)

        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 0.5f, 1f)
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 0.5f, 1f)
        pvhA = PropertyValuesHolder.ofFloat("alpha", 1f, 0f)
        pvh = ObjectAnimator.ofPropertyValuesHolder(binding.circleView, pvhSY, pvhSX, pvhA)
            .setDuration(2000)
        pvh!!.repeatCount = Animation.INFINITE

        if (!MineApp.isFirstOpen) {
            binding.vipTitle.visibility = View.VISIBLE
            binding.vipInfo.visibility = View.VISIBLE
            binding.vipLinear.setBackgroundResource(R.mipmap.mine_share_item_bg)
        } else mHandler.postDelayed(ra, 500)

        BaseApp.fixedThreadPool.execute {
            SystemClock.sleep(200L)
            activity.runOnUiThread {
                goAnimator(binding.ivGo, 0.8f, 1.0f, 800L)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (pvh != null)
            pvh!!.cancel()
        pvhSY = null
        pvhSX = null
        pvhA = null
        pvh = null
        stopGo()
    }

    /**
     * 更新访问数量
     */
    fun visitor() {
        binding.contactNum = MineApp.contactNum
    }

    /**
     * 新闻
     */
    fun toNews(view: View) {
//        ActivityUtils.getMineNewsManager()
    }

    /**
     * 打赏排行
     */
    fun toReward(view: View) {
//        ActivityUtils.getHomeRewardList(0, BaseActivity.userId)
    }

    /**
     * 设置
     */
    fun toSetting(view: View) {
//        ActivityUtils.getMineSetting()
    }

    /**
     * 编辑个人信息
     */
    fun toEditMember(view: View) {
//        ActivityUtils.getMineEditMember()
    }

    /**
     * 开通VIP
     */
    fun openVip(view: View) {
        activity.startActivity<OpenVipActivity>()
    }

    /**
     * FCL
     */
    fun contactNumDetail(index: Int) {
        when (index) {
            2 -> {
                if (MineApp.mineInfo.memberType == 2) {
                    saveInteger("beLikeCount_${getLong("userId")}", MineApp.contactNum.beLikeCount)
                    binding.hasNewBeLike = false
                    activity.startActivity<FCLActivity>(
                        Pair("index", 2)
                    )
                } else
                    VipAdDF(activity).setMainDataSource(mainDataSource).setType(4)
                        .show(activity.supportFragmentManager)
            }
            4 -> {
//                ActivityUtils.getMineNewsList(2)
            }
            else -> {
                if (index == 3) {
                    saveInteger(
                        "visitorCount_${getLong("userId")}", MineApp.contactNum.visitorCount
                    )
                    binding.hasNewVisitor = false
                }
                activity.startActivity<FCLActivity>(
                    Pair("index", index)
                )
            }
        }
    }

    /**
     * 分享
     */
    fun openShare(index: Int) {
        if (index == 1 || !MineApp.isFirstOpen) {
//            ActivityUtils.getMineWeb(
//                "邀请好友赚钱", HttpManager.BASE_URL.toString() + "mobile/Share_marketingInfo" +
//                        "?userId=" + BaseActivity.userId + "&sessionId=" + BaseActivity.sessionId +
//                        "&pfDevice=Android&pfAppType=203&pfAppVersion=" + MineApp.versionName
//            )
        } else
            VipAdDF(activity).setType(100).setMainDataSource(mainDataSource)
                .show(activity.supportFragmentManager)
    }

    /**
     * 爱情盲盒
     */
    fun toLove(view: View) {}

    /**
     * 照相馆
     */
    fun toPhotoStudio(view: View) {}

    /**
     * 图片/视频动态
     */
    fun selectIndex(index: Int) {
        binding.index = index
        if (index == 0)
            activity.replaceFragment(MemberDiscoverFrag(1L), R.id.dyn_content)
        else
            activity.replaceFragment(MemberVideoFrag(1L), R.id.dyn_content)
    }

    /**
     * 动态数量
     */
    fun dynNotData(isNotData: Boolean) {
        binding.hasDyn = !isNotData
        if (binding.hasDyn)
            pvh!!.start()
    }

    /**
     * VIP开通成功
     */
    fun setBtn() {
        binding.isFirstOpen = MineApp.isFirstOpen
        if (!MineApp.isFirstOpen) {
            binding.vipTitle.visibility = View.VISIBLE
            binding.vipInfo.visibility = View.VISIBLE
        }
        mHandler.removeCallbacks(ra)
        binding.vipLinear.setBackgroundResource(R.mipmap.mine_share_item_bg)
    }

    /**
     * 上传动态
     */
    fun publishDiscover(view: View) {}

    /**
     * 显示爱情盲盒
     */
    private fun getAi() {
        mainDataSource.enqueue({ getAi() }) {
            onSuccess {
                binding.showAi = it == 1
            }
        }
    }
}