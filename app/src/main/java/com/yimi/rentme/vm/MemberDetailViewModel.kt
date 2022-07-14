package com.yimi.rentme.vm

import com.yimi.rentme.databinding.AcMemberDetailBinding

class MemberDetailViewModel : BaseViewModel() {

    lateinit var binding: AcMemberDetailBinding
    var otherUserId = 0L
    var showLike = false

    override fun initViewModel() {

    }
}