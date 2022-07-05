package com.yimi.rentme.roomdata

import android.content.Context
import androidx.room.*

@Entity(tableName = "TagInfo")
data class TagInfo(
    @PrimaryKey
    var tag: String = "",
    var tagName: String = ""
) {
    @Ignore
    constructor() : this("")
}

@Dao
interface TagInfoDao {
    /**
     * 保存标签
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg tagInfo: TagInfo)

    /**
     * 获取标签类中所有标签
     */
    @Query("select tag from TagInfo where tagName=:tagName")
    fun queryTagList(tagName: String): MutableList<String>
}

@Database(entities = [TagInfo::class], version = 1, exportSchema = false)
abstract class TagRoomDatabase : RoomDatabase() {
    abstract fun getTagInfoDao(): TagInfoDao
}

class TagDaoManager(private val context: Context) {

    private val database by lazy {
        Room.databaseBuilder(
            context, TagRoomDatabase::class.java,
            "Tag_DB"
        ).build()
    }
    private val dao by lazy { database.getTagInfoDao() }

    /**
     * 保存标签
     */
    fun insert(tagInfo: TagInfo) {
        dao.insert(tagInfo)
    }

    /**
     * 获取标签类中所有标签
     */
    fun getTagList(tagName: String): MutableList<String> {
        return dao.queryTagList(tagName)
    }
}