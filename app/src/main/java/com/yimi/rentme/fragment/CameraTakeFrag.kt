package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragCameraTakeBinding
import com.yimi.rentme.vm.fragment.CameraTakeViewModel
import com.zb.baselibs.activity.BaseFragment

class CameraTakeFrag : BaseFragment() {

    private val viewModel by getViewModel(CameraTakeViewModel::class.java) {
        binding = mBinding as FragCameraTakeBinding
        activity = this@CameraTakeFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_camera_take
    }

    override fun initView() {
        viewModel.initViewModel()
    }
}