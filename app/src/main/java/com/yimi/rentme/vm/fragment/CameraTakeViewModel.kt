package com.yimi.rentme.vm.fragment

import android.os.Build
import android.os.SystemClock
import android.view.View
import androidx.annotation.RequiresApi
import com.yimi.rentme.MineApp
import com.yimi.rentme.activity.PublishDiscoverActivity
import com.yimi.rentme.bean.SelectImage
import com.yimi.rentme.databinding.FragCameraTakeBinding
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.SCToastUtil
import org.jetbrains.anko.startActivity
import org.simple.eventbus.EventBus


class CameraTakeViewModel : BaseViewModel() {

    lateinit var binding: FragCameraTakeBinding
    var isMore = false
    var isPublish = false

    override fun initViewModel() {
        binding.sizeIndex = 0
        binding.lightIndex = 0
        binding.isUpload = false
        binding.imageUrl = ""
    }

    override fun back(view: View) {
        super.back(view)
        activity.finish()
    }

    /**
     * 重置
     */
    fun reset(view: View) {
        binding.isUpload = false
        binding.imageUrl = ""
    }

    /**
     * 上传照片
     */
    fun upload(view: View) {
        if (binding.imageUrl!!.isNotEmpty()) {
            if (isMore) {
                val index = MineApp.selectImageList.size
                if (index == 9) {
                    SCToastUtil.showToast(activity, "只能选择9张照片", 2)
                    return
                }
                val selectImage = SelectImage()
                selectImage.imageUrl = binding.imageUrl!!
                selectImage.index = index + 1
                MineApp.selectImageList.add(selectImage)
            } else {
                MineApp.selectImageList.clear()
                val selectImage = SelectImage()
                selectImage.imageUrl = binding.imageUrl!!
                MineApp.selectImageList.add(selectImage)
            }

            if (isPublish)
                activity.startActivity<PublishDiscoverActivity>()
            else
                EventBus.getDefault().post(MineApp.selectImageList, "lobsterUploadImageList")
            BaseApp.fixedThreadPool.execute {
                SystemClock.sleep(200)
                activity.runOnUiThread {
                    back(binding.ivBack)
                }
            }
        }
    }

    /**
     * 改变照片格式
     */
    fun changeSizeIndex(sizeIndex: Int) {
        binding.sizeIndex = sizeIndex
        EventBus.getDefault().post("改变照片格式", "lobsterChangeSize")
    }

    /**
     * 是否开灯
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun changeLightIndex(lightIndex: Int) {
        binding.lightIndex = lightIndex
        EventBus.getDefault().post("打开闪光灯", "lobsterChangeLight")
    }

    /**
     * 前后摄像头
     */
    fun changeCameraId(view: View) {
        EventBus.getDefault().post("前后摄像头", "lobsterChangeCameraId")
    }

    /**
     * 拍照
     */
    fun createPhoto(view: View) {
        EventBus.getDefault().post("拍照", "lobsterTakePhoto")

    }


}