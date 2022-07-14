package com.yimi.rentme.vm

import android.annotation.SuppressLint
import android.view.View
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.yimi.rentme.R
import com.yimi.rentme.activity.MemberDetailActivity
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.Reward
import com.yimi.rentme.databinding.AcRewardListBinding
import kotlinx.coroutines.Job
import org.jetbrains.anko.startActivity

class RewardListViewModel : BaseViewModel(), OnRefreshListener, OnLoadMoreListener {

    lateinit var binding: AcRewardListBinding
    var otherUserId = 0L
    var friendDynId = 0L
    lateinit var adapter: BaseAdapter<Reward>
    private val rewardList = ArrayList<Reward>()
    private var pageNo = 1

    override fun initViewModel() {
        binding.title = "礼物排行榜"
        binding.noData = false

        adapter = BaseAdapter(activity, R.layout.item_reward_list, rewardList, this)
        if (friendDynId != 0L) seeGiftRewards() else seeUserGiftRewards()
    }

    override fun back(view: View) {
        super.back(view)
        activity.finish()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRefresh(refreshLayout: RefreshLayout) {
        binding.refresh.setEnableLoadMore(true)
        pageNo = 1
        rewardList.clear()
        adapter.notifyDataSetChanged()
        if (friendDynId != 0L) seeGiftRewards() else seeUserGiftRewards()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNo++
        if (friendDynId != 0L) seeGiftRewards() else seeUserGiftRewards()
    }

    /**
     * 用户详情
     */
    fun toMemberDetail(reward: Reward) {
        activity.startActivity<MemberDetailActivity>(
            Pair("otherUserId", reward.userId)
        )
    }

    /**
     * 礼物打赏
     */
    private fun seeGiftRewards() {
        if (pageNo == 1)
            showLoading(Job(), "获取打赏列表...")
        mainDataSource.enqueue({ seeGiftRewards(friendDynId, 2, pageNo, 10) }) {
            onSuccess {
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                binding.noData = false
                dismissLoading()
                val start = rewardList.size
                rewardList.addAll(it)
                adapter.notifyItemRangeChanged(start, rewardList.size)
            }
            onFailed {
                binding.refresh.setEnableLoadMore(false)
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                dismissLoading()
                if (it.isNoData)
                    binding.noData = rewardList.size == 0
            }
        }
    }

    /**
     * 礼物打赏
     */
    private fun seeUserGiftRewards() {
        if (pageNo == 1)
            showLoading(Job(), "获取打赏列表...")
        mainDataSource.enqueue({ seeUserGiftRewards(otherUserId, 2, pageNo, 10) }) {
            onSuccess {
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                binding.noData = false
                dismissLoading()
                val start = rewardList.size
                rewardList.addAll(it)
                adapter.notifyItemRangeChanged(start, rewardList.size)
            }
            onFailed {
                binding.refresh.setEnableLoadMore(false)
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                if (it.isNoData)
                    binding.noData = rewardList.size == 0
                dismissLoading()
            }
        }
    }


}