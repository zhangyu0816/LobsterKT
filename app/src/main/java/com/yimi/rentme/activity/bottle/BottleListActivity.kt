package com.yimi.rentme.activity.bottle

import com.yimi.rentme.R
import com.yimi.rentme.bean.SelectImage
import com.yimi.rentme.databinding.AcBottleListBinding
import com.yimi.rentme.vm.bottle.BottleListViewModel
import com.zb.baselibs.activity.BaseScreenActivity
import org.simple.eventbus.EventBus
import org.simple.eventbus.Subscriber

class BottleListActivity : BaseScreenActivity() {

    private val viewModel by getViewModel(BottleListViewModel::class.java) {
        binding = mBinding as AcBottleListBinding
        activity = this@BottleListActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_bottle_list
    }

    override fun initView() {
        needEvenBus = true
        viewModel.initViewModel()
    }

    /**
     * 支付成功
     */
    @Subscriber(tag = "kotlin_pay_back")
    private fun payBack(data: String) {
        viewModel.updateBlur()
    }

    /**
     * 更新单个漂流瓶
     */
    @Subscriber(tag = "lobsterSingleBottle")
    private fun lobsterSingleBottle(data: String) {
        viewModel.singleBottle(data.toLong())
    }
}