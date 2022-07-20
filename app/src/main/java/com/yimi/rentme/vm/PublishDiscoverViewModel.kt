package com.yimi.rentme.vm

import android.annotation.SuppressLint
import android.view.View
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.MNImageBrowserActivity
import com.yimi.rentme.activity.SelectImageActivity
import com.yimi.rentme.activity.VideoPlayActivity
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.SelectImage
import com.yimi.rentme.databinding.AcPublishDiscoverBinding
import com.yimi.rentme.dialog.SelectorDF
import com.yimi.rentme.utils.imagebrowser.MyMNImage
import com.yimi.rentme.utils.imagebrowser.OnDeleteListener
import com.yimi.rentme.utils.luban.PhotoFile
import com.yimi.rentme.utils.luban.PhotoManager
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.views.imagebrowser.base.ImageBrowserConfig
import org.jetbrains.anko.startActivity

class PublishDiscoverViewModel : BaseViewModel() {

    lateinit var binding: AcPublishDiscoverBinding
    lateinit var adapter: BaseAdapter<SelectImage>
    private val imageList = ArrayList<SelectImage>()
    private lateinit var photoManager: PhotoManager
    private var dataList = ArrayList<String>()

    override fun initViewModel() {
        dataList.add("发布照片")
        dataList.add("发布小视频")
        binding.content = ""
        binding.cityName = ""
        for (item in MineApp.selectImageList)
            imageList.add(item)
        val selectImage = SelectImage()
        selectImage.imageUrl = "add_image_icon"
        imageList.add(selectImage)
        adapter = BaseAdapter(activity, R.layout.item_discover_image, imageList, this)

        photoManager =
            PhotoManager(activity, mainDataSource, object : PhotoManager.OnUpLoadImageListener {
                override fun onSuccess() {
//                    comsub(photoManager.jointWebUrl(","))
                }

                override fun onError(file: PhotoFile?, errorMsg: String) {
                    super.onError(file, errorMsg)
                    dismissLoading()
                }
            })

        BaseApp.fixedThreadPool.execute {
            binding.cityName = if (MineApp.provinceId == 0L || MineApp.cityId == 0L) "全国"
            else BaseApp.cityDaoManager.getCityName(MineApp.provinceId, MineApp.cityId)
        }
    }

    override fun back(view: View) {
        super.back(view)
        MineApp.selectImageList.clear()
        activity.finish()
    }

    /**
     * 选择照片
     */
    fun previewImage(position: Int) {
        if (position == imageList.size - 1) { // 添加
            if (imageList.size == 1)
                SelectorDF(activity).setDataList(dataList)
                    .setCallBack(object : SelectorDF.CallBack {
                        override fun sure(position: Int) {
                            if (position == 0) {
                                activity.startActivity<SelectImageActivity>(
                                    Pair("isMore", true)
                                )
                            } else {
                                activity.startActivity<SelectImageActivity>(
                                    Pair("showVideo", true)
                                )
                            }
                        }
                    }).show(activity.supportFragmentManager)
            else if (imageList[0].videoUrl.isNotEmpty()) {
                activity.startActivity<SelectImageActivity>(
                    Pair("showVideo", true)
                )
            } else {
                activity.startActivity<SelectImageActivity>(
                    Pair("isMore", true)
                )
            }
        } else {
            if (imageList[0].videoUrl.isNotEmpty()) {
                activity.startActivity<VideoPlayActivity>(
                    Pair("videoUrl", imageList[0].videoUrl),
                    Pair("videoType", 2),
                    Pair("isDelete", true)
                )
            } else {
                val sourceImageList = ArrayList<String>()
                for (i in 0 until imageList.size - 1)
                    sourceImageList.add(imageList[i].imageUrl)
                MyMNImage.setIndex(0).setSourceImageList(sourceImageList)
                    .setTransformType(ImageBrowserConfig.TransformType.TransformDepthPage)
                    .setDeleteListener(object : OnDeleteListener {
                        override fun delete(index: Int) {
                            imageList.removeAt(index)
                            adapter.notifyItemRemoved(index)
                            adapter.notifyItemRangeChanged(index, imageList.size - index)
                            val selectImage = MineApp.selectImageList.removeAt(index)
                            for (item in MineApp.selectImageList) {
                                if (item.index > selectImage.index) {
                                    item.index = item.index - 1
                                }
                            }
                        }
                    })
                    .setCallBack(object : ImageBrowserConfig.StartBack {
                        override fun onStartActivity() {
                            activity.startActivity<MNImageBrowserActivity>()
                        }
                    })
                    .imageBrowser()
            }
        }
    }

    /**
     * 上传图片
     */
    @SuppressLint("NotifyDataSetChanged")
    fun uploadImageList(selectImageList: ArrayList<SelectImage>) {
        imageList.clear()
        adapter.notifyDataSetChanged()
        for (item in selectImageList) {
            imageList.add(item)
        }
        val selectImage = SelectImage()
        selectImage.imageUrl = "add_image_icon"
        imageList.add(selectImage)
        adapter.notifyItemRangeChanged(0, imageList.size)
    }

    /**
     * 删除视频
     */
    @SuppressLint("NotifyDataSetChanged")
    fun deleteVideo() {
        MineApp.selectImageList.clear()
        imageList.clear()
        adapter.notifyDataSetChanged()
        val selectImage = SelectImage()
        selectImage.imageUrl = "add_image_icon"
        imageList.add(selectImage)
        adapter.notifyItemRangeChanged(0, imageList.size)
    }

    /**
     * 选择城市
     */
    fun selectCity(view: View) {}

    /**
     * 发布
     */
    fun publish(view: View) {}
}