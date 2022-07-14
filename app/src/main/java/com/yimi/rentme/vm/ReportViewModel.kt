package com.yimi.rentme.vm

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.MNImageBrowserActivity
import com.yimi.rentme.activity.SelectImageActivity
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.Report
import com.yimi.rentme.bean.SelectImage
import com.yimi.rentme.databinding.AcReportBinding
import com.yimi.rentme.utils.imagebrowser.MyMNImage
import com.yimi.rentme.utils.imagebrowser.OnDeleteListener
import com.yimi.rentme.utils.luban.PhotoFile
import com.yimi.rentme.utils.luban.PhotoManager
import com.zb.baselibs.utils.SCToastUtil
import com.zb.baselibs.views.imagebrowser.base.ImageBrowserConfig
import kotlinx.coroutines.Job
import org.jetbrains.anko.startActivity

class ReportViewModel : BaseViewModel() {

    lateinit var binding: AcReportBinding
    var otherUserId = 0L
    lateinit var adapter: BaseAdapter<Report>
    private var mPosition = -1
    lateinit var imageAdapter: BaseAdapter<String>
    private val imageList = ArrayList<String>()
    private lateinit var photoManager: PhotoManager

    override fun initViewModel() {
        binding.title = "举报内容"
        binding.right = "举报"
        binding.content = ""

        adapter = BaseAdapter(activity, R.layout.item_report, MineApp.reportList, this)

        imageList.add("add_image_icon")
        imageAdapter = BaseAdapter(activity, R.layout.item_report_image, imageList, this)

        photoManager =
            PhotoManager(activity, mainDataSource, object : PhotoManager.OnUpLoadImageListener {
                override fun onSuccess() {
                    comsub(photoManager.jointWebUrl(","))
                }

                override fun onError(file: PhotoFile?, errorMsg: String) {
                    super.onError(file, errorMsg)
                    dismissLoading()
                }
            })
    }

    override fun back(view: View) {
        super.back(view)
        activity.finish()
    }

    override fun right(view: View) {
        super.right(view)
        if (mPosition == -1) {
            SCToastUtil.showToast(activity, "请选择举报类型", 2)
            return
        }
        if (binding.content!!.isEmpty()) {
            SCToastUtil.showToast(activity, "请填写举报理由", 2)
            return
        }
        if (imageList.size == 1) {
            SCToastUtil.showToast(activity, "请上传图片证据", 2)
            return
        }

        val images = ArrayList<String>()
        for (url in imageList) {
            if (!TextUtils.equals(url, "add_image_icon")) {
                images.add(url)
            }
        }
        showLoading(Job(), "提交举报信息...")
        photoManager.addFiles(images, object : PhotoManager.CompressOver {
            override fun success() {
                photoManager.reUploadByUnSuccess()
            }
        })
    }

    /**
     * 选择类型
     */
    fun selectPosition(position: Int) {
        adapter.setSelectIndex(position)
        if (mPosition != -1)
            adapter.notifyItemChanged(mPosition)
        adapter.notifyItemChanged(position)
        mPosition = position
    }

    /**
     * 选择照片
     */
    fun previewImage(position: Int) {
        if (position == imageList.size - 1) { // 添加
            activity.startActivity<SelectImageActivity>(
                Pair("showBottom", true),
                Pair("isMore", true)
            )
        } else {
            val sourceImageList = ArrayList<String>()
            for (i in 0 until imageList.size - 1)
                sourceImageList.add(imageList[i])
            MyMNImage.setIndex(0).setSourceImageList(sourceImageList)
                .setTransformType(ImageBrowserConfig.TransformType.TransformDepthPage)
                .setDeleteListener(object : OnDeleteListener {
                    override fun delete(index: Int) {
                        imageList.removeAt(index)
                        imageAdapter.notifyItemRemoved(index)
                        imageAdapter.notifyItemRangeChanged(index, imageList.size - index)
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

    /**
     * 上传图片
     */
    @SuppressLint("NotifyDataSetChanged")
    fun uploadImageList(selectImageList: ArrayList<SelectImage>) {
        imageList.clear()
        imageAdapter.notifyDataSetChanged()
        for (item in selectImageList) {
            imageList.add(item.imageUrl)
        }
        imageList.add("add_image_icon")
        imageAdapter.notifyItemRangeChanged(0, imageList.size)
    }

    /**
     * 提交举报
     */
    private fun comsub(images: String) {
        photoManager.deleteAllFile()
        mainDataSource.enqueue({
            comsub(
                MineApp.reportList[mPosition].id,
                otherUserId, binding.content!!, images
            )
        }) {
            onSuccess {
                SCToastUtil.showToast(activity, "举报信息已提交，我们会审核后进行处理", 2)
                MineApp.selectImageList.clear()
                dismissLoading()
                activity.finish()
            }
            onFailed {
                dismissLoading()
            }
        }
    }
}