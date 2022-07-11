package com.yimi.rentme.vm.fragment

import android.annotation.SuppressLint
import android.view.View
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.DiscoverDetailActivity
import com.yimi.rentme.activity.MemberDetailActivity
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.DiscoverInfo
import com.yimi.rentme.databinding.FragFollowBinding
import com.yimi.rentme.roomdata.FollowInfo
import com.yimi.rentme.roomdata.GoodInfo
import com.yimi.rentme.utils.PicSizeUtil
import com.yimi.rentme.views.GoodView
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.getLong
import kotlinx.coroutines.Job
import org.jetbrains.anko.startActivity

class FollowViewModel : BaseViewModel(), OnRefreshListener, OnLoadMoreListener {

    lateinit var binding: FragFollowBinding
    lateinit var adapter: BaseAdapter<DiscoverInfo>
    private val discoverInfoList = ArrayList<DiscoverInfo>()
    private var pageNo = 1
    private var prePosition = -1
    private lateinit var discoverInfo: DiscoverInfo
    private var friendDynId = 0L
    private var followInfo: FollowInfo? = null

    override fun initViewModel() {
        adapter = BaseAdapter(activity, R.layout.item_follow_discover, discoverInfoList, this)
        attentionDyn()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRefresh(refreshLayout: RefreshLayout) {
        binding.refresh.setEnableLoadMore(true)
        pageNo = 1
        discoverInfoList.clear()
        adapter.notifyDataSetChanged()
        attentionDyn()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNo++
        attentionDyn()
    }

    /**
     * 刷新
     */
    fun updateData(view: View) {
        onRefresh(binding.refresh)
    }

    /**
     * 我关注的动态列表
     */
    private fun attentionDyn() {
        if (pageNo == 1)
            showLoading(Job(), "加载动态...")
        mainDataSource.enqueue({ attentionDyn(pageNo, 1) }) {
            onSuccess {
                binding.noData = false
                binding.noWifi = false
                for (item in it) {
                    BaseApp.fixedThreadPool.execute {
                        followInfo = MineApp.followDaoManager.getFollowInfo(item.otherUserId)
                        if (followInfo != null) {
                            item.nick = followInfo!!.nick
                            item.image = followInfo!!.image
                        }
                        val url = item.videoUrl.ifEmpty {
                            if (item.images.isEmpty())
                                item.image
                            else
                                item.images.split(",")[0]
                        }
                        if (url.contains(".mp4")) {
                            val start = discoverInfoList.size
                            discoverInfoList.add(item)
                            adapter.notifyItemRangeChanged(start, discoverInfoList.size)
                        } else {
                            PicSizeUtil.getPicSize(
                                activity, url, object : PicSizeUtil.OnPicListener {
                                    override fun onImageSize(width: Int, height: Int) {
                                        val start = discoverInfoList.size
                                        item.width = width
                                        item.height = height
                                        discoverInfoList.add(item)
                                        if (followInfo != null) {
                                            activity.runOnUiThread {
                                                adapter.notifyItemRangeChanged(
                                                    start, discoverInfoList.size
                                                )
                                            }
                                        } else {
                                            activity.runOnUiThread {
                                                otherInfo(start, item.otherUserId)
                                            }
                                        }
                                    }
                                })
                        }
                    }
                }
                dismissLoading()
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
            }
            onFailed {
                dismissLoading()
                binding.refresh.setEnableLoadMore(false)
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                binding.noWifi = it.isNoWIFI
                if (it.isNoData) {
                    binding.noData = discoverInfoList.size == 0
                }
            }
        }
    }

    /**
     * 保存关注信息
     */
    private fun otherInfo(start: Int, otherUserId: Long) {
        mainDataSource.enqueue({ otherInfo(otherUserId) }) {
            onSuccess {
                BaseApp.fixedThreadPool.execute {
                    val followInfo = FollowInfo()
                    followInfo.image = it.image
                    followInfo.images = it.moreImages.ifEmpty { it.singleImage }
                    followInfo.nick = it.nick
                    followInfo.otherUserId = otherUserId
                    followInfo.mainUserId = getLong("userId")
                    MineApp.followDaoManager.insert(followInfo)
                    adapter.notifyItemRangeChanged(start, discoverInfoList.size)
                }
            }
        }
    }

    /**
     * 跳至动态详情
     */
    fun toDiscoverDetail(position: Int) {
        activity.startActivity<DiscoverDetailActivity>(
            Pair("friendDynId", discoverInfoList[position].friendDynId)
        )
    }

    /**.
     * 跳至用户详情
     */
    fun toMemberDetail(position: Int) {
        activity.startActivity<MemberDetailActivity>(
            Pair("otherUserId", discoverInfoList[position].otherUserId)
        )
    }

    /**
     * 点赞
     */
    fun doLike(view: View, position: Int) {
        prePosition = position
        discoverInfo = discoverInfoList[position]
        friendDynId = discoverInfo.friendDynId
        val goodView = view as GoodView
        BaseApp.fixedThreadPool.execute {
            val goodInfo = MineApp.goodDaoManager.getGood(friendDynId)
            activity.runOnUiThread {
                if (goodInfo == null) {
                    goodView.playLike()
                    dynDoLike()
                }
            }
        }
    }

    /**
     * 点赞
     */
    private fun dynDoLike() {
        mainDataSource.enqueue({ dynDoLike(friendDynId) }) {
            onSuccess {
                val goodInfo = GoodInfo()
                goodInfo.friendDynId = friendDynId
                goodInfo.mainUserId = getLong("userId")
                BaseApp.fixedThreadPool.execute {
                    MineApp.goodDaoManager.insert(goodInfo)
                    discoverInfo.goodNum = discoverInfo.goodNum + 1
                    discoverInfo.isLike = true
                    activity.runOnUiThread {
                        adapter.notifyItemChanged(prePosition)
                    }
                }
            }
            onFailToast { false }
            onFailed {
                if (it.errorMessage == "已经赞过了") {
                    val goodInfo = GoodInfo()
                    goodInfo.friendDynId = friendDynId
                    goodInfo.mainUserId = getLong("userId")
                    BaseApp.fixedThreadPool.execute {
                        MineApp.goodDaoManager.insert(goodInfo)
                        discoverInfo.isLike = true
                        activity.runOnUiThread {
                            adapter.notifyItemChanged(prePosition)
                        }
                    }
                }
            }
        }
    }
}