package com.yimi.rentme.activity

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
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

    private var savedInstanceState: Bundle? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
    }


    override fun getRes(): Int {
        return R.layout.ac_select_location
    }

    override fun initView() {
        val extras = intent.extras
        if (extras != null)
            viewModel.isPublish = extras.getBoolean("isPublish")
        map_view.onCreate(savedInstanceState)
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