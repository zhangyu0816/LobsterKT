package com.yimi.rentme.roomdata

import android.content.Context
import androidx.room.*

@Entity(tableName = "ImageSize")
data class ImageSize(
    @PrimaryKey
    var imageUrl: String = "",
    var width: Int = 0, // 图片宽
    var height: Int = 0 // 图片高
) {
    @Ignore
    constructor() : this("")
}

@Dao
interface ImageSizeDao {
    /**
     * 保存图片尺寸
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg imageSize: ImageSize)

    /**
     * 获取图片尺寸
     */
    @Query("select * from ImageSize where imageUrl=:imageUrl")
    fun queryImageSize(imageUrl: String): ImageSize?
}

@Database(entities = [ImageSize::class], version = 1, exportSchema = false)
abstract class ImageSizeRoomDatabase : RoomDatabase() {
    abstract fun getImageSizeDao(): ImageSizeDao
}

class ImageSizeDaoManager(private val context: Context) {

    private val database by lazy {
        Room.databaseBuilder(
            context, ImageSizeRoomDatabase::class.java,
            "ImageSize_DB"
        ).build()
    }
    private val dao by lazy { database.getImageSizeDao() }

    /**
     * 保存图片尺寸
     */
    fun insert(imageSize: ImageSize) {
        dao.insert(imageSize)
    }

    /**
     * 获取图片尺寸
     */
    fun getImageSize(imageUrl: String): ImageSize? {
        return dao.queryImageSize(imageUrl)
    }
}