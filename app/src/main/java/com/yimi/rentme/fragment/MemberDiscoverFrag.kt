package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragMemberDiscoverBinding
import com.yimi.rentme.vm.fragment.MemberDiscoverViewModel
import com.zb.baselibs.activity.BaseFragment
import org.simple.eventbus.Subscriber

class MemberDiscoverFrag(private val otherUserId: Long) : BaseFragment() {

    private val viewModel by getViewModel(MemberDiscoverViewModel::class.java) {
        binding = mBinding as FragMemberDiscoverBinding
        activity = this@MemberDiscoverFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_member_discover
    }

    override fun initView() {
        needEvenBus = true
        viewModel.otherUserId = otherUserId
        viewModel.initViewModel()
    }

    /**
     * 点赞
     */
    @Subscriber(tag = "lobsterDoLike")
    private fun lobsterDoLike(data: String) {
        viewModel.doLike(data.toLong())
    }
}