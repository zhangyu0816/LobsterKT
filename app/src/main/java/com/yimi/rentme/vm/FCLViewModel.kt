package com.yimi.rentme.vm

import android.annotation.SuppressLint
import android.view.View
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.MemberDetailActivity
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.MemberInfo
import com.yimi.rentme.databinding.AcFclBinding
import com.yimi.rentme.dialog.SuperLikeDF
import com.yimi.rentme.dialog.VipAdDF
import com.yimi.rentme.roomdata.FollowInfo
import com.yimi.rentme.roomdata.LikeTypeInfo
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.RemindDF
import com.zb.baselibs.utils.SCToastUtil
import com.zb.baselibs.utils.getLong
import kotlinx.coroutines.Job
import org.jetbrains.anko.startActivity
import org.simple.eventbus.EventBus

class FCLViewModel : BaseViewModel(), OnRefreshListener, OnLoadMoreListener {

    lateinit var binding: AcFclBinding
    var index = 0 // 0 关注  1 粉丝  2 喜欢
    var otherUserId = 0L
    lateinit var adapter: BaseAdapter<MemberInfo>
    private val memberInfoList = ArrayList<MemberInfo>()
    private var pageNo = 1
    private var _selectIndex = -1

    override fun initViewModel() {
        binding.title =
            if (index == 0) if (otherUserId == 0L) "我的关注" else "Ta的关注" else if (index == 1) if (otherUserId == 0L) "我的粉丝" else "TA的粉丝" else if (index == 2) "谁喜欢我" else "谁看过我"
        binding.isVip = MineApp.mineInfo.memberType == 2
        binding.noData = true
        binding.dataSize = 0
        adapter = BaseAdapter(activity, R.layout.item_fcl_member, memberInfoList, this)
        adapter.userIdList.clear()
    }

