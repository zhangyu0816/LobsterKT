package com.yimi.rentme.vm

import com.yimi.rentme.bean.MemberInfo
import com.yimi.rentme.databinding.AcBaseChatBinding

class BaseChatViewModel : BaseViewModel() {

    lateinit var binding: AcBaseChatBinding
    var isNotice = false
    var msgChannelType = 0 // 1：普通聊天  2：漂流瓶  3：闪聊

    /** 漂流瓶*/
    var driftBottleId = 0L

    override fun initViewModel() {
        binding.msgChannelType = msgChannelType
        binding.memberInfo = MemberInfo()
    }
}