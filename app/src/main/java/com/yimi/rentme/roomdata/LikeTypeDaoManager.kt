package com.yimi.rentme.roomdata

import android.content.Context
import androidx.room.*
import com.zb.baselibs.utils.getLong

@Entity(tableName = "LikeTypeInfo")
data class LikeTypeInfo(
    @PrimaryKey
    var otherUserId: Long = 0L,
    var likeType: Int = 0, // 1：喜欢  2：超级喜欢
    var mainUserId: Long = 0L // 拥有的
) {
    @Ignore
    constructor() : this(0L)
}

@Dao
interface LikeTypeInfoDao {
    /**
     * 保存喜欢或超级喜欢
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg likeTypeInfo: LikeTypeInfo)

    /**
     * 获取喜欢或超级喜欢
     */
    @Query("select * from LikeTypeInfo where otherUserId=:otherUserId and mainUserId=:mainUserId")
    fun queryLikeTypeInfo(otherUserId: Long, mainUserId: Long): LikeTypeInfo?

    /**
     * 更新喜欢或超级喜欢
     */
    @Query("update LikeTypeInfo set likeType=:likeType where otherUserId=:otherUserId and mainUserId=:mainUserId")
    fun updateLikeType(likeType: Int, otherUserId: Long, mainUserId: Long)

    /**
     * 删除喜欢
     */
    @Query("delete from LikeTypeInfo where otherUserId=:otherUserId and mainUserId=:mainUserId")
    fun deleteLikeTypeInfo(otherUserId: Long, mainUserId: Long)
}

@Database(entities = [LikeTypeInfo::class], version = 1, exportSchema = false)
abstract class LikeTypeRoomDatabase : RoomDatabase() {
    abstract fun getLikeTypeInfoDao(): LikeTypeInfoDao
}

class LikeTypeDaoManager(private val context: Context) {

    private val database by lazy {
        Room.databaseBuilder(
            context, LikeTypeRoomDatabase::class.java,
            "LikeType_DB"
        ).build()
    }
    private val dao by lazy { database.getLikeTypeInfoDao() }

    /**
     * 保存喜欢或超级喜欢
     */
    fun insert(likeTypeInfo: LikeTypeInfo) {
        dao.insert(likeTypeInfo)
    }

    /**
     * 获取喜欢或超级喜欢
     */
    fun getLikeTypeInfo(otherUserId: Long): LikeTypeInfo? {
        return dao.queryLikeTypeInfo(otherUserId, getLong("userId"))
    }

    /**
     * 更新喜欢或超级喜欢
     */
    fun updateLikeType(likeType: Int, otherUserId: Long) {
        dao.updateLikeType(likeType, otherUserId, getLong("userId"))
    }

    /**
     * 删除喜欢
     */
    fun deleteLikeTypeInfo(otherUserId: Long) {
        dao.deleteLikeTypeInfo(otherUserId, getLong("userId"))
    }
}