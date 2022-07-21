package com.yimi.rentme.activity

import com.yimi.rentme.R
import com.yimi.rentme.bean.MemberInfo
import com.yimi.rentme.databinding.AcDiscoverListBinding
import com.yimi.rentme.vm.DiscoverListViewModel
import com.zb.baselibs.activity.BaseWhiteActivity

class DiscoverListActivity : BaseWhiteActivity() {

    private val viewModel by getViewModel(DiscoverListViewModel::class.java) {
        binding = mBinding as AcDiscoverListBinding
        activity = this@DiscoverListActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_discover_list
    }

    override fun initView() {
        val extras = intent.extras
        if (extras != null) {
            viewModel.otherUserId = extras.getLong("otherUserId")
            viewModel.memberInfo = extras.getSerializable("memberInfo") as MemberInfo
        }
        viewModel.initViewModel()
    }
}