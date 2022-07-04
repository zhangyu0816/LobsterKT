package com.yimi.rentme.vm

import android.view.View
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.FileModel
import com.yimi.rentme.databinding.AcSelectImageBinding
import com.yimi.rentme.fragment.CameraImageFrag
import com.zb.baselibs.views.replaceFragment
import org.simple.eventbus.EventBus

class SelectImageViewModel : BaseViewModel() {

    lateinit var binding: AcSelectImageBinding
    var isMore = false
    var showBottom = false
    var showVideo = false
    lateinit var adapter: BaseAdapter<FileModel>

    override fun initViewModel() {
        binding.title = "所有图片"
        binding.showVideo = showVideo
        binding.showBottom = showBottom
        binding.showFileModel = false
        MineApp.fileList.clear()
        adapter = BaseAdapter(activity, R.layout.item_file_model, MineApp.fileList, this)
        selectIndex(0)
    }

    override fun back(view: View) {
        super.back(view)
        MineApp.selectImageList.clear()
        activity.finish()
    }

    /**
     * 更新文件集合
     */
    fun updateFileModel() {
        adapter.notifyItemRangeChanged(0, MineApp.fileList.size)
    }

    /**
     * 选择文件集合
     */
    fun selectTitle(view: View) {
        binding.showFileModel = !binding.showFileModel
    }

    /**
     * 标题集合
     */
    fun selectFileTitle(item: FileModel) {
        binding.showFileModel = false
        binding.title = item.fileName
        EventBus.getDefault().post(item.fileName, "lobsterUpdateImageList")
    }

    /**
     * 底部导航
     */
    fun selectIndex(index: Int) {
        binding.index = index
        when (index) {
            0 -> activity.replaceFragment(CameraImageFrag(), R.id.camera_content)
        }
    }

    /**
     * 选择图片
     */
    fun upload(view: View) {
        
    }
}