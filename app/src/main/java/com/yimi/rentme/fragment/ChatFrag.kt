package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragChatBinding
import com.yimi.rentme.vm.fragment.ChatViewModel
import com.zb.baselibs.activity.BaseFragment
import com.zb.baselibs.mimc.CustomMessageBody
import org.simple.eventbus.Subscriber

class ChatFrag : BaseFragment() {

    private val viewModel by getViewModel(ChatViewModel::class.java) {
        binding = mBinding as FragChatBinding
        activity = this@ChatFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_chat
    }

    override fun initView() {
        needEvenBus = true
        viewModel.initViewModel()
    }

    /**
     * 更新聊天列表
     */
    @Subscriber(tag = "lobsterUpdateChatList")
    private fun lobsterUpdateChat(data: String) {
    }
}