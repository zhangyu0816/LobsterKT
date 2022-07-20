package com.yimi.rentme.vm

import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcSelectImageBinding
import com.yimi.rentme.fragment.CameraImageFrag
import com.yimi.rentme.fragment.CameraTakeFrag
import com.yimi.rentme.fragment.CameraVideoFrag
import com.zb.baselibs.views.replaceFragment

class SelectImageViewModel : BaseViewModel() {

    lateinit var binding: AcSelectImageBinding
    var isMore = false
    var showVideo = false
    var isPublish = false

    override fun initViewModel() {
        binding.showVideo = showVideo
        selectIndex(0)
    }

    /**
     * 底部导航
     */
    fun selectIndex(index: Int) {
        binding.index = index
        when (index) {
            0 -> activity.replaceFragment(
                CameraImageFrag(isMore, showVideo, isPublish),
                R.id.camera_content
            )
            1 -> activity.replaceFragment(CameraVideoFrag(isPublish), R.id.camera_content)
            2 -> activity.replaceFragment(CameraTakeFrag(isMore, isPublish), R.id.camera_content)
        }
    }
}