package com.yimi.rentme.vm.fragment

import android.Manifest
import android.view.View
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.SelectImageActivity
import com.yimi.rentme.bean.SelectImage
import com.yimi.rentme.databinding.FragRegisterImageBinding
import com.yimi.rentme.utils.luban.PhotoFile
import com.yimi.rentme.utils.luban.PhotoManager
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.RemindDF
import com.zb.baselibs.utils.SCToastUtil
import com.zb.baselibs.utils.getInteger
import com.zb.baselibs.utils.permission.requestPermissionsForResult
import com.zb.baselibs.utils.saveInteger
import kotlinx.coroutines.Job
import org.jetbrains.anko.startActivity
import org.simple.eventbus.EventBus
import java.io.File

class RegisterImageViewModel : BaseViewModel() {

    lateinit var binding: FragRegisterImageBinding
    private lateinit var photoManager: PhotoManager

    override fun initViewModel() {
        binding.imageUrl = ""
        binding.canNext = false
        photoManager =
            PhotoManager(activity, mainDataSource, object : PhotoManager.OnUpLoadImageListener {
                override fun onSuccess() {
                    binding.imageUrl = photoManager.jointWebUrl(",")
                    MineApp.registerInfo.image = binding.imageUrl!!
                    MineApp.registerInfo.moreImages = binding.imageUrl!!
                    binding.canNext = true
                    photoManager.deleteAllFile()
                    MineApp.selectImageList.clear()
                    dismissLoading()
                }

                override fun onError(file: PhotoFile?, errorMsg: String) {
                    SCToastUtil.showToast(activity, errorMsg, 2)
                    dismissLoading()
                }
            })
    }

    /**
     * 选择图片
     */
    fun upload(view: View) {
        if (checkPermissionGranted(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            activity.startActivity<SelectImageActivity>(
                Pair("showBottom", true)
            )
        } else {
            if (getInteger("image_permission", 0) == 0) {
                saveInteger("image_permission", 1)
                RemindDF(activity).setTitle("权限说明")
                    .setContent(
                        "提交本人真实头像时需要使用上传图片功能，我们将会申请相机、存储权限：" +
                                "\n 1、申请相机权限--上传图片时获取拍摄照片功能，" +
                                "\n 2、申请存储权限--上传图片时获取保存和读取图片功能，" +
                                "\n 3、若您点击“同意”按钮，我们方可正式申请上述权限，以便拍摄照片及选取照片，完善个人信息，" +
                                "\n 4、若您点击“拒绝”按钮，我们将不再主动弹出该提示，您也无法使用上传图片功能，不影响使用其他的虾菇功能/服务，" +
                                "\n 5、您也可以通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭相机、存储权限。"
                    ).setSureName("同意").setCancelName("拒绝")
                    .setCallBack(object : RemindDF.CallBack {
                        override fun sure() {
                            selectImage()
                        }
                    }).show(activity.supportFragmentManager)
            } else {
                SCToastUtil.showToast(
                    activity,
                    "可通过“手机设置--应用--${BaseApp.context.resources.getString(R.string.app_name)}--权限”，手动开启或关闭相机、存储权限。",
                    2
                )
            }
        }
    }

    /**
     * 选择图片
     */
    private fun selectImage() {
        launchMain {
            activity.requestPermissionsForResult(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, rationale = "为了更好的提供服务，需要获取相机权限和存储权限"
            )
            activity.startActivity<SelectImageActivity>(
                Pair("showBottom", true)
            )
        }
    }

    /**
     * 上传图片
     */
    fun uploadImageList(selectImageList: ArrayList<SelectImage>) {
        showLoading(Job(), "上传头像...")
        photoManager.addFileUpload(0, File(selectImageList[0].imageUrl))
    }

    /**
     * 下一步
     */
    fun next(view: View) {
        if (binding.canNext) {
            EventBus.getDefault().post("", "lobsterRegisterMember")
        }
    }
}