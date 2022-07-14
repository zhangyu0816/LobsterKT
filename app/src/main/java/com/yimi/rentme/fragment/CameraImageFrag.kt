package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragCameraImageBinding
import com.yimi.rentme.vm.fragment.CameraImageViewModel
import com.zb.baselibs.activity.BaseFragment

class CameraImageFrag(private val isMore: Boolean) : BaseFragment() {

    private val viewModel by getViewModel(CameraImageViewModel::class.java) {
        binding = mBinding as FragCameraImageBinding
        activity = this@CameraImageFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_camera_image
    }

    override fun initView() {
        viewModel.isMore = isMore
        viewModel.initViewModel()
    }
}