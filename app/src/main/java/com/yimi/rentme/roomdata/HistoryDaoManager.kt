package com.yimi.rentme.roomdata

import android.content.Context
import androidx.room.*
import com.yimi.rentme.bean.PrivateMsg
import com.zb.baselibs.mimc.CustomMessageBody
import com.zb.baselibs.utils.getLong

/**
 * 会话列表--显示列表页
 */
@Entity(tableName = "HistoryInfo")
data class HistoryInfo(
    @PrimaryKey
    var thirdMessageId: String = "", //第三方消息id
    var fromId: Long = 0,//发送人ID
    var toId: Long = 0,//接收人ID
    var creationDate: String = "",//发送时间
    var stanza: String = "", //消息内容
    var msgType: Int = 0,//消息类型 1：文字 2：图片 3：语音 4：视频
    var title: String = "",//标题
    var resLink: String = "", //资源链接
    var resTime: Int = 0,//资源时长  秒
    var isDelete: Int = 0,//状态 0：正常 1删除
    var isRead: Int = 0,//状态 0：未读 1已读
    var msgChannelType: Int = 1,// 1：普通聊天  2：漂流瓶  3：闪聊
    var driftBottleId: Long = 0,//所属漂流瓶
    var flashTalkId: Long = 0,//所属闪聊
    var otherUserId: Long = 0,//所属普通聊天
    var imPlatformType: Int = 0,// 1.zuwoIM 2.阿里OpenIM 当前使用：2
    var showTime: Boolean = false,
    var theChatUk: String = "", //两个人的Id拼接起来，小的在前面  #12#101#
    var chatId: String = "", // 1、普通聊天：common_otherUserId   2、漂流瓶：drift_driftBottleId   3、闪聊：flash_flashTalkId
    var mainUserId: Long = getLong("userId")
) {
    @Ignore
    constructor() : this("")

    fun createBottleHistoryInfo(
        privateMsg: PrivateMsg,
        otherUserId: Long,
        msgChannelType: Int,
        driftBottleId: Long
    ): HistoryInfo {
        val historyInfo = HistoryInfo()
        historyInfo.thirdMessageId = privateMsg.thirdMessageId
        historyInfo.mainUserId = getLong("userId")
        historyInfo.fromId = privateMsg.fromId
        historyInfo.toId = privateMsg.toId
        historyInfo.creationDate = privateMsg.creationDate
        historyInfo.stanza = privateMsg.stanza
        historyInfo.msgType = privateMsg.msgType
        historyInfo.title = privateMsg.title
        historyInfo.resLink = privateMsg.resLink
        historyInfo.resTime = privateMsg.resTime
        historyInfo.otherUserId = otherUserId
        historyInfo.msgChannelType = msgChannelType
        historyInfo.driftBottleId = driftBottleId
        historyInfo.chatId = "drift_$driftBottleId"
        return historyInfo
    }
    fun createBottleHistoryInfoFromBody(
        body: CustomMessageBody,
        otherUserId: Long,
        msgChannelType: Int,
        driftBottleId: Long
    ): HistoryInfo {
        val historyInfo = HistoryInfo()
        historyInfo.thirdMessageId = body.thirdMessageId
        historyInfo.mainUserId = getLong("userId")
        historyInfo.fromId = body.mFromId
        historyInfo.toId = body.mToId
        historyInfo.creationDate = body.creationDate
        historyInfo.stanza = body.mStanza
        historyInfo.msgType = body.mMsgType
        historyInfo.title = body.mStanza
        historyInfo.resLink = body.mResLink
        historyInfo.resTime =  body.mResTime
        historyInfo.otherUserId = otherUserId
        historyInfo.msgChannelType = msgChannelType
        historyInfo.driftBottleId = driftBottleId
        historyInfo.chatId = "drift_$driftBottleId"
        return historyInfo
    }
}

@Dao
interface HistoryInfoDao {
    /**
     * 保存
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg historyInfo: HistoryInfo)

    /**
     * 获取单个历史记录
     */
    @Query("select * from HistoryInfo where thirdMessageId=:thirdMessageId and mainUserId=:mainUserId")
    fun queryHistoryInfo(thirdMessageId: String, mainUserId: Long): HistoryInfo?

    /**
     * 获取单个会话的历史记录 -- 按时间降序
     */
    @Query("select * from HistoryInfo where chatId=:chatId and mainUserId=:mainUserId order by creationDate desc")
    fun queryHistoryInfoList(chatId: String, mainUserId: Long): MutableList<HistoryInfo>

    /**
     * 更新已读
     */
    @Query("update HistoryInfo set isRead=:isRead where thirdMessageId=:thirdMessageId and mainUserId=:mainUserId")
    fun updateRead(isRead: Int, thirdMessageId: String, mainUserId: Long)

    /**
     * 更新显示时间
     */
    @Query("update HistoryInfo set showTime=:showTime where thirdMessageId=:thirdMessageId and mainUserId=:mainUserId")
    fun updateShowTime(showTime: Boolean, thirdMessageId: String, mainUserId: Long)

    /**
     * 删除历史记录
     */
    @Query("delete from HistoryInfo where chatId=:chatId and mainUserId=:mainUserId")
    fun deleteHistoryInfo(chatId: String, mainUserId: Long)
}

@Database(entities = [HistoryInfo::class], version = 1, exportSchema = false)
abstract class HistoryRoomDatabase : RoomDatabase() {
    abstract fun getHistoryInfoDao(): HistoryInfoDao
}

class HistoryDaoManager(private val context: Context) {

    private val database by lazy {
        Room.databaseBuilder(
            context, HistoryRoomDatabase::class.java,
            "History_DB"
        ).build()
    }
    private val dao by lazy { database.getHistoryInfoDao() }

    /**
     * 保存
     */
    fun insert(historyInfo: HistoryInfo) {
        dao.insert(historyInfo)
    }

    /**
     * 获取单个历史记录
     */
    fun getHistoryInfo(thirdMessageId: String): HistoryInfo? {
        return dao.queryHistoryInfo(thirdMessageId, getLong("userId"))
    }

    /**
     * 获取单个会话的历史记录 -- 按时间降序
     */
    fun getHistoryInfoList(chatId: String): MutableList<HistoryInfo> {
        return dao.queryHistoryInfoList(chatId, getLong("userId"))
    }

    /**
     * 更新已读
     */
    fun updateRead(thirdMessageId: String) {
        dao.updateRead(1, thirdMessageId, getLong("userId"))
    }

    /**
     * 更新显示时间
     */
    fun updateShowTime(thirdMessageId: String) {
        dao.updateShowTime(true, thirdMessageId, getLong("userId"))
    }

    /**
     * 删除历史记录
     */
    fun deleteHistoryInfo(chatId: String) {
        dao.deleteHistoryInfo(chatId, getLong("userId"))
    }
}