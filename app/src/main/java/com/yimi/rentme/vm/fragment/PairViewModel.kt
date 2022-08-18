package com.yimi.rentme.vm.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.os.SystemClock
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.FCLActivity
import com.yimi.rentme.activity.MemberDetailActivity
import com.yimi.rentme.activity.SelectImageActivity
import com.yimi.rentme.activity.bottle.BottleListActivity
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.databinding.FragPairBinding
import com.yimi.rentme.dialog.SelectorDF
import com.yimi.rentme.dialog.VipAdDF
import com.yimi.rentme.roomdata.ChatListInfo
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.RemindDF
import com.zb.baselibs.utils.DateUtil
import com.zb.baselibs.utils.getInteger
import com.zb.baselibs.utils.getLong
import com.zb.baselibs.utils.permission.requestPermissionsForResult
import com.zb.baselibs.utils.saveInteger
import com.zb.baselibs.views.touch.SimpleItemTouchHelperCallback
import org.jetbrains.anko.startActivity
import java.util.*

class PairViewModel : BaseViewModel(), OnRefreshListener {

    lateinit var binding: FragPairBinding
    lateinit var adapter: BaseAdapter<ChatListInfo>
    private val chatListInfoList = ArrayList<ChatListInfo>()
    private val chatType4List = ArrayList<ChatListInfo>()
    private var dataList = ArrayList<String>()

    override fun initViewModel() {
        dataList.add("发布照片")
        dataList.add("发布小视频")
        adapter = BaseAdapter(activity, R.layout.item_chat_pair, chatListInfoList, this)
        val callback = SimpleItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.pairList)
        callback.setSort(false)
        callback.setSwipeEnabled(true)
        callback.setSwipeFlags(ItemTouchHelper.START or ItemTouchHelper.END)
        callback.setDragFlags(0)
        binding.refresh.setEnableLoadMore(false)

