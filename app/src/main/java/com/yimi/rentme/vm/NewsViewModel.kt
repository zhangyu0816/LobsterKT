package com.yimi.rentme.vm

import android.annotation.SuppressLint
import android.view.View
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.DiscoverDetailActivity
import com.yimi.rentme.activity.MemberDetailActivity
import com.yimi.rentme.activity.VideoDetailActivity
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.MineNews
import com.yimi.rentme.databinding.AcNewsBinding
import kotlinx.coroutines.Job
import org.jetbrains.anko.startActivity
import org.simple.eventbus.EventBus

class NewsViewModel : BaseViewModel(), OnRefreshListener, OnLoadMoreListener {

    lateinit var binding: AcNewsBinding
    var reviewType = 0 // 1 评论  2 点赞  3 礼物
    lateinit var adapter: BaseAdapter<MineNews>
    private var pageNo = 1
    private val mineNewsList = ArrayList<MineNews>()

    override fun initViewModel() {
        binding.title = when (reviewType) {
            1 -> "我的消息"
            2 -> "我的点赞"
            else -> "我的礼物"
        }
        binding.noDataRes = when (reviewType) {
            1 -> R.mipmap.no_review_icon
            2 -> R.mipmap.no_good_icon
            else -> R.mipmap.no_gift_icon
        }

        adapter = BaseAdapter(activity, R.layout.item_news, mineNewsList, this)
        readNewDynMsgAll()
        dynNewMsgList()
    }

    override fun back(view: View) {
        super.back(view)
        activity.finish()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRefresh(refreshLayout: RefreshLayout) {
        binding.refresh.setEnableLoadMore(true)
        mineNewsList.clear()
        adapter.notifyDataSetChanged()
        pageNo = 1
        dynNewMsgList()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNo++
        dynNewMsgList()
    }

    /**
     * 更新
     */
    fun updateData(view: View) {
        onRefresh(binding.refresh)
    }

    /**
     * 用户详情
     */
    fun toMemberDetail(mineNews: MineNews) {
        activity.startActivity<MemberDetailActivity>(
            Pair("otherUserId", mineNews.reviewUserId)
        )
    }

    /**
     * 动态详情
     */
    fun toDiscoverDetail(mineNews: MineNews) {
        if (mineNews.friendDynamicDycType >= 4)
            activity.startActivity<VideoDetailActivity>(
                Pair("friendDynId", mineNews.friendDynamicId)
            )
        else
            activity.startActivity<DiscoverDetailActivity>(
                Pair("friendDynId", mineNews.friendDynamicId)
            )
    }

    /**
     * 已读
     */
    private fun readNewDynMsgAll() {
        mainDataSource.enqueue({ readNewDynMsgAll(reviewType) }) {
            onSuccess {
                //1评论  2.点赞 3.礼物
                when (reviewType) {
                    1 -> MineApp.newsCount.friendDynamicReviewNum = 0
                    2 -> MineApp.newsCount.friendDynamicGoodNum = 0
                    else -> MineApp.newsCount.friendDynamicGiftNum = 0
                }
                EventBus.getDefault().post("", "lobsterNewsCount")
            }
        }
    }

    /**
     * 数据
     */
    private fun dynNewMsgList() {
        if (pageNo == 1)
            showLoading(Job(), "获取数据...")
        mainDataSource.enqueue({ dynNewMsgList(pageNo, reviewType) }) {
            onSuccess {
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                val start = mineNewsList.size
                mineNewsList.addAll(it)
                adapter.notifyItemRangeChanged(start, mineNewsList.size)
                binding.noData = false
                binding.noWifi = false
            }
            onFailed {
                binding.noWifi = it.isNoWIFI
                binding.refresh.setEnableLoadMore(false)
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                dismissLoading()
                if (it.isNoData)
                    binding.noData = mineNewsList.size == 0
            }
        }
    }
}