package com.yimi.rentme.activity

import android.content.Intent
import com.umeng.socialize.UMShareAPI
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
            viewModel.isFollow = extras.getBoolean("isFollow")
        }
        viewModel.initViewModel()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val extras = intent!!.extras
        if (extras != null) {
            viewModel.otherUserId = extras.getLong("otherUserId")
            viewModel.showLike = extras.getBoolean("showLike")
            viewModel.isFollow = extras.getBoolean("isFollow")
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

    /**
     * 分享重写
     */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data)
    }
}