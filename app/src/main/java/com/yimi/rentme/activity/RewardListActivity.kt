package com.yimi.rentme.activity

import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcRewardListBinding
import com.yimi.rentme.vm.RewardListViewModel
import com.zb.baselibs.activity.BaseScreenActivity

class RewardListActivity : BaseScreenActivity() {

    private val viewModel by getViewModel(RewardListViewModel::class.java) {
        binding = mBinding as AcRewardListBinding
        activity = this@RewardListActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_reward_list
    }

    override fun initView() {
        val extras = intent.extras
        if (extras != null)
            viewModel.otherUserId = extras.getLong("otherUserId")
        viewModel.initViewModel()
    }
}