package com.yimi.rentme.vm

import com.yimi.rentme.bean.MemberInfo
import com.yimi.rentme.databinding.AcDiscoverListBinding

class DiscoverListViewModel : BaseViewModel() {

    lateinit var binding: AcDiscoverListBinding
    var otherUserId = 0L
    lateinit var memberInfo: MemberInfo

    override fun initViewModel() {

    }
}