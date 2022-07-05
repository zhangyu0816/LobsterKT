package com.yimi.rentme.vm

import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcSelectImageBinding
import com.yimi.rentme.fragment.CameraImageFrag
import com.yimi.rentme.fragment.CameraTakeFrag
import com.zb.baselibs.views.replaceFragment

class SelectImageViewModel : BaseViewModel() {

    lateinit var binding: AcSelectImageBinding
    var isMore = false
    var showBottom = false
    var showVideo = false

    override fun initViewModel() {
        binding.showVideo = showVideo
        binding.showBottom = showBottom
        selectIndex(0)
    }

    fun finish() {
        MineApp.selectImageList.clear()
        activity.finish()
    }

    /**
     * 底部导航
     */
    fun selectIndex(index: Int) {
        binding.index = index
        when (index) {
            0 -> activity.replaceFragment(CameraImageFrag(), R.id.camera_content)
            2 -> activity.replaceFragment(CameraTakeFrag(), R.id.camera_content)
        }
    }
}