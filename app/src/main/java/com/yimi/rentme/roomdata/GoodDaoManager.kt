package com.yimi.rentme.roomdata

import android.content.Context
import androidx.room.*
import com.zb.baselibs.utils.getLong

@Entity(tableName = "GoodInfo")
data class GoodInfo(
    @PrimaryKey
    var friendDynId: Long = 0L,
    var mainUserId: Long = 0L
) {
    @Ignore
    constructor() : this(0L)
}

@Dao
interface GoodInfoDao {
    /**
     * 保存点赞
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg goodInfo: GoodInfo)

    /**
     * 获取点赞
     */
    @Query("select * from GoodInfo where friendDynId=:friendDynId and mainUserId=:mainUserId")
    fun queryGood(friendDynId: Long, mainUserId: Long): GoodInfo?

    /**
     * 删除点赞
     */
    @Query("delete from GoodInfo where friendDynId=:friendDynId and mainUserId=:mainUserId")
    fun deleteGood(friendDynId: Long, mainUserId: Long)
}

@Database(entities = [GoodInfo::class], version = 1, exportSchema = false)
abstract class GoodRoomDatabase : RoomDatabase() {
    abstract fun getGoodInfoDao(): GoodInfoDao
}

class GoodDaoManager(private val context: Context) {

    private val database by lazy {
        Room.databaseBuilder(
            context, GoodRoomDatabase::class.java,
            "Good_DB"
        ).build()
    }
    private val dao by lazy { database.getGoodInfoDao() }

    /**
     * 保存点餐
     */
    fun insert(goodInfo: GoodInfo) {
        dao.insert(goodInfo)
    }

    /**
     * 获取点赞
     */
    fun getGood(friendDynId: Long): GoodInfo? {
        return dao.queryGood(friendDynId, getLong("userId"))
    }

    /**
     * 删除点赞
     */
    fun deleteGood(friendDynId: Long) {
        dao.deleteGood(friendDynId, getLong("userId"))
    }
}