package com.yimi.rentme.vm

import android.Manifest
import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import android.widget.Toast
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
import com.zb.baselibs.dialog.RemindDF
import com.zb.baselibs.utils.SCToastUtil
import com.zb.baselibs.utils.getInteger
import com.zb.baselibs.utils.permission.requestPermissionsForResult
import com.zb.baselibs.utils.saveInteger
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
            if (checkPermissionGranted(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
            ) {
                activity.startActivity<SelectImageActivity>(
                    Pair("isMore", true)
                )
            } else {
                if (getInteger("image_permission", 0) == 0) {
                    saveInteger("image_permission", 1)
                    RemindDF(activity).setTitle("权限说明")
                        .setContent(
                            "提供举报信息时需要使用上传图片功能，我们将会申请相机、存储权限：" +
                                    "\n 1、申请相机权限--上传图片时获取拍摄照片功能，" +
                                    "\n 2、申请存储权限--上传图片时获取保存和读取图片功能，" +
                                    "\n 3、若您点击“同意”按钮，我们方可正式申请上述权限，以便拍摄照片及选取照片，提交图片证据，" +
                                    "\n 4、若您点击“拒绝”按钮，我们将不再主动弹出该提示，您也无法使用上传图片功能，不影响使用其他的虾菇功能/服务，" +
                                    "\n 5、您也可以通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭相机、存储权限。"
                        ).setSureName("同意").setCancelName("拒绝")
                        .setCallBack(object : RemindDF.CallBack {
                            override fun sure() {
                                toSelectImage()
                            }
                        }).show(activity.supportFragmentManager)
                } else {
                    Toast.makeText(
                        activity,
                        "可通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭相机权限、存储权限。",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

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
     * 选择图片
     */
    private fun toSelectImage() {
        launchMain {
            activity.requestPermissionsForResult(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA, rationale = "为了更好的提供服务，需要获取相机权限、存储权限"
            )
            activity.startActivity<SelectImageActivity>(
                Pair("isMore", true)
            )
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