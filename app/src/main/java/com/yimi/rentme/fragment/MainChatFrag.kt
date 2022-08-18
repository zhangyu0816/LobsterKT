package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragMainChatBinding
import com.yimi.rentme.vm.fragment.MainChatViewModel
import com.zb.baselibs.activity.BaseFragment
import org.simple.eventbus.Subscriber

class MainChatFrag : BaseFragment() {

    private val viewModel by getViewModel(MainChatViewModel::class.java) {
        binding = mBinding as FragMainChatBinding
        activity = this@MainChatFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_main_chat
    }

    override fun initView() {
        needEvenBus = true
        viewModel.initViewModel()
    }

    /**
     * 聊天红点
     */
    @Subscriber(tag = "lobsterUpdateTabRed")
    private fun lobsterUpdateTabRed(data: String) {
        viewModel.updateTabRed()
    }

}