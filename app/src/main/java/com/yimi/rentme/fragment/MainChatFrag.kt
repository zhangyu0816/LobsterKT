package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragMainChatBinding
import com.yimi.rentme.vm.fragment.MainChatViewModel
import com.zb.baselibs.activity.BaseFragment

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
        viewModel.initViewModel()
    }
}