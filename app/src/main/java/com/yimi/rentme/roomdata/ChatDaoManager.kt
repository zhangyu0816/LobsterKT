package com.yimi.rentme.roomdata

import android.content.Context
import androidx.room.*
import com.zb.baselibs.utils.getLong

/**
 * 会话列表--显示列表页
 */
@Entity(tableName = "ChatListInfo")
data class ChatListInfo(
    @PrimaryKey
    var chatId: String = "", // 1、普通聊天：common_otherUserId   2、漂流瓶：drift_driftBottleId   3、闪聊：flash_flashTalkId
    var otherUserId: Long = 0L,
    var nick: String = "", // 用户昵称
    var image: String = "", // 用户头像
    var creationDate: String = "", // 会话时间
    var stanza: String = "", // 内容
    var msgType: Int = 0, // 消息类型 1：文字 2：图片 3：语音 4：视频
    var noReadNum: Int = 0, // 未读数
    var publicTag: String = "", // 标签
    var effectType: Int = 0, // 作用类型 1.无  2.系统内置      默认 null
    var authType: Int = 0, // 权限类型  1.无限制  2.以下限制      默认 null
    var msgChannelType: Int = 0, // 1：普通聊天  2：漂流瓶  3：闪聊
    var chatType: Int = 0, // 1 喜欢我  2 漂流瓶  3超级喜欢  4 匹配成功  5 动态  6 闪聊
    var mainUserId: Long = 0L // 拥有的
) {
    @Ignore
    constructor() : this("")
}

@Dao
interface ChatListInfoDao {
    /**
     * 保存
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg chatListInfo: ChatListInfo)

    /**
     * 获取单个会话
     */
    @Query("select * from ChatListInfo where chatId=:chatId and mainUserId=:mainUserId")
    fun queryChatListInfo(chatId: String, mainUserId: Long): ChatListInfo?

    /**
     * 获取某个类型的会话列表
     */
    @Query("select * from ChatListInfo where msgChannelType=:msgChannelType and mainUserId=:mainUserId")
    fun queryChatListInfoList(msgChannelType: Int, mainUserId: Long): MutableList<ChatListInfo>

    /**
     * 更新未读数
     */
    @Query("update ChatListInfo set noReadNum=:noReadNum where chatId=:chatId and mainUserId=:mainUserId")
    fun updateNoReadNum(noReadNum: Int, chatId: String, mainUserId: Long)

    /**
     * 更新所有
     */
    @Query("update ChatListInfo set nick=:nick,image=:image,creationDate=:creationDate,stanza=:stanza,msgType=:msgType,noReadNum=:noReadNum  where chatId=:chatId and mainUserId=:mainUserId")
    fun updateChatListInfo(
        nick: String, image: String, creationDate: String, stanza: String, msgType: Int,
        noReadNum: Int, chatId: String, mainUserId: Long
    )

    /**
     * 删除会话
     */
    @Query("delete from ChatListInfo where chatId=:chatId and mainUserId=:mainUserId")
    fun deleteChatListInfo(chatId: String, mainUserId: Long)
}

@Database(entities = [ChatListInfo::class], version = 1, exportSchema = false)
abstract class ChatListRoomDatabase : RoomDatabase() {
    abstract fun getChatListInfoDao(): ChatListInfoDao
}

class ChatListDaoManager(private val context: Context) {

    private val database by lazy {
        Room.databaseBuilder(
            context, ChatListRoomDatabase::class.java,
            "ChatList_DB"
        ).build()
    }
    private val dao by lazy { database.getChatListInfoDao() }

    /**
     * 保存
     */
    fun insert(chatListInfo: ChatListInfo) {
        dao.insert(chatListInfo)
    }

    /**
     * 获取单个会话
     */
    fun getChatListInfo(chatId: String): ChatListInfo? {
        return dao.queryChatListInfo(chatId, getLong("userId"))
    }

    /**
     * 获取某个类型的会话列表
     */
    fun getChatListInfoList(msgChannelType: Int): MutableList<ChatListInfo> {
        return dao.queryChatListInfoList(msgChannelType, getLong("userId"))
    }

    /**
     * 更新未读数
     */
    fun updateNoReadNum(noReadNum: Int, chatId: String) {
        dao.updateNoReadNum(noReadNum, chatId, getLong("userId"))
    }

    /**
     * 更新所有
     */
    fun updateChatListInfo(
        nick: String, image: String, creationDate: String, stanza: String, msgType: Int,
        noReadNum: Int, chatId: String
    ) {
        dao.updateChatListInfo(
            nick, image, creationDate, stanza, msgType,
            noReadNum, chatId, getLong("userId")
        )
    }

    /**
     * 删除会话
     */
    fun deleteChatListInfo(chatId: String) {
        dao.deleteChatListInfo(chatId, getLong("userId"))
    }
}