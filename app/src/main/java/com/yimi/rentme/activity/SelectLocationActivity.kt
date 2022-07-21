package com.yimi.rentme.activity

import android.os.Bundle
import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcSelectLocationBinding
import com.yimi.rentme.vm.SelectLocationViewModel
import com.zb.baselibs.activity.BaseWhiteActivity
import kotlinx.android.synthetic.main.ac_select_location.*

class SelectLocationActivity : BaseWhiteActivity() {

    private val viewModel by getViewModel(SelectLocationViewModel::class.java) {
        binding = mBinding as AcSelectLocationBinding
        activity = this@SelectLocationActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_select_location
    }

    override fun initView() {
        val extras = intent.extras
        if (extras != null)
            viewModel.isPublish = extras.getBoolean("isPublish")
        viewModel.initViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map_view.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        map_view.onResume()
    }

    override fun onPause() {
        super.onPause()
        map_view.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        map_view.onDestroy()
    }
}