    override fun back(view: View) {
        super.back(view)
        activity.finish()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRefresh(refreshLayout: RefreshLayout) {
        // 下拉刷新
        binding.refresh.setEnableLoadMore(true)
        pageNo = 1
        memberInfoList.clear()
        adapter.notifyDataSetChanged()
        adapter.userIdList.clear()
        getData()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        // 上拉加载更多
        if (index == 3 && MineApp.mineInfo.memberType == 1) {
            binding.refresh.finishLoadMore()
            return
        }
        pageNo++
        getData()
    }

    private fun getData() {
        if (index == 0) {
            if (otherUserId == 0L) myConcerns() else otherConcerns()
        } else if (index == 1) {
            if (otherUserId == 0L) myFans() else otherFans()
        } else if (index == 2) {
            likeMeList()
        } else {
            visitorBySeeMeList()
        }
    }

    /**
     * 用户详情
     */
    fun toMemberDetail(memberInfo: MemberInfo, position: Int) {
        _selectIndex = position
        activity.startActivity<MemberDetailActivity>(
            Pair("otherUserId", memberInfo.userId)
        )
    }

    fun clickMember(memberInfo: MemberInfo, position: Int) {
        _selectIndex = position
        val otherUserId = memberInfo.userId
        if (index == 2) {
            // 被喜欢
            BaseApp.fixedThreadPool.execute {
                if (MineApp.likeTypeDaoManager.getLikeTypeInfo(otherUserId) == null) {
                    activity.runOnUiThread {
                        makeEvaluate(otherUserId)
                    }
                } else {
                    activity.runOnUiThread {
                        RemindDF(activity).setTitle("解除匹配关系").setContent("解除匹配关系后，将对方移除匹配列表及聊天列表。")
                            .setSureName("解除").setCallBack(object : RemindDF.CallBack {
                                override fun sure() {
                                    relievePair(otherUserId)
                                }
                            }).show(activity.supportFragmentManager)
                    }
                }
            }
        } else {
            // 我的关注  我的粉丝
            BaseApp.fixedThreadPool.execute {
                if (MineApp.followDaoManager.getFollowInfo(otherUserId) == null) {
                    activity.runOnUiThread {
                        attentionOther(otherUserId)
                    }
                } else {
                    activity.runOnUiThread {
                        cancelAttention(otherUserId)
                    }
                }
            }
        }
    }

    /**
     * 开通会员
     */
    fun openVip(view: View) {
        VipAdDF(activity).setType(8).setMainDataSource(mainDataSource)
            .show(activity.supportFragmentManager)
    }

    /**
     * 我关注的
     */
    private fun myConcerns() {
        if (pageNo == 1)
            showLoading(Job(), "我的关注...")
        mainDataSource.enqueue({ myConcerns(pageNo) }) {
            onSuccess {
                dismissLoading()
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                binding.noData = false

                for (item in it) {
                    BaseApp.fixedThreadPool.execute {
                        item.isFollow = true
                        item.hasLike =
                            MineApp.likeTypeDaoManager.getLikeTypeInfo(item.userId) != null
                        val start = memberInfoList.size
                        memberInfoList.add(item)
                        adapter.notifyItemRangeChanged(start, memberInfoList.size)

                        val followInfo = FollowInfo()
                        followInfo.image = item.image
                        followInfo.nick = item.nick
                        followInfo.otherUserId = item.userId
                        followInfo.mainUserId = getLong("userId")
                        MineApp.followDaoManager.insert(followInfo)
                    }
                }
            }
            onFailed {
                dismissLoading()
                binding.refresh.setEnableLoadMore(false)
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                if (it.isNoData)
                    binding.noData = memberInfoList.size == 0
            }
        }
    }

    /**
     * 别人的关注
     */
    private fun otherConcerns() {
        if (pageNo == 1)
            showLoading(Job(), "Ta的关注...")
        mainDataSource.enqueue({ otherConcerns(pageNo, otherUserId) }) {
            onSuccess {
                dismissLoading()
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                binding.noData = false
                for (item in it) {
                    BaseApp.fixedThreadPool.execute {
                        item.isFollow = true
                        item.hasLike =
                            MineApp.likeTypeDaoManager.getLikeTypeInfo(item.userId) != null
                        val start = memberInfoList.size
                        memberInfoList.add(item)
                        adapter.notifyItemRangeChanged(start, memberInfoList.size)

                        val followInfo = FollowInfo()
                        followInfo.image = item.image
                        followInfo.nick = item.nick
                        followInfo.otherUserId = item.userId
                        followInfo.mainUserId = getLong("userId")
                        MineApp.followDaoManager.insert(followInfo)
                    }
                }
            }
            onFailed {
                dismissLoading()
                binding.refresh.setEnableLoadMore(false)
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                if (it.isNoData)
                    binding.noData = memberInfoList.size == 0
            }
        }
    }

    /**
     * 我的粉丝
     */
    private fun myFans() {
        if (pageNo == 1)
            showLoading(Job(), "我的粉丝...")
        mainDataSource.enqueue({ myFans(pageNo) }) {
            onSuccess {
                dismissLoading()
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                binding.noData = false
                for (item in it) {
                    BaseApp.fixedThreadPool.execute {
                        item.isFollow = true
                        item.hasLike =
                            MineApp.likeTypeDaoManager.getLikeTypeInfo(item.userId) != null
                        val start = memberInfoList.size
                        memberInfoList.add(item)
                        adapter.notifyItemRangeChanged(start, memberInfoList.size)

                        val followInfo = FollowInfo()
                        followInfo.image = item.image
                        followInfo.nick = item.nick
                        followInfo.otherUserId = item.userId
                        followInfo.mainUserId = getLong("userId")
                        MineApp.followDaoManager.insert(followInfo)
                    }
                }
            }
            onFailed {
                dismissLoading()
                binding.refresh.setEnableLoadMore(false)
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                if (it.isNoData)
                    binding.noData = memberInfoList.size == 0
            }
        }
    }

    /**
     * 别人的粉丝
     */
    private fun otherFans() {
        if (pageNo == 1)
            showLoading(Job(), "Ta的粉丝...")
        mainDataSource.enqueue({ otherFans(pageNo, otherUserId) }) {
            onSuccess {
                dismissLoading()
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                binding.noData = false
                for (item in it) {
                    BaseApp.fixedThreadPool.execute {
                        item.isFollow = true
                        item.hasLike =
                            MineApp.likeTypeDaoManager.getLikeTypeInfo(item.userId) != null
                        val start = memberInfoList.size
                        memberInfoList.add(item)
                        adapter.notifyItemRangeChanged(start, memberInfoList.size)

                        val followInfo = FollowInfo()
                        followInfo.image = item.image
                        followInfo.nick = item.nick
                        followInfo.otherUserId = item.userId
                        followInfo.mainUserId = getLong("userId")
                        MineApp.followDaoManager.insert(followInfo)

                    }
                }
            }
            onFailed {
                dismissLoading()
                binding.refresh.setEnableLoadMore(false)
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                if (it.isNoData)
                    binding.noData = memberInfoList.size == 0
            }
        }
    }

    /**
     * 喜欢我的
     */
    private fun likeMeList() {
        if (pageNo == 1)
            showLoading(Job(), "喜欢我的...")
        val map = HashMap<String, String>()
        map["pageNo"] = pageNo.toString()
        mainDataSource.enqueue({ likeMeList(map) }) {
            onSuccess {
                dismissLoading()
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                binding.noData = false
                for (item in it) {
                    BaseApp.fixedThreadPool.execute {
                        val memberInfo = MemberInfo()
                        memberInfo.isFollow = true
                        memberInfo.hasLike =
                            MineApp.likeTypeDaoManager.getLikeTypeInfo(item.userId) != null
                        memberInfo.userId = item.userId
                        memberInfo.image = item.headImage
                        memberInfo.nick = item.nick
                        val start = memberInfoList.size
                        memberInfoList.add(memberInfo)
                        adapter.notifyItemRangeChanged(start, memberInfoList.size)

                        val followInfo = FollowInfo()
                        followInfo.image = item.headImage
                        followInfo.nick = item.nick
                        followInfo.otherUserId = item.userId
                        followInfo.mainUserId = getLong("userId")
                        MineApp.followDaoManager.insert(followInfo)
                    }
                }
            }
            onFailed {
                dismissLoading()
                binding.refresh.setEnableLoadMore(false)
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                if (it.isNoData)
                    binding.noData = memberInfoList.size == 0
            }
        }
    }

    /**
     * 看过我的
     */
    private fun visitorBySeeMeList() {
        if (pageNo == 1)
            showLoading(Job(), "看过我的...")
        mainDataSource.enqueue({ visitorBySeeMeList(pageNo) }) {
            onSuccess {
                dismissLoading()
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                binding.noData = false
                for (item in it) {
                    BaseApp.fixedThreadPool.execute {
                        item.isFollow = true
                        item.hasLike =
                            MineApp.likeTypeDaoManager.getLikeTypeInfo(item.userId) != null
                        val start = memberInfoList.size
                        memberInfoList.add(item)
                        adapter.notifyItemRangeChanged(start, memberInfoList.size)

                        val followInfo = FollowInfo()
                        followInfo.image = item.image
                        followInfo.nick = item.nick
                        followInfo.otherUserId = item.userId
                        followInfo.mainUserId = getLong("userId")
                        MineApp.followDaoManager.insert(followInfo)

                    }
                }
            }
            onFailed {
                dismissLoading()
                binding.refresh.setEnableLoadMore(false)
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                if (it.isNoData)
                    binding.noData = memberInfoList.size == 0
            }
        }
    }

    /**
     * 喜欢
     */
    private fun makeEvaluate(otherUserId: Long) {
        mainDataSource.enqueueLoading({ makeEvaluate(otherUserId, 1) }, "提交喜欢信息...") {
            onSuccess {
                // 1喜欢成功 2匹配成功 3喜欢次数用尽
                val myHead = MineApp.mineInfo.image
                val otherHead = memberInfoList[_selectIndex].image
                when (it) {
                    1 -> {
                        BaseApp.fixedThreadPool.execute {
                            if (MineApp.likeTypeDaoManager.getLikeTypeInfo(otherUserId) == null) {
                                val likeTypeInfo = LikeTypeInfo()
                                likeTypeInfo.likeType = 1
                                likeTypeInfo.otherUserId = otherUserId
                                likeTypeInfo.mainUserId = getLong("userId")
                                MineApp.likeTypeDaoManager.insert(likeTypeInfo)
                            } else {
                                MineApp.likeTypeDaoManager.updateLikeType(1, otherUserId)
                            }
                            activity.runOnUiThread {
                                memberInfoList[_selectIndex].hasLike = true
                                adapter.notifyItemChanged(_selectIndex)
                            }
                        }
                    }
                    2 -> {
                        SuperLikeDF(activity).setMyHead(myHead).setOtherHead(otherHead)
                            .setMySex(MineApp.mineInfo.sex)
                            .setOtherSex(memberInfoList[_selectIndex].sex)
                            .setOtherNick(memberInfoList[_selectIndex].nick)
                            .setCallBack(object : SuperLikeDF.CallBack {
                                override fun sure() {
//                                ActivityUtils.getChatActivity(discoverInfo.getUserId(), false)
                                }
                            })
                            .show(activity.supportFragmentManager)
                        BaseApp.fixedThreadPool.execute {
                            if (MineApp.likeTypeDaoManager.getLikeTypeInfo(otherUserId) == null) {
                                val likeTypeInfo = LikeTypeInfo()
                                likeTypeInfo.likeType = 1
                                likeTypeInfo.otherUserId = otherUserId
                                likeTypeInfo.mainUserId = getLong("userId")
                                MineApp.likeTypeDaoManager.insert(likeTypeInfo)
                            } else {
                                MineApp.likeTypeDaoManager.updateLikeType(1, otherUserId)
                            }
                            activity.runOnUiThread {
                                memberInfoList[_selectIndex].hasLike = true
                                adapter.notifyItemChanged(_selectIndex)
                            }
                        }
                        EventBus.getDefault().post("更新喜欢次数", "lobsterUpdateLikeCount")
                        EventBus.getDefault().post("更新匹配列表", "lobsterUpdatePairList")
                    }
                    3 -> {
                        VipAdDF(activity).setType(6).setMainDataSource(mainDataSource)
                            .show(activity.supportFragmentManager)
                        SCToastUtil.showToast(activity, "今日喜欢次数已用完", 2)
                    }
                    5 -> {
                        BaseApp.fixedThreadPool.execute {
                            if (MineApp.likeTypeDaoManager.getLikeTypeInfo(otherUserId) == null) {
                                val likeTypeInfo = LikeTypeInfo()
                                likeTypeInfo.likeType = 1
                                likeTypeInfo.otherUserId = otherUserId
                                likeTypeInfo.mainUserId = getLong("userId")
                                MineApp.likeTypeDaoManager.insert(likeTypeInfo)
                            } else {
                                MineApp.likeTypeDaoManager.updateLikeType(1, otherUserId)
                            }
                            activity.runOnUiThread {
                                memberInfoList[_selectIndex].hasLike = true
                                adapter.notifyItemChanged(_selectIndex)
                            }
                        }
                        SCToastUtil.showToast(activity, "你已喜欢过对方", 2)
                    }
                }
            }
        }
    }

    /**
     * 解除匹配
     */
    private fun relievePair(otherUserId: Long) {
        mainDataSource.enqueueLoading({ relievePair(otherUserId) }, "解除匹配...") {
            onSuccess {
                BaseApp.fixedThreadPool.execute {
                    MineApp.likeTypeDaoManager.deleteLikeTypeInfo(otherUserId)
                    activity.runOnUiThread {
                        memberInfoList.removeAt(_selectIndex)
                        adapter.notifyItemRemoved(_selectIndex)
                        adapter.notifyItemRangeChanged(
                            _selectIndex, memberInfoList.size - _selectIndex
                        )
                        EventBus.getDefault()
                            .post("${otherUserId},true", "lobsterRelievePair") // 解除匹配
                    }
                }
            }
        }
    }

    private fun attentionOther(otherUserId: Long) {
        mainDataSource.enqueue({ attentionOther(otherUserId) }) {
            onSuccess {
                BaseApp.fixedThreadPool.execute {
                    val memberInfo = memberInfoList[_selectIndex]
                    memberInfo.fansQuantity = memberInfo.fansQuantity + 1
                    memberInfo.isFollow = true

                    val followInfo = FollowInfo()
                    followInfo.image = memberInfo.image
                    followInfo.nick = memberInfo.nick
                    followInfo.otherUserId = otherUserId
                    followInfo.mainUserId = getLong("userId")
                    MineApp.followDaoManager.insert(followInfo)

                    activity.runOnUiThread {
                        adapter.notifyItemChanged(_selectIndex)
                    }
                }
                EventBus.getDefault().post(true, "lobsterUpdateFollow")
            }
            onFailToast { false }
            onFailed {
                if (it.errorMessage == "已经关注过") {
                    BaseApp.fixedThreadPool.execute {
                        val memberInfo = memberInfoList[_selectIndex]
                        memberInfo.isFollow = true

                        val followInfo = FollowInfo()
                        followInfo.image = memberInfo.image
                        followInfo.nick = memberInfo.nick
                        followInfo.otherUserId = otherUserId
                        followInfo.mainUserId = getLong("userId")
                        MineApp.followDaoManager.insert(followInfo)

                        activity.runOnUiThread {
                            adapter.notifyItemChanged(_selectIndex)
                        }
                    }
                    EventBus.getDefault().post(true, "lobsterUpdateFollow")
                }
            }
        }
    }

    /**
     * 取消关注
     */
    private fun cancelAttention(otherUserId: Long) {
        mainDataSource.enqueue({ cancelAttention(otherUserId) }) {
            onSuccess {
                BaseApp.fixedThreadPool.execute {
                    val memberInfo = memberInfoList[_selectIndex]
                    memberInfo.isFollow = false
                    memberInfo.fansQuantity = memberInfo.fansQuantity - 1

                    MineApp.followDaoManager.deleteFollowInfo(otherUserId)
                    activity.runOnUiThread {
                        adapter.notifyItemChanged(_selectIndex)
                    }
                }
                EventBus.getDefault().post(false, "lobsterUpdateFollow")
            }
            onFailToast { false }
            onFailed {
                if (it.errorMessage == "已经取消了") {
                    BaseApp.fixedThreadPool.execute {
                        val memberInfo = memberInfoList[_selectIndex]
                        memberInfo.isFollow = false

                        MineApp.followDaoManager.deleteFollowInfo(otherUserId)
                        activity.runOnUiThread {
                            adapter.notifyItemChanged(_selectIndex)
                        }
                    }
                    EventBus.getDefault().post(false, "lobsterUpdateFollow")
                }
            }
        }
    }
}