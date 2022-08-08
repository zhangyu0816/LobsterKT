package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragMainCardBinding
import com.yimi.rentme.vm.fragment.MainCardViewModel
import com.zb.baselibs.activity.BaseFragment
import com.zb.baselibs.utils.getString
import org.simple.eventbus.Subscriber

class MainCardFrag : BaseFragment() {

    private val viewModel by getViewModel(MainCardViewModel::class.java) {
        binding = mBinding as FragMainCardBinding
        activity = this@MainCardFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_main_card
    }

    override fun initView() {
        needEvenBus = true
        viewModel.initViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    /**
     * 更新卡片动画
     */
    @Subscriber(tag = "lobsterCard")
    private fun lobsterCard(data: Int) {
        viewModel.moveCard(data)
    }

    /**
     * 更新城市
     */
    @Subscriber(tag = "kotlin_do_area")
    private fun kotlinDoArea(data: String) {
        viewModel.updateCity(0)
    }

    /**
     * 选择城市
     */
    @Subscriber(tag = "lobsterCityName")
    private fun lobsterCityName(data: String) {
        viewModel.updateCity(1)
    }
}