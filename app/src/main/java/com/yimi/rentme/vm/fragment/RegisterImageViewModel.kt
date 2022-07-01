package com.yimi.rentme.vm.fragment

import android.view.View
import com.yimi.rentme.MineApp
import com.yimi.rentme.databinding.FragRegisterImageBinding
import com.yimi.rentme.vm.BaseViewModel

class RegisterImageViewModel : BaseViewModel() {

    lateinit var binding: FragRegisterImageBinding

    override fun initViewModel() {
        binding.imageUrl = ""
        binding.canNext = false
    }

    /**
     * 选择图片
     */
    fun upload(view: View) {}

    /**
     * 下一步
     */
    fun next(view: View) {
        if(binding.canNext){

        }
    }
}