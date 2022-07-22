package com.yimi.rentme.activity

import android.content.Intent
import com.umeng.socialize.UMShareAPI
import com.yimi.rentme.R
import com.yimi.rentme.bean.ContactNum
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
            viewModel.contactNum = extras.getSerializable("contactNum") as ContactNum
        }
        viewModel.initViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
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