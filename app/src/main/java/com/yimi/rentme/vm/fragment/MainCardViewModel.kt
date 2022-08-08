package com.yimi.rentme.vm.fragment

import android.annotation.SuppressLint
import android.os.SystemClock
import android.view.View
import com.yimi.rentme.MineApp
import com.yimi.rentme.activity.MemberDetailActivity
import com.yimi.rentme.adapter.CardAdapter
import com.yimi.rentme.bean.PairInfo
import com.yimi.rentme.databinding.FragMainCardBinding
import com.yimi.rentme.views.card.SwipeCardsView
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.SCToastUtil
import org.jetbrains.anko.startActivity

class MainCardViewModel : BaseViewModel() {

    lateinit var binding: FragMainCardBinding
    private val pairInfoList = ArrayList<PairInfo>()
    private lateinit var adapter: CardAdapter

    @SuppressLint("StaticFieldLeak")
    private lateinit var mCardImageView: View
    private var curIndex = -1
    private val userIdList = ArrayList<Long>()

    override fun initViewModel() {
        adapter = CardAdapter()
        binding.isProgressPlay = true
        binding.swipeCardsView.setCardsSlideListener(object : SwipeCardsView.CardsSlideListener {
            override fun onShow(index: Int) {
                curIndex = index
                if (adapter.count - curIndex == 3) {
                    prePairList()
                }
            }

            override fun onCardVanish(index: Int, type: SwipeCardsView.SlideType) {
                binding.swipeCardsView.setSrollDuration(400)
                if (type == SwipeCardsView.SlideType.LEFT) {
                    SCToastUtil.showToast(activity, "不喜欢", 2)
                } else {
                    SCToastUtil.showToast(activity, "喜欢", 2)
                }

            }

            override fun onItemClick(cardImageView: View, index: Int) {
                mCardImageView = cardImageView
                activity.startActivity<MemberDetailActivity>(
                    Pair("otherUserId", pairInfoList[index].otherUserId),
                    Pair("showLike", true)
                )
            }
        })
        prePairList()
    }

    /**
     * 更新卡片动画
     */
    fun moveCard(direction: Int) {
        BaseApp.fixedThreadPool.execute {
            SystemClock.sleep(500L)
            binding.swipeCardsView.setSrollDuration(1000)
            activity.runOnUiThread {
                when (direction) {
                    0 -> {
                        binding.swipeCardsView.slideCardOut(SwipeCardsView.SlideType.LEFT)
                    }
                    1 -> {
                        binding.swipeCardsView.slideCardOut(SwipeCardsView.SlideType.RIGHT)
                    }
                    2 -> {
                        binding.swipeCardsView.slideCardOut(SwipeCardsView.SlideType.RIGHT)
                    }
                }
            }
        }
    }

    /**
     * 预匹配
     */
    private fun prePairList() {
        val map = HashMap<String, String>()
        if (MineApp.sex != -1) {
            map["sex"] = MineApp.sex.toString()
        }
        if (MineApp.minAge != 18 || MineApp.maxAge != 70) {
            map["maxAge"] = MineApp.maxAge.toString()
            map["minAge"] = MineApp.minAge.toString()
        }
        mainDataSource.enqueue({ prePairList(map) }) {
            onSuccess {
                for (item in it) {
                    if (!userIdList.contains(item.otherUserId)) {
                        userIdList.add(item.otherUserId)
                        pairInfoList.add(item)
                    }
                }
                adapter.setDataList(pairInfoList)

                if (curIndex == -1) {
                    curIndex = 0
                    binding.swipeCardsView.setAdapter(adapter)
                } else
                    binding.swipeCardsView.notifyDatasetChanged(curIndex)
                binding.isProgressPlay = false
            }
        }
    }
}