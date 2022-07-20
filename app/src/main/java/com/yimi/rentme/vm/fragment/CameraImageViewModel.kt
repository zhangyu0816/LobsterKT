package com.yimi.rentme.vm.fragment

import android.annotation.SuppressLint
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.os.SystemClock
import android.provider.MediaStore
import android.view.View
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.PublishDiscoverActivity
import com.yimi.rentme.activity.VideoPlayActivity
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.FileModel
import com.yimi.rentme.bean.SelectImage
import com.yimi.rentme.databinding.FragCameraImageBinding
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.SCToastUtil
import kotlinx.coroutines.Job
import org.jetbrains.anko.startActivity
import org.simple.eventbus.EventBus
import java.io.File

class CameraImageViewModel : BaseViewModel() {

    lateinit var binding: FragCameraImageBinding
    var isMore = false
    var showVideo = false
    var isPublish = false
    lateinit var adapter: BaseAdapter<String>
    private val imageList = ArrayList<String>()
    private val imageMap = HashMap<String, ArrayList<String>>()
    private val columns =
        arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
    private val videoColumns =
        arrayOf(
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE
        )
    private lateinit var cur: Cursor
    private var mPosition = -1

    lateinit var fileAdapter: BaseAdapter<FileModel>
    private val fileList = ArrayList<FileModel>()

    override fun initViewModel() {
        binding.title = if (showVideo) "视频" else "所有图片"
        binding.showVideo = showVideo
        binding.showFileModel = false
        fileList.add(FileModel("所有图片", "", 0))
        imageMap["所有图片"] = ArrayList()
        cur = if (showVideo)
            activity.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                videoColumns,
                MediaStore.Video.Media.MIME_TYPE + "=?",
                arrayOf("video/mp4"),
                MediaStore.Video.Media.DATE_ADDED + " DESC "
            )!!
        else
            activity.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null,
                null
            )!!
        if (!isMore)
            MineApp.selectImageList.clear()
        adapter = BaseAdapter(activity, R.layout.item_camera_image, imageList, this)

        fileAdapter = BaseAdapter(activity, R.layout.item_file_model, fileList, this)

        if (showVideo)
            getAllLocalVideos()
        else // 获取相机内照片
            buildImagesBucketList()
    }

    override fun back(view: View) {
        super.back(view)
        activity.finish()
    }

    /**
     * 选择文件集合
     */
    fun selectTitle(view: View) {
        if (!showVideo)
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
        adapter.notifyDataSetChanged()
        imageList.addAll(imageMap[item.fileName]!!)
        imageList.reverse()
        adapter.notifyItemRangeChanged(0, imageList.size)
        if (!isMore) {
            MineApp.selectImageList.clear()
            mPosition = -1
            adapter.notifyDataSetChanged()
        }
    }

    /**
     * 选择图片
     */
    fun upload(view: View) {
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

    /**
     * 选择照片
     */
    @SuppressLint("NotifyDataSetChanged")
    fun selectImage(image: String, position: Int) {
        if (showVideo) {
            MineApp.selectImageList.clear()
            val media = MediaMetadataRetriever()
            media.setDataSource(image)
            val selectImage = SelectImage()
            selectImage.videoUrl = image
            selectImage.bitmap = media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            MineApp.selectImageList.add(selectImage)
            activity.startActivity<VideoPlayActivity>(
                Pair("videoUrl", image),
                Pair("videoType", 2),
                Pair("isUpload", true),
                Pair("isPublish", isPublish)
            )
        } else if (isMore) {
            var has = false
            for (item in MineApp.selectImageList) {
                if (item.imageUrl == image) {
                    has = true
                    break
                }
            }

            if (has) {
                val iterator = MineApp.selectImageList.iterator()
                var index = 0
                while (iterator.hasNext()) {
                    val item = iterator.next()
                    if (image == item.imageUrl) {
                        iterator.remove()
                        index = item.index
                        break
                    }
                }
                for (item in MineApp.selectImageList) {
                    if (item.index > index) {
                        item.index = item.index - 1
                    }
                }
                adapter.notifyItemRangeChanged(0, imageList.size)
            } else {
                val index = MineApp.selectImageList.size
                if (index == 9) {
                    SCToastUtil.showToast(activity, "只能选择9张照片", 2)
                    return
                }
                val selectImage = SelectImage()
                selectImage.imageUrl = image
                selectImage.index = index + 1
                MineApp.selectImageList.add(selectImage)
                adapter.notifyItemChanged(position)
            }
        } else {
            adapter.notifyItemChanged(position)
            if (mPosition != -1)
                adapter.notifyItemChanged(mPosition)
            MineApp.selectImageList.clear()
            val selectImage = SelectImage()
            selectImage.imageUrl = image
            MineApp.selectImageList.add(selectImage)
            mPosition = position
        }
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

    /**
     * 获取本地视频
     */
    private fun getAllLocalVideos() {
        showLoading(Job(), "加载本地视频...")
        BaseApp.fixedThreadPool.execute {
            try {
                while (cur.moveToNext()) {
                    val size: Long =
                        cur.getLong(cur.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)) // 大小
                    if (size < 20 * 1024 * 1024) { //<600M
                        val path =
                            cur.getString(cur.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)) // 路径
                        imageList.add(path)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cur.close()
            }
            activity.runOnUiThread {
                dismissLoading()
                adapter.notifyItemRangeChanged(0, imageList.size)
            }
        }
    }
}