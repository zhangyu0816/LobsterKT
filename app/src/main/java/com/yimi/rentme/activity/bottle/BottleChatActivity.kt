package com.yimi.rentme.activity.bottle

import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcBaseChatBinding
import com.yimi.rentme.vm.BaseChatViewModel
import com.zb.baselibs.activity.BaseScreenActivity

class BottleChatActivity : BaseScreenActivity() {

    private val viewModel by getViewModel(BaseChatViewModel::class.java) {
        binding = mBinding as AcBaseChatBinding
        activity = this@BottleChatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_base_chat
    }

    override fun initView() {
        val extras = intent.extras
        if (extras != null) {
            viewModel.isNotice = extras.getBoolean("isNotice")
            viewModel.msgChannelType = extras.getInt("msgChannelType")
            viewModel.driftBottleId = extras.getLong("driftBottleId")
        }
        viewModel.initViewModel()
    }
}