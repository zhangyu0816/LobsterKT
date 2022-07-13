package com.yimi.rentme.activity

import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcReportBinding
import com.yimi.rentme.vm.ReportViewModel
import com.zb.baselibs.activity.BaseWhiteActivity

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
        val extras = intent.extras
        if (extras != null)
            viewModel.otherUserId = extras.getLong("otherUserId")
        viewModel.initViewModel()
    }
}