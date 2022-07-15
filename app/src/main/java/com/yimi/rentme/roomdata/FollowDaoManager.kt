package com.yimi.rentme.roomdata

import android.content.Context
import androidx.room.*
import com.zb.baselibs.utils.getLong

@Entity(tableName = "FollowInfo")
data class FollowInfo(
    @PrimaryKey
    var otherUserId: Long = 0L,
    var nick: String = "", // 用户昵称
    var image: String = "", // 用户头像
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
    @Query("select * from FollowInfo where otherUserId=:otherUserId and mainUserId=:mainUserId")
    fun queryFollowInfo(otherUserId: Long, mainUserId: Long): FollowInfo?

    /**
     * 删除关注
     */
    @Query("delete from FollowInfo where otherUserId=:otherUserId and mainUserId=:mainUserId")
    fun deleteFollowInfo(otherUserId: Long, mainUserId: Long)
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
    fun getFollowInfo(otherUserId: Long): FollowInfo? {
        return dao.queryFollowInfo(otherUserId, getLong("userId"))
    }

    /**
     * 删除关注
     */
    fun deleteFollowInfo(otherUserId: Long){
         dao.deleteFollowInfo(otherUserId, getLong("userId"))
    }
}