package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragMemberVideoBinding
import com.yimi.rentme.vm.fragment.MemberVideoViewModel
import com.zb.baselibs.activity.BaseFragment
import org.simple.eventbus.Subscriber

class MemberVideoFrag(private val otherUserId: Long) : BaseFragment() {

    private val viewModel by getViewModel(MemberVideoViewModel::class.java) {
        binding = mBinding as FragMemberVideoBinding
        activity = this@MemberVideoFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_member_video
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

    /**
     * 取消点赞
     */
    @Subscriber(tag = "lobsterCancelLike")
    private fun lobsterCancelLike(data: String) {
        viewModel.cancelLike(data.toLong())
    }
}