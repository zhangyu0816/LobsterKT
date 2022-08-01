package com.yimi.rentme.activity.bottle

import android.view.KeyEvent
import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcBottleBinding
import com.yimi.rentme.vm.bottle.BottleViewModel
import com.zb.baselibs.activity.BaseScreenActivity
import kotlinx.android.synthetic.main.ac_mnimage_browser.*

class BottleActivity : BaseScreenActivity() {

    private val viewModel by getViewModel(BottleViewModel::class.java) {
        binding = mBinding as AcBottleBinding
        activity = this@BottleActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_bottle
    }

    override fun initView() {
        viewModel.initViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.onResume()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            viewModel.back(iv_back)
            return true
        }
        return false
    }
}