package com.yimi.rentme.roomdata

import android.content.Context
import androidx.room.*

@Entity(tableName = "JobInfo")
data class JobInfo(
    @PrimaryKey
    var jobName: String = "",
    var jobTitle: String = ""

) {
    @Ignore
    constructor() : this("")
}

@Dao
interface JobInfoDao {
    /**
     * 保存工作
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg jobInfo: JobInfo)

    /**
     * 获取工作类中所有工作信息
     */
    @Query("select jobName from JobInfo where jobTitle=:jobTitle")
    fun queryJobList(jobTitle: String): MutableList<String>
}

@Database(entities = [JobInfo::class], version = 1, exportSchema = false)
abstract class JobRoomDatabase : RoomDatabase() {
    abstract fun getJobInfoDao(): JobInfoDao
}

class JobDaoManager(private val context: Context) {

    private val database by lazy {
        Room.databaseBuilder(
            context, JobRoomDatabase::class.java,
            "Job_DB"
        ).build()
    }
    private val dao by lazy { database.getJobInfoDao() }

    /**
     * 保存工作
     */
    fun insert(jobInfo: JobInfo) {
        dao.insert(jobInfo)
    }

    /**
     * 获取工作类中所有工作信息
     */
    fun getJobList(jobTitle: String): MutableList<String> {
        return dao.queryJobList(jobTitle)
    }
}