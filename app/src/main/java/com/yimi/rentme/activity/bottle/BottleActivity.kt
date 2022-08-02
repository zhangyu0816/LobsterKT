package com.yimi.rentme.activity.bottle

import android.view.KeyEvent
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcBottleBinding
import com.yimi.rentme.vm.bottle.BottleViewModel
import com.zb.baselibs.activity.BaseScreenActivity
import kotlinx.android.synthetic.main.ac_mnimage_browser.*
import org.simple.eventbus.Subscriber

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
        needEvenBus = true
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

    /**
     * 更新漂流瓶
     */
    @Subscriber(tag = "lobsterBottleNoReadNum")
    private fun lobsterBottleNoReadNum(data: String) {
        viewModel.binding.noReadNum = MineApp.noReadBottleNum
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            viewModel.back(iv_back)
            return true
        }
        return false
    }
}