package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragPairBinding
import com.yimi.rentme.vm.fragment.PairViewModel
import com.zb.baselibs.activity.BaseFragment
import org.simple.eventbus.Subscriber

class PairFrag : BaseFragment() {

    private val viewModel by getViewModel(PairViewModel::class.java) {
        binding = mBinding as FragPairBinding
        activity = this@PairFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_pair
    }

    override fun initView() {
        needEvenBus = true
        viewModel.initViewModel()
    }

    /**
     * 更新聊天列表
     */
    @Subscriber(tag = "lobsterUpdateChatList")
    private fun lobsterUpdateChatList(data: String) {
        viewModel.personOtherDyn()
    }

    /**
     * 更新聊天列表
     */
    @Subscriber(tag = "lobsterUploadDyn")
    private fun lobsterUploadDyn(data: String) {
        viewModel.personOtherDyn()
    }
}