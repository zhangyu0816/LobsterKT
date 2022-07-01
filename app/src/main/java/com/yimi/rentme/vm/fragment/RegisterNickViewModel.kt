package com.yimi.rentme.vm.fragment

import android.view.View
import com.yimi.rentme.MineApp
import com.yimi.rentme.databinding.FragRegisterNickBinding
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.utils.afterTextChanged
import org.simple.eventbus.EventBus

class RegisterNickViewModel : BaseViewModel() {

    lateinit var binding: FragRegisterNickBinding

    override fun initViewModel() {
        binding.canNext = false
        MineApp.registerInfo.name = MineApp.threeInfo.unionNick
        binding.edNick.setText(MineApp.threeInfo.unionNick)
        binding.edNick.afterTextChanged {
            MineApp.registerInfo.name = it
            binding.canNext = it.isNotEmpty()
        }
    }

    /**
     * 下一步
     */
    fun next(view: View) {
        if (binding.canNext) {
            EventBus.getDefault().post("", "lobsterRegisterBirthday")
        }
    }
}