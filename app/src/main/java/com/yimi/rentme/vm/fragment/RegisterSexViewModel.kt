package com.yimi.rentme.vm.fragment

import android.view.View
import com.yimi.rentme.MineApp
import com.yimi.rentme.databinding.FragRegisterSexBinding
import com.yimi.rentme.vm.BaseViewModel
import org.simple.eventbus.EventBus

class RegisterSexViewModel : BaseViewModel() {

    lateinit var binding: FragRegisterSexBinding

    override fun initViewModel() {
        binding.sexIndex = MineApp.threeInfo.unionSex
        binding.canNext = MineApp.threeInfo.unionSex!=2
    }

    /**
     * 选择性别
     */
    fun selectSex(index: Int) {
        binding.sexIndex = index
        binding.canNext = true
        MineApp.registerInfo.sex = index
        MineApp.sex = index
    }

    /**
     * 下一步
     */
    fun next(view: View) {
        if (binding.canNext) {
            EventBus.getDefault().post("", "lobsterRegisterNick")
        }
    }
}