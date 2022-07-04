package com.yimi.rentme.fragment

import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.yimi.rentme.databinding.FragRegisterImageBinding
import com.yimi.rentme.vm.fragment.RegisterImageViewModel
import com.zb.baselibs.activity.BaseFragment
import org.simple.eventbus.Subscriber

class RegisterImageFrag : BaseFragment() {

    private val viewModel by getViewModel(RegisterImageViewModel::class.java) {
        binding = mBinding as FragRegisterImageBinding
        activity = this@RegisterImageFrag.activity as AppCompatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.frag_register_image
    }

    override fun initView() {
        needEvenBus = true
        viewModel.initViewModel()
    }

    /**
     * 上传图片
     */
    @Subscriber(tag = "lobsterUploadImageList")
    private fun lobsterUploadImageList(data: ArrayList<String>) {
        viewModel.uploadImageList(data)
    }
}