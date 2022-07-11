package com.yimi.rentme.activity

import android.view.KeyEvent
import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcMnimageBrowserBinding
import com.yimi.rentme.vm.MNImageBrowserViewModel
import com.zb.baselibs.activity.BaseScreenActivity
import kotlinx.android.synthetic.main.ac_mnimage_browser.*

class MNImageBrowserActivity : BaseScreenActivity() {

    val viewModel by getViewModel(MNImageBrowserViewModel::class.java) {
        activity = this@MNImageBrowserActivity
        binding = mBinding as AcMnimageBrowserBinding
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_mnimage_browser
    }

    override fun initView() {
        viewModel.initViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            viewModel.back(iv_back)
            return true
        }
        return false
    }
}