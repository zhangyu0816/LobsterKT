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
import com.yimi.rentme.bean.MemberInfo
import com.yimi.rentme.databinding.FragMemberDiscoverBinding
import com.yimi.rentme.roomdata.GoodInfo
import com.yimi.rentme.utils.PicSizeUtil
import com.yimi.rentme.views.GoodView
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.getLong
import kotlinx.coroutines.Job
import org.jetbrains.anko.startActivity

class MemberDiscoverViewModel : BaseViewModel(), OnRefreshListener, OnLoadMoreListener {

    lateinit var binding: FragMemberDiscoverBinding
    var otherUserId = 0L // 用户Id
    lateinit var adapter: BaseAdapter<DiscoverInfo>
    private val discoverInfoList = ArrayList<DiscoverInfo>()
    private var pageNo = 1
    private var updateTop = false
    private lateinit var memberInfo: MemberInfo
    private var prePosition = -1
    private lateinit var discoverInfo: DiscoverInfo
    private var friendDynId = 0L

    override fun initViewModel() {
        adapter = BaseAdapter(activity, R.layout.item_member_discover, discoverInfoList, this)
        when (otherUserId) {
            0L -> dynPiazzaList()
            1L -> {
                binding.noDataRes = R.mipmap.my_no_discover_data
                personOtherDyn()
            }
            else ->{
                binding.noDataRes = R.mipmap.other_no_discover_data
                otherInfo()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRefresh(refreshLayout: RefreshLayout) {
        if (otherUserId != 0L) {
            binding.refresh.setEnableLoadMore(true)
            pageNo = 1
            discoverInfoList.clear()
            adapter.notifyDataSetChanged()
            personOtherDyn()
        } else {
            updateTop = true
            pageNo++
            dynPiazzaList()
        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        updateTop = false
        pageNo++
        when (otherUserId) {
            0L -> dynPiazzaList()
            else -> personOtherDyn()
        }
    }

    /**
     * 刷新
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(view: View) {
        binding.refresh.setEnableLoadMore(true)
        pageNo = 1
        discoverInfoList.clear()
        adapter.notifyDataSetChanged()
        when (otherUserId) {
            0L -> dynPiazzaList()
            else -> personOtherDyn()
        }
    }

    /**
     * 推荐动态
     */
    private fun dynPiazzaList() {
        if (pageNo == 1)
            showLoading(Job(), "获取动态...")
        val map = HashMap<String, String>()
        map["cityId"] = MineApp.cityId.toString()
        map["pageNo"] = pageNo.toString()
        map["dynType"] = "1"
        map["sex"] = if (MineApp.sex == 0) "1" else "0"
        mainDataSource.enqueue({ dynPiazzaList(map) }) {
            onSuccess {
                updateDiscoverInfoList(it)
            }
            onFailed {
                dismissLoading()
                binding.refresh.setEnableLoadMore(false)
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                binding.noWifi = it.isNoWIFI
            }
        }
    }

    /**
     * 用户信息
     */
    private fun otherInfo() {
        mainDataSource.enqueue({ otherInfo(otherUserId) }) {
            onSuccess {
                memberInfo = it
                personOtherDyn()
            }
            onFailed {
                binding.noWifi = it.isNoWIFI
            }
        }
    }

    /**
     * 个人动态
     */
    private fun personOtherDyn() {
        if (pageNo == 1)
            showLoading(Job(), "获取动态...")
        val map = HashMap<String, String>()
        map["otherUserId"] =
            if (otherUserId == 1L) getLong("userId").toString() else otherUserId.toString()
        map["pageNo"] = pageNo.toString()
        map["timeSortType"] = "1"
        map["dycRootType"] = "2"

        mainDataSource.enqueue({ personOtherDyn(map) }) {
            onSuccess {
                updateDiscoverInfoList(it)
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
     * 更新动态
     */
    private fun updateDiscoverInfoList(it: ArrayList<DiscoverInfo>) {
        binding.noData = false
        binding.noWifi = false
        if (otherUserId != 0L) {
            for (item in it) {
                if (otherUserId == 1L) {
                    item.nick = MineApp.mineInfo.nick
                    item.image = MineApp.mineInfo.image
                } else {
                    item.nick = memberInfo.nick
                    item.image = memberInfo.image
                }
            }
        }
        if (updateTop) {
            it.reverse()
            for (item in it) {
                val url = if (item.images.isEmpty()) item.image else item.images.split(",")[0]
                PicSizeUtil.getPicSize(activity, url, object : PicSizeUtil.OnPicListener {
                    override fun onImageSize(width: Int, height: Int) {
                        item.width = width
                        item.height = height
                        discoverInfoList.add(0, item)
                        BaseApp.fixedThreadPool.execute {
                            item.isLike = MineApp.goodDaoManager.getGood(item.friendDynId) != null
                            activity.runOnUiThread {
                                adapter.notifyItemRangeChanged(0, discoverInfoList.size)
                            }
                        }
                    }
                })
            }
        } else {
            for (item in it) {
                val url = if (item.images.isEmpty()) item.image else item.images.split(",")[0]
                PicSizeUtil.getPicSize(activity, url, object : PicSizeUtil.OnPicListener {
                    override fun onImageSize(width: Int, height: Int) {
                        val start = discoverInfoList.size
                        item.width = width
                        item.height = height
                        discoverInfoList.add(item)
                        BaseApp.fixedThreadPool.execute {
                            item.isLike = MineApp.goodDaoManager.getGood(item.friendDynId) != null
                            activity.runOnUiThread {
                                adapter.notifyItemRangeChanged(start, discoverInfoList.size)
                            }
                        }
                    }
                })
            }
        }

        dismissLoading()
        binding.refresh.finishRefresh()
        binding.refresh.finishLoadMore()
    }

    /**
     * 跳至动态详情
     */
    fun toDiscoverDetail(position: Int) {
        activity.startActivity<MemberDetailActivity>(
            Pair("otherUserId", discoverInfoList[position].userId)
        )
    }

    /**.
     * 跳至用户详情
     */
    fun toMemberDetail(position: Int) {
        activity.startActivity<DiscoverDetailActivity>(
            Pair("friendDynId", discoverInfoList[position].friendDynId)
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
                } else if (otherUserId != 0L) {
                    goodView.playUnlike()
                    dynCancelLike()
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

    /**
     * 取消点赞
     */
    private fun dynCancelLike() {
        mainDataSource.enqueue({ dynCancelLike(friendDynId) }) {
            onSuccess {
                BaseApp.fixedThreadPool.execute {
                    MineApp.goodDaoManager.deleteGood(friendDynId)
                    discoverInfo.goodNum = discoverInfo.goodNum - 1
                    discoverInfo.isLike = false
                    activity.runOnUiThread {
                        adapter.notifyItemChanged(prePosition)
                    }
                }
            }
            onFailToast { false }
            onFailed {
                if (it.errorMessage == "已经取消过") {
                    MineApp.goodDaoManager.deleteGood(friendDynId)
                    discoverInfo.isLike = false
                    activity.runOnUiThread {
                        adapter.notifyItemChanged(prePosition)
                    }
                }
            }
        }
    }
}