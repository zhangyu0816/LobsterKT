package com.yimi.rentme.dialog

import android.graphics.Color
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.yimi.rentme.R
import com.yimi.rentme.databinding.DfJobBinding
import com.yimi.rentme.roomdata.JobDaoManager
import com.yimi.rentme.roomdata.JobInfo
import com.zb.baselibs.adapter.SelectAdapter
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.BaseDialogFragment
import com.zb.baselibs.utils.SimulateNetAPI
import org.jaaksi.pickerview.widget.DefaultCenterDecoration
import org.json.JSONArray

class JobDF(activity: AppCompatActivity) : BaseDialogFragment(activity, false, false) {

    private lateinit var binding: DfJobBinding
    private var jobTitle = ""
    private var jobName = ""
    private lateinit var callBack: CallBack
    private lateinit var jobDaoManager: JobDaoManager
    private val jobTitleList = ArrayList<String>()
    private var jobNameList = ArrayList<String>()
    private lateinit var decoration: DefaultCenterDecoration
    private var mTitle = ""
    private var mName = ""
    private var mTitlePosition = 0
    private var mNamePosition = 0

    override val layoutId: Int
        get() = R.layout.df_job

    fun setJobName(jobName: String): JobDF {
        this.jobName = jobName
        return this
    }

    fun setJobTitle(jobTitle: String): JobDF {
        this.jobTitle = jobTitle
        return this
    }

    fun setCallBack(callBack: CallBack): JobDF {
        this.callBack = callBack
        return this
    }

    override fun setDataBinding(viewDataBinding: ViewDataBinding?) {
        binding = viewDataBinding as DfJobBinding
    }

    fun show(manager: FragmentManager) {
        show(manager, "${BaseApp.projectName}_JobDF")
    }

    override fun initUI() {
        jobTitleList.add("信息技术")
        jobTitleList.add("金融保险")
        jobTitleList.add("娱乐服务")
        jobTitleList.add("商业管理")
        jobTitleList.add("工程制造")
        jobTitleList.add("交通运输")
        jobTitleList.add("文化传媒")
        jobTitleList.add("公共事业")
        jobTitleList.add("模特")
        jobTitleList.add("学生")

        decoration = DefaultCenterDecoration(context)
        decoration.setLineColor(Color.parseColor("#eeeeee"))
        decoration.setLineWidth(0.1f)

        binding.titlePv.visibleItemCount = 5
        binding.namePv.visibleItemCount = 5

        binding.dialog = this
        jobDaoManager = JobDaoManager(BaseApp.context)
        BaseApp.fixedThreadPool.execute {
            jobNameList =
                jobDaoManager.getJobList(jobTitle.ifEmpty { jobTitleList[0] }) as ArrayList<String>
            if (jobNameList.size == 0) {
                val data = SimulateNetAPI.getOriginalFundData("job.json")
                val array = JSONArray(data)
                for (i in 0 until array.length()) {
                    val jobJSON = array.optJSONObject(i)
                    val jobArray = jobJSON.optJSONArray("job")
                    if (jobArray != null) {
                        for (j in 0 until jobArray.length()) {
                            val jobObject = jobArray.optJSONObject(j)
                            val jobInfo = JobInfo()
                            jobInfo.jobTitle = jobJSON.optString("jobTitle")
                            jobInfo.jobName = jobObject.optString("name")
                            jobDaoManager.insert(jobInfo)
                        }
                    }
                }
                jobNameList =
                    jobDaoManager.getJobList(jobTitle.ifEmpty { jobTitleList[0] }) as ArrayList<String>
            }
            activity.runOnUiThread {
                mTitle = jobTitle.ifEmpty { jobTitleList[0] }
                mName = jobName.ifEmpty { jobNameList[0] }
                setTitleList()
                setNameList()
            }
        }
    }

    /**
     * 工作类型
     */
    private fun setTitleList() {
        mTitlePosition = jobTitleList.indexOf(mTitle)
        binding.titlePv.adapter = SelectAdapter(jobTitleList)
        binding.titlePv.setCenterDecoration(decoration)
        binding.titlePv.selectedPosition = mTitlePosition
        binding.titlePv.setOnSelectedListener { pickerView, position ->
            mTitlePosition = position
            mTitle = jobTitleList[position]
            BaseApp.fixedThreadPool.execute {
                jobNameList = jobDaoManager.getJobList(mTitle) as ArrayList<String>
                activity.runOnUiThread {
                    setNameList()
                }
            }
        }
    }

    /**
     * 工作名
     */
    private fun setNameList() {
        mNamePosition = jobNameList.indexOf(mName)
        binding.namePv.adapter = SelectAdapter(jobNameList)
        binding.namePv.setCenterDecoration(decoration)
        binding.namePv.selectedPosition = mNamePosition
        binding.namePv.setOnSelectedListener { pickerView, position ->
            mNamePosition = position
            mName = jobNameList[position]
        }
    }

    fun cancel(view: View) {
        dismiss()
    }

    fun sure(view: View) {
        callBack.sure(mTitle, mName)
        dismiss()
    }

    interface CallBack {
        fun sure(jobTitle: String, jobName: String)
    }
}