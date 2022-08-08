package com.yimi.rentme.vm.bottle

import android.annotation.SuppressLint
import android.os.SystemClock
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.bottle.BottleChatActivity
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.BottleInfo
import com.yimi.rentme.databinding.AcBottleListBinding
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.RemindDF
import com.zb.baselibs.utils.DateUtil
import com.zb.baselibs.utils.ObjectUtils
import com.zb.baselibs.utils.dip2px
import com.zb.baselibs.utils.getLong
import com.zb.baselibs.views.touch.SimpleItemTouchHelperCallback
import kotlinx.coroutines.Job
import org.jetbrains.anko.startActivity
import org.simple.eventbus.EventBus
import java.util.*

class BottleListViewModel : BaseViewModel(), OnRefreshListener {

    lateinit var binding: AcBottleListBinding
    lateinit var adapter: BaseAdapter<BottleInfo>
    private val bottleInfoList = ArrayList<BottleInfo>()
    var isBlur = true
    private val comparator = BottleInfoComparator()

    override fun initViewModel() {
        adapter = BaseAdapter(activity, R.layout.item_bottle_list, bottleInfoList, this)
        val callback = SimpleItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.bottleList)
        callback.setSort(false)
        callback.setSwipeEnabled(true)
        callback.setSwipeFlags(ItemTouchHelper.START or ItemTouchHelper.END)
        callback.setDragFlags(0)

