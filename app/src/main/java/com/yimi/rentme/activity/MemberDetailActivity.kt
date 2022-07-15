package com.yimi.rentme.activity

import android.content.Intent
import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcMemberDetailBinding
import com.yimi.rentme.vm.MemberDetailViewModel
import com.zb.baselibs.activity.BaseScreenActivity

class MemberDetailActivity : BaseScreenActivity() {

    private val viewModel by getViewModel(MemberDetailViewModel::class.java) {
        binding = mBinding as AcMemberDetailBinding
        activity = this@MemberDetailActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_member_detail
    }

    override fun initView() {
        val extras = intent.extras
        if (extras != null) {
            viewModel.otherUserId = extras.getLong("otherUserId")
            viewModel.showLike = extras.getBoolean("showLike")
        }
        viewModel.initViewModel()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val extras = intent!!.extras
        if (extras != null) {
            viewModel.otherUserId = extras.getLong("otherUserId")
            viewModel.showLike = extras.getBoolean("showLike")
        }
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
}