        BaseApp.fixedThreadPool.execute {
            SystemClock.sleep(300L)
            activity.runOnUiThread {
                onRefresh(binding.refresh)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRefresh(refreshLayout: RefreshLayout) {
        chatListInfoList.clear()
        chatType4List.clear()
        adapter.notifyDataSetChanged()
        BaseApp.fixedThreadPool.execute {
            chatListInfoList.addAll(MineApp.chatListDaoManager.getChatListInfoListByChatType(2))
            chatListInfoList.addAll(MineApp.chatListDaoManager.getChatListInfoListByChatType(1))
            activity.runOnUiThread {
                personOtherDyn()
            }
        }
    }

    /**
     * 选择聊天
     */
    fun selectChat(chatListInfo: ChatListInfo, position: Int) {
        if (chatListInfo.chatType == 1) {
            // 喜欢我
            if (MineApp.mineInfo.memberType == 2) {
                saveInteger("beLikeCount_${getLong("userId")}", MineApp.contactNum.beLikeCount)
                BaseApp.fixedThreadPool.execute {
                    MineApp.chatListDaoManager.updateHasNewBeLike(
                        false,
                        "common_${MineApp.likeUserId}"
                    )
                }
                chatListInfo.hasNewBeLike = false
                adapter.notifyItemChanged(position)
                activity.startActivity<FCLActivity>(
                    Pair("index", 2),
                    Pair("otherUserId", 0)
                )
                return
            }
            VipAdDF(activity).setType(4).setMainDataSource(mainDataSource)
                .show(activity.supportFragmentManager)
        } else if (chatListInfo.chatType == 2) {
            // 漂流瓶
            activity.startActivity<BottleListActivity>()
        } else if (chatListInfo.chatType == 3) {
            // 超级喜欢
            activity.startActivity<MemberDetailActivity>(
                Pair("otherUserId", chatListInfo.otherUserId)
            )
        } else if (chatListInfo.chatType == 4) {
            // 匹配-聊天
//            ActivityUtils.getChatActivity(chatListInfo.getUserId(), false)
        } else if (chatListInfo.chatType == 10) {
            if (checkPermissionGranted(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
            ) {
                toSelectImage()
            } else {
                if (getInteger("image_permission", 0) == 0) {
                    saveInteger("image_permission", 1)
                    RemindDF(activity).setTitle("权限说明")
                        .setContent(
                            "在使用发布动态功能，包括图文、视频时，我们将会申请相机、存储、麦克风权限：" +
                                    "\n 1、申请相机权限--发布动态时获取拍摄照片，录制视频功能，" +
                                    "\n 2、申请存储权限--发布动态时获取保存和读取图片、视频，" +
                                    "\n 3、申请麦克风权限--发布视频动态时获取录制视频音频功能，" +
                                    "\n 4、若您点击“同意”按钮，我们方可正式申请上述权限，以便正常发布图文动态、视频动态，" +
                                    "\n 5、若您点击“拒绝”按钮，我们将不再主动弹出该提示，您也无法使用发布动态功能，不影响使用其他的虾菇功能/服务，" +
                                    "\n 6、您也可以通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭相机、存储、麦克风权限。"
                        ).setSureName("同意").setCancelName("拒绝")
                        .setCallBack(object : RemindDF.CallBack {
                            override fun sure() {
                                toSelectImage()
                            }
                        }).show(activity.supportFragmentManager)
                } else {
                    Toast.makeText(
                        activity,
                        "可通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭相机权限、存储权限、麦克风权限。",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * 选择发布动态
     */
    private fun toSelectImage() {
        launchMain {
            activity.requestPermissionsForResult(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO, rationale = "为了更好的提供服务，需要获取相机权限、存储权限、麦克风权限"
            )
            SelectorDF(activity).setDataList(dataList).setCallBack(object : SelectorDF.CallBack {
                override fun sure(position: Int) {
                    if (position == 0) {
                        activity.startActivity<SelectImageActivity>(
                            Pair("isMore", true),
                            Pair("isPublish", true)
                        )
                    } else {
                        activity.startActivity<SelectImageActivity>(
                            Pair("showVideo", true),
                            Pair("isPublish", true)
                        )
                    }
                }
            }).show(activity.supportFragmentManager)
        }
    }

    /**
     * 个人视频动态
     */
    fun personOtherDyn() {
        val map = HashMap<String, String>()
        map["otherUserId"] = getLong("userId").toString()
        map["pageNo"] = "1"
        map["timeSortType"] = "1"
        map["dycRootType"] = "0"

        mainDataSource.enqueue({ personOtherDyn(map) }) {
            onSuccess {
                val day = DateUtil.getDateCount(
                    DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss),
                    it[0].createTime, DateUtil.yyyy_MM_dd_HH_mm_ss, 1000f * 3600f * 24f
                )
                if (day > 1) {
                    setDiscoverChat()
                }
                MineApp.pairList.clear()
                beSuperLikeList()
            }
            onFailed {
                setDiscoverChat()
                MineApp.pairList.clear()
                beSuperLikeList()
            }
        }
    }

    /**
     * 显示动态聊天
     */
    private fun setDiscoverChat() {
        val start = chatListInfoList.size
        val chatListInfo = ChatListInfo()
        chatListInfo.chatId = "common_${MineApp.systemUserId}"
        chatListInfo.otherUserId = MineApp.systemUserId
        chatListInfo.nick = "虾菇"
        chatListInfo.image = "ic_chat_xiagu"
        chatListInfo.creationDate = DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss)
        chatListInfo.stanza = "好久没发布了，期待着你更新照片和视频哦！"
        chatListInfo.msgType = 1
        chatListInfo.noReadNum = 0
        chatListInfo.publicTag = ""
        chatListInfo.effectType = 1
        chatListInfo.authType = 1
        chatListInfo.msgChannelType = 1
        chatListInfo.showChat = false
        chatListInfo.chatType = 10
        chatListInfo.mainUserId = getLong("userId")
        chatListInfoList.add(chatListInfo)
        adapter.notifyItemRangeChanged(start, chatListInfoList.size)
    }

    /**
     * 超级喜欢我的
     */
    private fun beSuperLikeList() {
        val map = HashMap<String, String>()
        map["likeOtherStatus"] = "2"
        mainDataSource.enqueue({ likeMeList(map) }) {
            onSuccess {
                val start = chatListInfoList.size
                for (item in it) {
                    val chatListInfo = ChatListInfo()
                    chatListInfo.chatId = "common_${item.userId}"
                    chatListInfo.otherUserId = item.userId
                    chatListInfo.nick = item.nick
                    chatListInfo.image = item.headImage
                    chatListInfo.creationDate = item.modifyTime
                    chatListInfo.stanza = "超级喜欢你！"
                    chatListInfo.msgType = 1
                    chatListInfo.noReadNum = 0
                    chatListInfo.publicTag = ""
                    chatListInfo.effectType = 1
                    chatListInfo.authType = 1
                    chatListInfo.msgChannelType = 1
                    chatListInfo.showChat = false
                    chatListInfo.chatType = 3
                    chatListInfo.mainUserId = getLong("userId")
                    chatListInfoList.add(chatListInfo)
                }
                adapter.notifyItemRangeChanged(start, chatListInfoList.size)
                pairList(1)
            }
            onFailed {
                pairList(1)
            }
        }
    }

    /**
     * 已匹配列表
     */
    private fun pairList(pageNo: Int) {
        mainDataSource.enqueue({ pairList(pageNo) }) {
            onSuccess {
                BaseApp.fixedThreadPool.execute {
                    for (item in it) {
                        var chatListInfo =
                            MineApp.chatListDaoManager.getChatListInfo("common_${item.otherUserId}")
                        if (chatListInfo == null) {
                            chatListInfo = ChatListInfo()
                            chatListInfo.chatId = "common_${item.otherUserId}"
                            chatListInfo.otherUserId = item.otherUserId
                            chatListInfo.nick = item.nick
                            chatListInfo.image = item.headImage
                            chatListInfo.creationDate = item.pairTime
                            chatListInfo.stanza = "匹配于" + if (item.pairTime.isEmpty())
                                DateUtil.getNow(DateUtil.yyyy_MM_dd_HH_mm_ss).substring(5, 10)
                            else
                                item.pairTime.substring(5, 10)
                            chatListInfo.msgType = 1
                            chatListInfo.noReadNum = 0
                            chatListInfo.publicTag = ""
                            chatListInfo.effectType = 1
                            chatListInfo.authType = 1
                            chatListInfo.msgChannelType = 1
                            chatListInfo.showChat = false
                            chatListInfo.chatType = 4
                            chatListInfo.mainUserId = getLong("userId")
                            MineApp.chatListDaoManager.insert(chatListInfo)
                        } else {
                            MineApp.chatListDaoManager.updateChatListInfo(
                                item.nick, item.headImage, chatListInfo.creationDate,
                                chatListInfo.stanza, chatListInfo.msgType,
                                chatListInfo.noReadNum, chatListInfo.showChat,
                                "common_${item.otherUserId}"
                            )
                        }
                        chatType4List.add(chatListInfo)
                    }
                }

                MineApp.pairList.addAll(it)
                pairList(pageNo + 1)
            }
            onFailed {
                binding.refresh.finishRefresh()
                val start = chatListInfoList.size
                val comparator = LikeMeComparator()
                Collections.sort(chatType4List, comparator)
                chatListInfoList.addAll(chatType4List)
                adapter.notifyItemRangeChanged(start, chatListInfoList.size)
            }
        }
    }

    private class LikeMeComparator : Comparator<ChatListInfo> {
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
                    o2.creationDate, o1.creationDate, DateUtil.yyyy_MM_dd_HH_mm_ss, 1000f
                )
        }
    }
}