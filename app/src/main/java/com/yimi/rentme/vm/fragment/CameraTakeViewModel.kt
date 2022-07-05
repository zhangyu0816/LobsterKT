package com.yimi.rentme.vm.fragment

import android.view.View
import com.yimi.rentme.databinding.FragCameraTakeBinding
import com.yimi.rentme.vm.BaseViewModel

class CameraTakeViewModel : BaseViewModel() {

    lateinit var binding: FragCameraTakeBinding

    override fun initViewModel() {
        binding.sizeIndex = 0
        binding.lightIndex = 0
    }

    /**
     * 重置
     */
    fun reset(view: View) {}

    /**
     * 上传照片
     */
    fun upload(view: View) {}

    /**
     * 改变照片格式
     */
    fun changeSizeIndex(sizeIndex: Int) {
        binding.sizeIndex = sizeIndex
    }

    /**
     * 是否开灯
     */
    fun changeLightIndex(lightIndex:Int){
        binding.lightIndex = lightIndex
    }

    /**
     * 前后摄像头
     */
    fun changeCameraId(view: View){}

    /**
     * 拍照
     */
    fun createPhoto(view: View){}
}