        val height: Int = BaseApp.context.dip2px(82f) - ObjectUtils.getViewSizeByWidth(660f / 1125f)
        binding.appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            binding.showBg = verticalOffset <= height
        }
        isBlur = MineApp.mineInfo.memberType == 1
        myBottleList(1)
    }

    override fun back(view: View) {
        super.back(view)
        activity.finish()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRefresh(refreshLayout: RefreshLayout) {
        binding.refresh.setEnableLoadMore(true)
        bottleInfoList.clear()
        adapter.notifyDataSetChanged()
        myBottleList(1)
    }

    /**
     * 更新毛玻璃
     */
    fun updateBlur() {
        isBlur = false
        adapter.notifyItemRangeChanged(0, bottleInfoList.size)
    }

    /**
     * 更新单个漂流瓶
     */
    fun singleBottle(driftBottleId: Long) {
        var hasBottle = false
        var position = 0
        for (i in 0 until bottleInfoList.size) {
            if (bottleInfoList[i].driftBottleId == driftBottleId) {
                hasBottle = true
                position = i
                break
            }
        }
        if (hasBottle) {
            BaseApp.fixedThreadPool.execute {
                val chatListInfo =
                    MineApp.chatListDaoManager.getChatListInfo("drift_${driftBottleId}")
                bottleInfoList[position].noReadNum = chatListInfo!!.noReadNum
                bottleInfoList[position].text = chatListInfo.stanza
                bottleInfoList[position].modifyTime = chatListInfo.creationDate
                activity.runOnUiThread {
                    Collections.sort(bottleInfoList, comparator)
                    adapter.notifyItemRangeChanged(0, bottleInfoList.size)
                }
            }

        } else {
            onRefresh(binding.refresh)
        }
    }

    /**
     * 去漂流瓶聊天
     */
    fun toBottleChat(bottleInfo: BottleInfo, position: Int) {
        bottleInfo.noReadNum = 0
        adapter.notifyItemChanged(position)
        BaseApp.fixedThreadPool.execute {
            MineApp.chatListDaoManager.updateNoReadNum(0, "drift_${bottleInfo.driftBottleId}")
            SystemClock.sleep(500L)
            val chatListInfoList = MineApp.chatListDaoManager.getChatListInfoList(2)
            MineApp.noReadBottleNum = 0
            for (item in chatListInfoList) {
                MineApp.noReadBottleNum += item.noReadNum
            }
            EventBus.getDefault().post("", "lobsterBottleNoReadNum")
        }

        activity.startActivity<BottleChatActivity>(
            Pair("msgChannelType", 2),
            Pair("driftBottleId", bottleInfo.driftBottleId)
        )
    }

    /**
     * 删除漂流瓶
     */
    fun deleteBottle(position: Int) {
        RemindDF(activity).setTitle("销毁漂流瓶").setContent("销毁后，你将与对方失去联系")
            .setSureName("销毁").setCallBack(object : RemindDF.CallBack {
                override fun sure() {
                    pickBottle(position)
                }

                override fun cancel() {
                    adapter.notifyItemRangeChanged(0, bottleInfoList.size)
                }
            }).show(activity.supportFragmentManager)
    }

    /**
     * 我的漂流瓶
     */
    private fun myBottleList(pageNo: Int) {
        if (pageNo == 1)
            showLoading(Job(), "获取我的漂流瓶列表...")
        mainDataSource.enqueue({ myBottleList(pageNo) }) {
            onSuccess {
                BaseApp.fixedThreadPool.execute {
                    for (bottleInfo in it) {
                        if (bottleInfo.destroyType == 1 && bottleInfo.userId == getLong("userId")) {
                            MineApp.chatListDaoManager.deleteChatListInfo("drift_${bottleInfo.driftBottleId}")
                            continue
                        }
                        if (bottleInfo.destroyType == 2 && bottleInfo.otherUserId == getLong("userId")) {
                            MineApp.chatListDaoManager.deleteChatListInfo("drift_${bottleInfo.driftBottleId}")
                            continue
                        }
                        if (bottleInfo.otherHeadImage.isEmpty()) {
                            bottleInfo.otherHeadImage = MineApp.mineInfo.image
                            bottleInfo.otherNick = MineApp.mineInfo.nick
                        }
                        val chatListInfo =
                            MineApp.chatListDaoManager.getChatListInfo("drift_${bottleInfo.driftBottleId}")
                        if (chatListInfo != null) {
                            bottleInfo.text = chatListInfo.stanza
                            bottleInfo.noReadNum = chatListInfo.noReadNum
                            bottleInfo.modifyTime = chatListInfo.creationDate
                        } else {
                            bottleInfo.modifyTime = bottleInfo.createTime
                        }
                        bottleInfoList.add(bottleInfo)
                    }
                    activity.runOnUiThread {
                        myBottleList(pageNo + 1)
                    }
                }
            }
            onFailed {
                dismissLoading()
                binding.refresh.setEnableLoadMore(false)
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                binding.noData = bottleInfoList.size == 0
                if (!binding.noData) {
                    Collections.sort(bottleInfoList, comparator)
                    adapter.notifyItemRangeChanged(0, bottleInfoList.size)
                    BaseApp.fixedThreadPool.execute {
                        val chatListInfoList = MineApp.chatListDaoManager.getChatListInfoList(2)
                        MineApp.noReadBottleNum = 0
                        for (item in chatListInfoList) {
                            MineApp.noReadBottleNum += item.noReadNum
                        }
                        EventBus.getDefault().post("", "lobsterBottleNoReadNum")
                    }
                }
            }
        }
    }

    /**+
     * 漂流瓶状态 .1.漂流中  2.被拾起  3.销毁
     */
    private fun pickBottle(position: Int) {
        mainDataSource.enqueueLoading(
            { pickBottle(bottleInfoList[position].driftBottleId, 3) },
            "销毁漂流瓶..."
        ) {
            onSuccess {
                val bottleInfo = bottleInfoList[position]
                val otherUserId =
                    if (bottleInfo.userId == getLong("userId")) bottleInfo.otherUserId else bottleInfo.userId
                bottleInfoList.removeAt(position)
                adapter.notifyItemRemoved(position)
                adapter.notifyItemRangeChanged(position, bottleInfoList.size - position)
                binding.noData = bottleInfoList.size == 0
                BaseApp.fixedThreadPool.execute {
                    MineApp.chatListDaoManager.deleteChatListInfo("drift_${bottleInfo.driftBottleId}")
                    MineApp.historyDaoManager.deleteHistoryInfo("drift_${bottleInfo.driftBottleId}")
                    SystemClock.sleep(500L)
                    val chatListInfoList = MineApp.chatListDaoManager.getChatListInfoList(2)
                    MineApp.noReadBottleNum = 0
                    for (item in chatListInfoList) {
                        MineApp.noReadBottleNum += item.noReadNum
                    }
                    EventBus.getDefault().post("", "lobsterBottleNoReadNum")
                    activity.runOnUiThread {
                        mainDataSource.enqueue({
                            clearAllDriftBottleHistoryMsg(
                                otherUserId, bottleInfo.driftBottleId
                            )
                        })
                    }
                }
            }
            onFailed { adapter.notifyItemRangeChanged(0, bottleInfoList.size) }
        }
    }

    class BottleInfoComparator : Comparator<BottleInfo?> {
        override fun compare(o1: BottleInfo?, o2: BottleInfo?): Int {
            if (o1 == null && o2 == null) {
                return 0
            }
            if (o1 == null) {
                return -1
            }
            if (o2 == null) {
                return 1
            }
            if (o1.modifyTime.isEmpty()) return -1

            return if (o2.modifyTime.isEmpty()) -1 else
                DateUtil.getDateCount(
                    o2.modifyTime, o1.modifyTime, DateUtil.yyyy_MM_dd_HH_mm_ss, 1000f
                )
        }
    }
}