package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragFollowBinding
import com.yimi.rentme.vm.fragment.FollowViewModel
import com.zb.baselibs.activity.BaseFragment
import org.simple.eventbus.Subscriber

class FollowFrag : BaseFragment() {

    private val viewModel by getViewModel(FollowViewModel::class.java) {
        binding = mBinding as FragFollowBinding
        activity = this@FollowFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_follow
    }

    override fun initView() {
        needEvenBus = true
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

    /**
     * 取消关注
     */
    @Subscriber(tag = "lobsterUpdateFollow")
    private fun lobsterUpdateFollow(data: Boolean) {
        viewModel.updateFollow(data)
    }

    /**
     * 取消关注
     */
    @Subscriber(tag = "lobsterUpdateFollowFrag")
    private fun lobsterUpdateFollowFrag(data: String) {
        viewModel.updateFollowFrag(data.toLong())
    }
}