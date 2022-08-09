package com.yimi.rentme.vm.fragment

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.MemberDetailActivity
import com.yimi.rentme.activity.SelectLocationActivity
import com.yimi.rentme.adapter.CardAdapter
import com.yimi.rentme.bean.PairInfo
import com.yimi.rentme.databinding.FragMainCardBinding
import com.yimi.rentme.dialog.VipAdDF
import com.yimi.rentme.views.LeanTextView
import com.yimi.rentme.views.card.SwipeCardsView
import com.yimi.rentme.vm.BaseViewModel
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.RemindDF
import com.zb.baselibs.utils.*
import com.zb.baselibs.utils.permission.requestPermissionsForResult
import kotlinx.coroutines.Job
import org.jetbrains.anko.startActivity

class MainCardViewModel : BaseViewModel() {

    lateinit var binding: FragMainCardBinding
    private val pairInfoList = ArrayList<PairInfo>()
    private lateinit var adapter: CardAdapter

    @SuppressLint("StaticFieldLeak")
    private lateinit var mCardImageView: View
    private var curIndex = -1
    private val userIdList = ArrayList<Long>()
    private lateinit var aMapLocation: AMapLocation
    private var animator: ObjectAnimator? = null
    private val mHandler = Handler()
    private val ra: Runnable = object : Runnable {
        override fun run() {
            startAnim()
            mHandler.postDelayed(this, 5000)
        }
    }

    private var middleX = 0

    override fun initViewModel() {
        adapter = CardAdapter(activity, this)
        binding.isProgressPlay = true
        binding.cityName = ""
        aMapLocation = AMapLocation(activity)
        middleX = BaseApp.W / 2
        binding.swipeCardsView.setCardsSlideListener(object : SwipeCardsView.CardsSlideListener {
            var ivDislike: ImageView? = null
            var ivLike: ImageView? = null
            lateinit var cardImageView: View
            override fun onShow(index: Int) {
                curIndex = index
                cardImageView = binding.swipeCardsView.topView!!
                for (view in binding.swipeCardsView.viewList) {
                    view.findViewById<LeanTextView>(R.id.tv_like_count).text =
                        MineApp.likeCount.toString()
                }
                ivDislike = null
                ivLike = null
                if (adapter.count - curIndex == 3 || adapter.count - curIndex == 0) {
                    prePairList()
                }
            }

            override fun onCardVanish(index: Int, type: SwipeCardsView.SlideType) {
                binding.swipeCardsView.setSrollDuration(400)
                if (type == SwipeCardsView.SlideType.LEFT) {
                    SCToastUtil.showToast(activity, "不喜欢", 2)
                } else {
                    SCToastUtil.showToast(activity, "喜欢", 2)
                    MineApp.likeCount--
                }

            }

            override fun onItemClick(cardImageView: View, index: Int) {
                mCardImageView = cardImageView
                activity.startActivity<MemberDetailActivity>(
                    Pair("otherUserId", pairInfoList[index].otherUserId),
                    Pair("showLike", true)
                )
            }
        })
        BaseApp.fixedThreadPool.execute {
            binding.cityName =
                BaseApp.cityDaoManager.getCityName(MineApp.provinceId, MineApp.cityId)
            SystemClock.sleep(1000L)
            activity.runOnUiThread {
                if (MineApp.hasLocation)
                    prePairList()
                else
                    setLocation(1)
            }
        }
    }

    fun onResume() {
        mHandler.removeCallbacks(ra)
        playExposure(if (MineApp.mineInfo.memberType == 1) binding.tvCity else binding.ivExposure)
        mHandler.post(ra)
    }

    override fun onDestroy() {
        super.onDestroy()
        aMapLocation.destroy()
        mHandler.removeCallbacks(ra)
        stopAnim()
        animator = null
    }

    /**
     * 更新卡片动画
     */
    fun moveCard(direction: Int) {
        BaseApp.fixedThreadPool.execute {
            SystemClock.sleep(500L)
            binding.swipeCardsView.setSrollDuration(1000)
            activity.runOnUiThread {
                when (direction) {
                    0 -> {
                        binding.swipeCardsView.slideCardOut(SwipeCardsView.SlideType.LEFT)
                    }
                    1 -> {
                        binding.swipeCardsView.slideCardOut(SwipeCardsView.SlideType.RIGHT)
                    }
                    2 -> {
                        binding.swipeCardsView.slideCardOut(SwipeCardsView.SlideType.RIGHT)
                    }
                }
            }
        }
    }

    /**
     * 更新城市
     */
    fun updateCity(type: Int) {
        BaseApp.fixedThreadPool.execute {
            binding.cityName =
                BaseApp.cityDaoManager.getCityName(MineApp.provinceId, MineApp.cityId)
            if (type == 1)
                activity.runOnUiThread {
                    binding.isProgressPlay = true
                    userIdList.clear()
                    pairInfoList.clear()
                    adapter.setDataList(pairInfoList)
                    curIndex = -1
                    adapter = CardAdapter(activity, this)
                    prePairList()
                }
        }
    }

    /**
     * 选择城市
     */
    fun selectCity(view: View) {
        if (MineApp.mineInfo.memberType == 1) {
            VipAdDF(activity).setType(5).setMainDataSource(mainDataSource)
                .show(activity.supportFragmentManager)
            return
        }
        if (MineApp.hasLocation)
            activity.startActivity<SelectLocationActivity>()
        else
            setLocation(2)
    }

    /**
     * 超级曝光
     */
    fun exposure(view: View) {
        if (MineApp.mineInfo.memberType == 1)
            VipAdDF(activity).setType(1).setMainDataSource(mainDataSource)
                .show(activity.supportFragmentManager)
        else
            RemindDF(activity).setTitle("VIP专享").setContent("虾菇每日自动为你增加曝光度，让10的人优先看到你")
                .isSingle(true).setSureName("明白了").show(activity.supportFragmentManager)
    }

