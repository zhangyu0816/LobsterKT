package com.yimi.rentme.vm.fragment

import android.os.SystemClock
import android.view.View
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.NewsActivity
import com.yimi.rentme.activity.SystemNewsActivity
import com.yimi.rentme.activity.bottle.BottleActivity
import com.yimi.rentme.databinding.FragMainChatBinding
import com.yimi.rentme.fragment.ChatFrag
import com.yimi.rentme.fragment.PairFrag
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.dip2px
import com.zb.baselibs.views.replaceFragment
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.startActivity

class MainChatViewModel : BaseViewModel() {

    lateinit var binding: FragMainChatBinding

    override fun initViewModel() {
        binding.newsCount = MineApp.newsCount
        selectIndex(0)
        BaseApp.fixedThreadPool.execute {
            SystemClock.sleep(300L)
            activity.runOnUiThread {
                val height: Int = BaseApp.context.dip2px(44f) - binding.topLinear.height
                binding.appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                    val y: Int = verticalOffset - height
                    var alpha: Float = y.toFloat() / BaseApp.context.dip2px(44f)
                    if (alpha >= 1f) alpha = 1f else if (alpha <= 0) {
                        alpha = 0f
                    }
                    binding.viewTop.alpha = 1 - alpha
                }
            }
        }
    }

    fun toBottle(view: View) {
        activity.startActivity<BottleActivity>()
    }

    fun selectIndex(index: Int) {
        binding.index = index
        when (index) {
            0 -> activity.replaceFragment(PairFrag(), R.id.pair_content)
            1 -> activity.replaceFragment(ChatFrag(), R.id.pair_content)
        }
    }

    /**
     * 消息
     */
    fun toNews(index: Int) {
        // 1 评论  2 点赞  3 礼物
        activity.startActivity<NewsActivity>(
            Pair("reviewType", index)
        )
    }

    /**
     * 系统消息
     */
    fun toService(view: View) {
        activity.startActivity<SystemNewsActivity>()
    }

    /**
     * 更新红i但
     */
    fun updateTabRed() {
        BaseApp.fixedThreadPool.execute {
            var showRed = false
            for (item in MineApp.chatListDaoManager.getChatListInfoListByChatType(4)) {
                if (item.noReadNum > 0) {
                    showRed = true
                    break
                }
            }
            activity.runOnUiThread {
                binding.showRed = showRed
            }
        }
    }
}