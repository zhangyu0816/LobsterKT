package com.yimi.rentme.vm.fragment

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.SystemClock
import android.provider.MediaStore
import android.view.View
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.FileModel
import com.yimi.rentme.databinding.FragCameraImageBinding
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.app.BaseApp
import kotlinx.coroutines.Job
import org.simple.eventbus.EventBus
import java.io.File

class CameraImageViewModel : BaseViewModel() {

    lateinit var binding: FragCameraImageBinding
    lateinit var adapter: BaseAdapter<String>
    private val imageList = ArrayList<String>()
    private val imageMap = HashMap<String, ArrayList<String>>()
    private val columns =
        arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
    private lateinit var cur: Cursor
    private var mPosition = -1

    lateinit var fileAdapter: BaseAdapter<FileModel>
    private val fileList = ArrayList<FileModel>()

    override fun initViewModel() {
        binding.title = "所有图片"
        binding.showFileModel = false
        fileList.add(FileModel("所有图片", "", 0))
        imageMap["所有图片"] = ArrayList()
        cur = activity.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null,
            null
        )!!
        adapter = BaseAdapter(activity, R.layout.item_camera_image, imageList, this)

        fileAdapter = BaseAdapter(activity, R.layout.item_file_model, fileList, this)
        // 获取相机内照片
        buildImagesBucketList()
    }

    override fun back(view: View) {
        super.back(view)
        EventBus.getDefault().post("", "lobsterCameraFinish")
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
    @SuppressLint("NotifyDataSetChanged")
    fun selectFileTitle(item: FileModel) {
        binding.showFileModel = false
        binding.title = item.fileName
        imageList.clear()
        MineApp.selectImageList.clear()
        mPosition = -1
        adapter.notifyDataSetChanged()
        imageList.addAll(imageMap[item.fileName]!!)
        imageList.reverse()
        adapter.notifyItemRangeChanged(0, imageList.size)
    }

    /**
     * 选择图片
     */
    fun upload(view: View) {
        EventBus.getDefault().post(MineApp.selectImageList,"lobsterUploadImageList")
        BaseApp.fixedThreadPool.execute {
            SystemClock.sleep(200)
            activity.runOnUiThread {
                back(binding.ivBack)
            }
        }
    }

    /**
     * 选择照片
     */
    fun selectImage(image: String, position: Int) {
        if (mPosition != -1) {
            adapter.notifyItemChanged(mPosition)
        }
        MineApp.selectImageList.clear()
        MineApp.selectImageList.add(image)
        adapter.notifyItemChanged(position)
        mPosition = position
    }

    /**
     * 获取本地图片
     */
    private fun buildImagesBucketList() {
        showLoading(Job(), "加载本地图片...")
        BaseApp.fixedThreadPool.execute {
            var file: File
            if (cur.moveToFirst()) {
                val photoPathIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                do {
                    val path = cur.getString(photoPathIndex)
                    val fileName = cur.getString(1)
                    var hasName = false
                    for (fileModel in fileList) {
                        if (fileName == null) break
                        if (fileModel.fileName == fileName) {
                            hasName = true
                            break
                        }
                    }
                    if (!hasName) {
                        fileList.add(FileModel(fileName!!, "", 0))
                        imageMap[fileName] = ArrayList()
                    }
                    file = File(path)
                    if (file.length() != 0L) {
                        imageMap[fileName]!!.add(path)
                        imageMap["所有图片"]!!.add(path)
                    }
                } while (cur.moveToNext())
            }
            cur.close()

            activity.runOnUiThread {
                dismissLoading()
                imageList.addAll(imageMap["所有图片"]!!)
                imageList.reverse()
                adapter.notifyItemRangeChanged(0, imageList.size)
                for (item in fileList) {
                    val temp = imageMap[item.fileName]!!
                    if (temp.isNotEmpty()) {
                        temp.reverse()
                        item.image = temp[0]
                        item.size = temp.size
                    }
                }
                fileAdapter.notifyItemRangeChanged(0, fileList.size)
            }
        }
    }
}