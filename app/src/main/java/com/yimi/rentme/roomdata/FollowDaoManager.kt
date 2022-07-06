package com.yimi.rentme.roomdata

import android.content.Context
import androidx.room.*
import com.zb.baselibs.utils.getLong

@Entity(tableName = "FollowInfo")
data class FollowInfo(
    @PrimaryKey
    var otherUserId: Long = 0L,
    var nick: String = "",
    var image: String = "", // 头像
    var images: String = "", // 形象图
    var isFollow: Boolean = false,
    var mainUserId: Long = 0L // 拥有的
) {
    @Ignore
    constructor() : this(0L)
}

@Dao
interface FollowInfoDao {
    /**
     * 保存关注
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg followInfo: FollowInfo)

    /**
     * 获取关注中是否已关注
     */
    @Query("select isFollow from FollowInfo where otherUserId=:otherUserId and mainUserId=:mainUserId")
    fun queryFollow(otherUserId: Long, mainUserId: Long): Boolean

    /**
     * 获取关注中头像
     */
    @Query("select image from FollowInfo where otherUserId=:otherUserId and mainUserId=:mainUserId")
    fun queryImage(otherUserId: Long, mainUserId: Long): String

    /**
     * 获取关注中形象图
     */
    @Query("select images from FollowInfo where otherUserId=:otherUserId and mainUserId=:mainUserId")
    fun queryImages(otherUserId: Long, mainUserId: Long): String

    /**
     * 获取关注中昵称
     */
    @Query("select nick from FollowInfo where otherUserId=:otherUserId and mainUserId=:mainUserId")
    fun queryNick(otherUserId: Long, mainUserId: Long): String
}

@Database(entities = [FollowInfo::class], version = 1, exportSchema = false)
abstract class FollowRoomDatabase : RoomDatabase() {
    abstract fun getFollowInfoDao(): FollowInfoDao
}

class FollowDaoManager(private val context: Context) {

    private val database by lazy {
        Room.databaseBuilder(
            context, FollowRoomDatabase::class.java,
            "Follow_DB"
        ).build()
    }
    private val dao by lazy { database.getFollowInfoDao() }

    /**
     * 保存关注
     */
    fun insert(followInfo: FollowInfo) {
        dao.insert(followInfo)
    }

    /**
     * 获取关注中是否已关注
     */
    fun getFollow(otherUserId: Long): Boolean {
        return dao.queryFollow(otherUserId, getLong("userId"))
    }

    /**
     * 获取关注中头像
     */
    fun getImage(otherUserId: Long): String {
        return dao.queryImage(otherUserId, getLong("userId"))
    }

    /**
     * 获取关注中形象图
     */
    fun getImages(otherUserId: Long): String {
        return dao.queryImages(otherUserId, getLong("userId"))
    }

    /**
     * 获取关注中昵称
     */
    fun getNick(otherUserId: Long): String {
        return dao.queryNick(otherUserId, getLong("userId"))
    }
}