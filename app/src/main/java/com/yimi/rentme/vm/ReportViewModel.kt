package com.yimi.rentme.vm

import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.Report
import com.yimi.rentme.databinding.AcReportBinding

class ReportViewModel : BaseViewModel() {

    lateinit var binding: AcReportBinding
    var otherUserId = 0L
    lateinit var adapter: BaseAdapter<Report>
    private var mPosition = -1
    lateinit var imageAdapter: BaseAdapter<String>
    private val imageList = ArrayList<String>()

    override fun initViewModel() {
        binding.title = "举报内容"
        binding.right = "举报"
        binding.content = ""

        adapter = BaseAdapter(activity, R.layout.item_report, MineApp.reportList, this)

        imageAdapter = BaseAdapter(activity, R.layout.item_report_image, imageList, this)
    }

    /**
     * 选择类型
     */
    fun selectPosition(position: Int) {
        adapter.setSelectIndex(position)
        if (mPosition != -1)
            adapter.notifyItemChanged(mPosition)
        adapter.notifyItemChanged(position)
        mPosition = position
    }

    /**
     * 选择照片
     */
    fun previewImage(position: Int){

    }
}