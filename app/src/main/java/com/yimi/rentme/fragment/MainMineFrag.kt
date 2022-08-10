package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragMainMineBinding
import com.yimi.rentme.vm.fragment.MainMineViewModel
import com.zb.baselibs.activity.BaseFragment
import org.simple.eventbus.Subscriber

class MainMineFrag : BaseFragment() {

    private val viewModel by getViewModel(MainMineViewModel::class.java) {
        binding = mBinding as FragMainMineBinding
        activity = this@MainMineFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_main_mine
    }

    override fun initView() {
        needEvenBus = true
        viewModel.initViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    /**
     * 更新访问统计
     */
    @Subscriber(tag = "lobsterVisitor")
    private fun lobsterVisitor(data: String) {
        viewModel.visitor()
    }

    /**
     * 更新动态数量
     */
    @Subscriber(tag = "lobsterDynNotData")
    private fun lobsterDynNotData(data: Boolean) {
        viewModel.dynNotData(data)
    }

    /**
     * VIP开通成功
     */
    @Subscriber(tag = "lobsterUpdateBtn")
    private fun lobsterUpdateBtn(data: String) {
        viewModel.setBtn()
    }
}