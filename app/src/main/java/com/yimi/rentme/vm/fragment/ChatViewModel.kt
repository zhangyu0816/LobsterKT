package com.yimi.rentme.vm.fragment

import androidx.recyclerview.widget.ItemTouchHelper
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.databinding.FragChatBinding
import com.yimi.rentme.roomdata.ChatListInfo
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.DateUtil
import com.zb.baselibs.views.touch.SimpleItemTouchHelperCallback
import org.simple.eventbus.EventBus
import java.util.*

class ChatViewModel : BaseViewModel(), OnRefreshListener {

    lateinit var binding: FragChatBinding
    lateinit var adapter: BaseAdapter<ChatListInfo>
    private val chatListInfoList = ArrayList<ChatListInfo>()

    override fun initViewModel() {
        BaseApp.fixedThreadPool.execute {
            chatListInfoList.addAll(MineApp.chatListDaoManager.getChatListInfoListByChatType(5))
            chatListInfoList.addAll(MineApp.chatListDaoManager.getChatListInfoListByChatType(6))
            MineApp.chatListDaoManager.getChatListInfoListByChatType(4).forEach {
                var has = false
                for (item in MineApp.pairList) {
                    if (item.otherUserId == it.otherUserId) {
                        has = true
                        break
                    }
                }
                if (has) chatListInfoList.add(it)
            }
            activity.runOnUiThread {
                Collections.sort(chatListInfoList, ChatComparator())
                adapter = BaseAdapter(activity, R.layout.item_chat_list, chatListInfoList, this)
                EventBus.getDefault().post("", "lobsterUpdateTabRed")
                val callback = SimpleItemTouchHelperCallback(adapter)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(binding.chatList)
                callback.setSort(false)
                callback.setSwipeEnabled(true)
                callback.setSwipeFlags(ItemTouchHelper.START or ItemTouchHelper.END)
                callback.setDragFlags(0)
                binding.refresh.setEnableLoadMore(false)
            }
        }

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        EventBus.getDefault().post("", "lobsterNewDynMsgAllNum")
    }

    private class ChatComparator : Comparator<ChatListInfo> {
        override fun compare(o1: ChatListInfo?, o2: ChatListInfo?): Int {
            if (o1 == null && o2 == null) {
                return 0
            }
            if (o1 == null) {
                return -1
            }
            if (o2 == null) {
                return 1
            }
            if (o1.creationDate.isEmpty()) return -1
            return if (o2.creationDate.isEmpty()) -1
            else
                DateUtil.getDateCount(
                    o2.creationDate, o1.creationDate,
                    DateUtil.yyyy_MM_dd_HH_mm_ss, 1000f
                )
        }
    }
}