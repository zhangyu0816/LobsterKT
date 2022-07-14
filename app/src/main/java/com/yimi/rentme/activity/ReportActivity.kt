package com.yimi.rentme.activity

import com.yimi.rentme.R
import com.yimi.rentme.bean.SelectImage
import com.yimi.rentme.databinding.AcReportBinding
import com.yimi.rentme.vm.ReportViewModel
import com.zb.baselibs.activity.BaseWhiteActivity
import org.simple.eventbus.Subscriber

class ReportActivity : BaseWhiteActivity() {

    private val viewModel by getViewModel(ReportViewModel::class.java) {
        binding = mBinding as AcReportBinding
        activity = this@ReportActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_report
    }

    override fun initView() {
        needEvenBus = true
        val extras = intent.extras
        if (extras != null)
            viewModel.otherUserId = extras.getLong("otherUserId")
        viewModel.initViewModel()
    }

    /**
     * 上传图片
     */
    @Subscriber(tag = "lobsterUploadImageList")
    private fun lobsterUploadImageList(data: ArrayList<SelectImage>) {
        viewModel.uploadImageList(data)
    }
}