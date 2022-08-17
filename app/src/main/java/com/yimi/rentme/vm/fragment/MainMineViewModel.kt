package com.yimi.rentme.vm.fragment

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.view.animation.Animation
import android.widget.Toast
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.FCLActivity
import com.yimi.rentme.activity.OpenVipActivity
import com.yimi.rentme.activity.SelectImageActivity
import com.yimi.rentme.databinding.FragMainMineBinding
import com.yimi.rentme.dialog.SelectorDF
import com.yimi.rentme.dialog.VipAdDF
import com.yimi.rentme.fragment.MemberDiscoverFrag
import com.yimi.rentme.fragment.MemberVideoFrag
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.RemindDF
import com.zb.baselibs.utils.dip2px
import com.zb.baselibs.utils.getInteger
import com.zb.baselibs.utils.getLong
import com.zb.baselibs.utils.permission.requestPermissionsForResult
import com.zb.baselibs.utils.saveInteger
import com.zb.baselibs.views.replaceFragment
import org.jetbrains.anko.startActivity

class MainMineViewModel : BaseViewModel() {

    lateinit var binding: FragMainMineBinding
    private var pvh: ObjectAnimator? = null
    private var pvhSY: PropertyValuesHolder? = null
    private var pvhSX: PropertyValuesHolder? = null
    private var pvhA: PropertyValuesHolder? = null

    private var index = 1
    private val mHandler = Handler()
    private val ra: Runnable = object : Runnable {
        override fun run() {
            index++
            if (index % 2 == 0)
                binding.vipLinear.setBackgroundResource(R.mipmap.mine_big_vip_item_bg)
            else
                binding.vipLinear.setBackgroundResource(R.mipmap.mine_vip_item_bg)
            mHandler.postDelayed(this, 500L)
        }
    }
    private var dataList = ArrayList<String>()

    override fun initViewModel() {
        dataList.add("发布照片")
        dataList.add("发布小视频")

        binding.mineInfo = MineApp.mineInfo
        binding.contactNum = MineApp.contactNum
        binding.hasNewBeLike =
            MineApp.contactNum.beLikeCount > getInteger("beLikeCount_${getLong("userId")}")
        binding.hasNewVisitor =
            MineApp.contactNum.visitorCount > getInteger("visitorCount_${getLong("userId")}")
        binding.isFirstOpen = MineApp.isFirstOpen

        getAi()

        selectIndex(0)

        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 0.5f, 1f)
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 0.5f, 1f)
        pvhA = PropertyValuesHolder.ofFloat("alpha", 1f, 0f)
        pvh = ObjectAnimator.ofPropertyValuesHolder(binding.circleView, pvhSY, pvhSX, pvhA)
            .setDuration(2000)
        pvh!!.repeatCount = Animation.INFINITE

        if (!MineApp.isFirstOpen) {
            binding.vipTitle.visibility = View.VISIBLE
            binding.vipInfo.visibility = View.VISIBLE
            binding.vipLinear.setBackgroundResource(R.mipmap.mine_share_item_bg)
        } else mHandler.postDelayed(ra, 500)

        BaseApp.fixedThreadPool.execute {
            SystemClock.sleep(200L)
            activity.runOnUiThread {
                goAnimator(binding.ivGo, 0.8f, 1.0f, 800L)
                val height: Int = BaseApp.context.dip2px(30f) - binding.topLinear.getHeight()
                binding.appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                    binding.showBg = verticalOffset <= height
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (pvh != null)
            pvh!!.cancel()
        pvhSY = null
        pvhSX = null
        pvhA = null
        pvh = null
        stopGo()
    }

    /**
     * 更新访问数量
     */
    fun visitor() {
        binding.contactNum = MineApp.contactNum
    }

    /**
     * 新闻
     */
    fun toNews(view: View) {
//        ActivityUtils.getMineNewsManager()
    }

    /**
     * 打赏排行
     */
    fun toReward(view: View) {
//        ActivityUtils.getHomeRewardList(0, BaseActivity.userId)
    }

    /**
     * 设置
     */
    fun toSetting(view: View) {
//        ActivityUtils.getMineSetting()
    }

    /**
     * 编辑个人信息
     */
    fun toEditMember(view: View) {
//        ActivityUtils.getMineEditMember()
    }

    /**
     * 开通VIP
     */
    fun openVip(view: View) {
        activity.startActivity<OpenVipActivity>()
    }

    /**
     * FCL
     */
    fun contactNumDetail(index: Int) {
        when (index) {
            2 -> {
                if (MineApp.mineInfo.memberType == 2) {
                    saveInteger("beLikeCount_${getLong("userId")}", MineApp.contactNum.beLikeCount)
                    binding.hasNewBeLike = false
                    activity.startActivity<FCLActivity>(
                        Pair("index", 2)
                    )
                } else
                    VipAdDF(activity).setMainDataSource(mainDataSource).setType(4)
                        .show(activity.supportFragmentManager)
            }
            4 -> {
//                ActivityUtils.getMineNewsList(2)
            }
            else -> {
                if (index == 3) {
                    saveInteger(
                        "visitorCount_${getLong("userId")}", MineApp.contactNum.visitorCount
                    )
                    binding.hasNewVisitor = false
                }
                activity.startActivity<FCLActivity>(
                    Pair("index", index)
                )
            }
        }
    }

    /**
     * 分享
     */
    fun openShare(index: Int) {
        if (index == 1 || !MineApp.isFirstOpen) {
//            ActivityUtils.getMineWeb(
//                "邀请好友赚钱", HttpManager.BASE_URL.toString() + "mobile/Share_marketingInfo" +
//                        "?userId=" + BaseActivity.userId + "&sessionId=" + BaseActivity.sessionId +
//                        "&pfDevice=Android&pfAppType=203&pfAppVersion=" + MineApp.versionName
//            )
        } else
            VipAdDF(activity).setType(100).setMainDataSource(mainDataSource)
                .show(activity.supportFragmentManager)
    }

    /**
     * 爱情盲盒
     */
    fun toLove(view: View) {}

    /**
     * 照相馆
     */
    fun toPhotoStudio(view: View) {}

    /**
     * 图片/视频动态
     */
    fun selectIndex(index: Int) {
        binding.index = index
        if (index == 0)
            activity.replaceFragment(MemberDiscoverFrag(1L), R.id.dyn_content)
        else
            activity.replaceFragment(MemberVideoFrag(1L), R.id.dyn_content)
    }

    /**
     * 动态数量
     */
    fun dynNotData(isNotData: Boolean) {
        binding.hasDyn = !isNotData
        if (binding.hasDyn)
            pvh!!.start()
    }

    /**
     * VIP开通成功
     */
    fun setBtn() {
        binding.isFirstOpen = MineApp.isFirstOpen
        if (!MineApp.isFirstOpen) {
            binding.vipTitle.visibility = View.VISIBLE
            binding.vipInfo.visibility = View.VISIBLE
        }
        mHandler.removeCallbacks(ra)
        binding.vipLinear.setBackgroundResource(R.mipmap.mine_share_item_bg)
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

    /**
     * 显示爱情盲盒
     */
    private fun getAi() {
        mainDataSource.enqueue({ getAi() }) {
            onSuccess {
                binding.showAi = it == 1
            }
        }
    }
}