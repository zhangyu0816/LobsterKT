package com.yimi.rentme.vm.fragment

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.SystemClock
import android.view.View
import android.view.animation.Animation
import android.widget.Toast
import com.yimi.rentme.R
import com.yimi.rentme.activity.SearchActivity
import com.yimi.rentme.activity.SelectImageActivity
import com.yimi.rentme.activity.bottle.BottleActivity
import com.yimi.rentme.databinding.FragMainHomeBinding
import com.yimi.rentme.dialog.SelectorDF
import com.yimi.rentme.fragment.FollowFrag
import com.yimi.rentme.fragment.MemberDiscoverFrag
import com.yimi.rentme.fragment.MemberVideoFrag
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.RemindDF
import com.zb.baselibs.utils.getInteger
import com.zb.baselibs.utils.permission.requestPermissionsForResult
import com.zb.baselibs.utils.saveInteger
import com.zb.baselibs.views.replaceFragment
import org.jetbrains.anko.startActivity

class MainHomeViewModel : BaseViewModel() {

    lateinit var binding: FragMainHomeBinding
    private var pvhSY: PropertyValuesHolder? = null
    private var pvhSX: PropertyValuesHolder? = null
    private var pvhA: PropertyValuesHolder? = null
    private var pvh: ObjectAnimator? = null
    private var dataList = ArrayList<String>()

    override fun initViewModel() {
        dataList.add("发布照片")
        dataList.add("发布小视频")
        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 0.5f, 1f)
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 0.5f, 1f)
        pvhA = PropertyValuesHolder.ofFloat("alpha", 1f, 0f)
        pvh = ObjectAnimator.ofPropertyValuesHolder(binding.circleView, pvhSY, pvhSX, pvhA)
            .setDuration(2000)
        pvh!!.repeatCount = Animation.INFINITE
        BaseApp.fixedThreadPool.execute {
            SystemClock.sleep(200)
            activity.runOnUiThread {
                binding.publishLayout.visibility = View.VISIBLE
                pvh!!.start()
            }
        }

        selectIndex(1)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (pvh != null)
            pvh!!.cancel()
        pvhSY = null
        pvhSX = null
        pvhA = null
        pvh = null
    }

    /**
     * 选择
     */
    fun selectIndex(index: Int) {
        if (binding.index == index)
            return
        binding.index = index
        when (index) {
            0 -> activity.replaceFragment(FollowFrag(), R.id.home_content)
            1 -> activity.replaceFragment(MemberDiscoverFrag(0L), R.id.home_content)
            2 -> activity.replaceFragment(MemberVideoFrag(0L), R.id.home_content)
        }
    }

    /**
     * 搜索
     */
    fun toSearch(view: View) {
        activity.startActivity<SearchActivity>()
    }

    /**
     * 漂流瓶
     */
    fun toBottle(view: View) {
        activity.startActivity<BottleActivity>()
    }

    /**
     * 上传动态
     */
    fun publishDiscover(view: View) {
        if (checkPermissionGranted(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        ) {
            toSelectImage()
        } else {
            if (getInteger("image_permission", 0) == 0) {
                saveInteger("image_permission", 1)
                RemindDF(activity).setTitle("权限说明")
                    .setContent(
                        "在使用发布动态功能，包括图文、视频时，我们将会申请相机、存储、麦克风权限：" +
                                "\n 1、申请相机权限--发布动态时获取拍摄照片，录制视频功能，" +
                                "\n 2、申请存储权限--发布动态时获取保存和读取图片、视频，" +
                                "\n 3、申请麦克风权限--发布视频动态时获取录制视频音频功能，" +
                                "\n 4、若您点击“同意”按钮，我们方可正式申请上述权限，以便正常发布图文动态、视频动态，" +
                                "\n 5、若您点击“拒绝”按钮，我们将不再主动弹出该提示，您也无法使用发布动态功能，不影响使用其他的虾菇功能/服务，" +
                                "\n 6、您也可以通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭相机、存储、麦克风权限。"
                    ).setSureName("同意").setCancelName("拒绝")
                    .setCallBack(object : RemindDF.CallBack {
                        override fun sure() {
                            toSelectImage()
                        }
                    }).show(activity.supportFragmentManager)
            } else {
                Toast.makeText(
                    activity,
                    "可通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭相机权限、存储权限、麦克风权限。",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * 选择发布动态
     */
    private fun toSelectImage() {
        launchMain {
            activity.requestPermissionsForResult(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO, rationale = "为了更好的提供服务，需要获取相机权限、存储权限、麦克风权限"
            )
            SelectorDF(activity).setDataList(dataList).setCallBack(object : SelectorDF.CallBack {
                override fun sure(position: Int) {
                    if (position == 0) {
                        activity.startActivity<SelectImageActivity>(
                            Pair("isMore", true),
                            Pair("isPublish", true)
                        )
                    } else {
                        activity.startActivity<SelectImageActivity>(
                            Pair("showVideo", true),
                            Pair("isPublish", true)
                        )
                    }
                }
            }).show(activity.supportFragmentManager)
        }
    }
}