    /**
     * 刷新
     */
    fun onRefresh(view: View) {
        prePairList()
    }

    /**
     * 预匹配
     */
    private fun prePairList() {
        val map = HashMap<String, String>()
        if (MineApp.sex != -1) {
            map["sex"] = MineApp.sex.toString()
        }
        if (MineApp.minAge != 18 || MineApp.maxAge != 70) {
            map["maxAge"] = MineApp.maxAge.toString()
            map["minAge"] = MineApp.minAge.toString()
        }
        mainDataSource.enqueue({ prePairList(map) }) {
            onSuccess {
                for (item in it) {
                    if (!userIdList.contains(item.otherUserId)) {
                        userIdList.add(item.otherUserId)
                        if (item.moreImages.isNotEmpty()) {
                            val temp = item.moreImages.split("#")
                            for (image in temp)
                                item.imageList.add(image)
                        }
                        if (item.imageList.size == 0)
                            item.imageList.add(item.singleImage)
                        pairInfoList.add(item)
                    }
                }
                adapter.setDataList(pairInfoList)
                if (curIndex == -1) {
                    curIndex = 0
                    binding.swipeCardsView.setAdapter(adapter)
                } else
                    binding.swipeCardsView.notifyDatasetChanged(curIndex)
                binding.isProgressPlay = false
            }
            onFailed {
                if (it.isNoData)
                    binding.isProgressPlay = true
                binding.isNoWifi = it.isNoWIFI
            }
        }
    }

    /**
     * 定位
     */
    private fun setLocation(type: Int) {
        if (checkPermissionGranted(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            toLocation(type)
        } else {
            if (getInteger("location_permission", 0) == 0) {
                saveInteger("location_permission", 1)
                RemindDF(activity).setTitle("权限说明")
                    .setContent(
                        "当您浏览查看广场动态、广场视频、滑动卡片展示时，我们将会申请定位权限：" +
                                "\n 1、申请定位权限--设备所在位置相关信息（包括您授权的GPS位置以及WLAN接入点、蓝牙和基站等传感器信息），" +
                                "\n 2、若您点击“同意”按钮，我们方可正式申请上述权限，以便通过高德地图API获取经纬度及城市信息，也可通过左上角地址按钮进入地图，自主选择定位，" +
                                "\n 3、若您点击“拒绝”按钮，我们将不再主动弹出该提示，我们会获取全国范围内的动态、视频、卡片信息，不影响使用其他的虾菇功能/服务，" +
                                "\n 4、您也可以通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭定位权限。"
                    ).setSureName("同意").setCancelName("拒绝")
                    .setCallBack(object : RemindDF.CallBack {
                        override fun sure() {
                            toLocation(type)
                        }
                    }).show(activity.supportFragmentManager)
            } else {
                if (type == 1)
                    prePairList()
                else
                    Toast.makeText(
                        activity,
                        "可通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭定位权限。",
                        Toast.LENGTH_SHORT
                    ).show()

            }
        }
    }

    private fun toLocation(type: Int) {
        launchMain {
            activity.requestPermissionsForResult(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION, rationale = "为了更好的提供服务，需要获取定位权限"
            )
            activity.runOnUiThread {
                showLoading(Job(), "定位中...")
                aMapLocation.start(object : AMapLocation.CallBack {
                    override fun success() {
                        dismissLoading()
                        if (type == 1) {
                            BaseApp.fixedThreadPool.execute {
                                MineApp.provinceId =
                                    BaseApp.provinceDaoManager.getProvinceId(getString("provinceName"))
                                MineApp.cityId = BaseApp.cityDaoManager.getCityId(
                                    MineApp.provinceId, getString("cityName")
                                )
                                MineApp.districtId = BaseApp.districtDaoManager.getDistrictId(
                                    MineApp.cityId, getString("districtName")
                                )
                                MineApp.hasLocation = true
                                activity.runOnUiThread {
                                    modifyMemberInfoForNoVerify()
                                    joinPairPool()
                                }
                            }
                        } else
                            activity.startActivity<SelectLocationActivity>()
                    }
                })
            }
        }
    }

    /**
     * 更新地区信息
     */
    private fun modifyMemberInfoForNoVerify() {
        val map = HashMap<String, String>()
        map["provinceId"] = MineApp.provinceId.toString()
        map["cityId"] = MineApp.cityId.toString()
        map["districtId"] = MineApp.districtId.toString()
        mainDataSource.enqueue({ modifyMemberInfoForNoVerify(map) })
    }

    /**
     * 加入匹配池
     */
    private fun joinPairPool() {
        val map = HashMap<String, String>()
        map["longitude"] = getString("longitude")
        map["latitude"] = getString("latitude")
        map["provinceId"] = MineApp.provinceId.toString()
        map["cityId"] = MineApp.cityId.toString()
        map["districtId"] = MineApp.districtId.toString()
        mainDataSource.enqueue({ joinPairPool(map) }) {
            onSuccess {
                prePairList()
            }
        }
    }

    /**
     * 顶部动画
     */
    private fun playExposure(view: View) {
        animator = ObjectAnimator.ofFloat(view, "rotation", 0f, -5f, 0f, 5f).setDuration(400L)
        animator!!.repeatMode = ValueAnimator.REVERSE
        animator!!.repeatCount = 3
    }

    /**
     * 开始
     */
    private fun startAnim() {
        if (animator != null && !animator!!.isRunning) animator!!.start()
    }

    /**
     * 结束
     */
    private fun stopAnim() {
        if (animator != null && animator!!.isRunning) animator!!.cancel()
    }
}