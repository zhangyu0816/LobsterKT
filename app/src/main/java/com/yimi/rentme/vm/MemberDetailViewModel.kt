package com.yimi.rentme.vm

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.os.Handler
import android.os.SystemClock
import android.view.View
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.*
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.*
import com.yimi.rentme.databinding.AcMemberDetailBinding
import com.yimi.rentme.dialog.*
import com.yimi.rentme.roomdata.FollowInfo
import com.yimi.rentme.roomdata.LikeTypeInfo
import com.yimi.rentme.utils.imagebrowser.MyMNImage
import com.yimi.rentme.views.SuperLikeInterface
import com.zb.baselibs.adapter.loadImage
import com.zb.baselibs.adapter.viewSize
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.bean.Ads
import com.zb.baselibs.utils.DateUtil
import com.zb.baselibs.utils.SCToastUtil
import com.zb.baselibs.utils.getLong
import com.zb.baselibs.views.imagebrowser.base.ImageBrowserConfig
import com.zb.baselibs.views.xbanner.ImageLoader
import com.zb.baselibs.views.xbanner.XBanner
import com.zb.baselibs.views.xbanner.XUtils
import com.zb.baselibs.views.xbanner.XUtils.showBanner
import org.jetbrains.anko.startActivity
import org.simple.eventbus.EventBus

class MemberDetailViewModel : BaseViewModel(), SuperLikeInterface {

    lateinit var binding: AcMemberDetailBinding
    var otherUserId = 0L
    var showLike = false // true 卡片页显示动画
    var isFollow = false
    lateinit var imageAdapter: BaseAdapter<String>
    private var mPosition = 0

    @SuppressLint("StaticFieldLeak")
    private lateinit var xBanner: XBanner
    private val adList = ArrayList<Ads>()
    private val sourceImageList = ArrayList<String>()
    private var bannerWidth = BaseApp.W
    lateinit var rewardAdapter: BaseAdapter<Reward>
    private val rewardList = ArrayList<Reward>()
    private lateinit var temp: CharArray
    private var i = 0
    private var info = ""
    private var rewardInfo = ""
    private val mHandler = Handler()

    lateinit var discoverAdapter: BaseAdapter<DiscoverInfo>
    private val discoverInfoList = ArrayList<DiscoverInfo>()
    lateinit var tagAdapter: BaseAdapter<String>
    private val tagList = ArrayList<String>()


    private var likeTypeInfo: LikeTypeInfo? = null
    private var pvh: ObjectAnimator? = null
    private var translateY: ObjectAnimator? = null

    override fun initViewModel() {
        adList.clear()
        binding.title = "个人资料"
        binding.memberInfo = MemberInfo()
        binding.likeType = 0
        binding.isFollow = false
        binding.isMine = false
        binding.distant = ""
        binding.job = "保密"
        binding.height = "保密"
        binding.cityName = "-"
        binding.district = "-"
        binding.rewardNum = 0
        binding.rewardInfo = ""
        binding.constellation = "-"
        binding.isPlay = false
        binding.contactNum = ContactNum()
        imageAdapter = BaseAdapter(activity, R.layout.item_more_image, sourceImageList, this)
        imageAdapter.setSelectIndex(mPosition)

        rewardAdapter = BaseAdapter(activity, R.layout.item_reward, rewardList, this)

        discoverAdapter = BaseAdapter(activity, R.layout.item_discover_list, discoverInfoList, this)

        tagAdapter = BaseAdapter(activity, R.layout.item_member_service_tag, tagList, this)

        xBanner = XBanner(activity)
        otherInfo()
    }

    override fun back(view: View) {
        super.back(view)
        activity.finish()
    }

    override fun right(view: View) {
        super.right(view)
        if (binding.memberInfo!!.userId == 0L) return
        mainDataSource.enqueue({ memberInfoConf() }) {
            onSuccess {
                val sharedUrl =
                    "${BaseApp.baseUrl}render/${otherUserId}.html?sharetextId=${it.sharetextId}"
                var sharedName = it.text.replace("{userId}", otherUserId.toString())

                sharedName = sharedName.replace("{nick}", binding.memberInfo!!.nick)
                var content: String
                if (binding.memberInfo!!.serviceTags.isEmpty()) {
                    content = it.text
                } else {
                    content = binding.memberInfo!!.serviceTags.substring(
                        1, binding.memberInfo!!.serviceTags.length - 1
                    )
                    content = "兴趣：" + content.replace("#", ",")
                }
                FunctionDF(activity).setUmImage(
                    binding.memberInfo!!.image.replace("YM0000", "430X430")
                ).setSharedName(sharedName).setContent(content).setSharedUrl(sharedUrl)
                    .setOtherUserId(otherUserId).setIsVideo(false)
                    .setIsDiscover(false).setIsList(false)
                    .setCallBack(object : FunctionDF.CallBack {
                        override fun report() {
                            activity.startActivity<ReportActivity>(
                                Pair("otherUserId", otherUserId)
                            )
                        }

                        override fun like() {
                            if (MineApp.mineInfo.memberType == 2) { // 会员
                                makeEvaluate(2)
                            } else {
                                VipAdDF(activity).setType(3)
                                    .setOtherImage(binding.memberInfo!!.image)
                                    .setMainDataSource(mainDataSource)
                                    .show(activity.supportFragmentManager)
                            }
                        }
                    })
                    .show(activity.supportFragmentManager)
            }
        }
    }

    fun onResume() {
        if (binding.memberInfo!!.userId != 0L) {
            BaseApp.fixedThreadPool.execute {
                likeTypeInfo = MineApp.likeTypeDaoManager.getLikeTypeInfo(otherUserId) // 喜欢 or 超级喜欢
                if (likeTypeInfo != null)
                    binding.likeType = likeTypeInfo!!.likeType
            }
            attentionStatus()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        xBanner.releaseBanner()
    }

    override fun superLike(view: View?, pairInfo: PairInfo?) {
        if (MineApp.mineInfo.memberType == 2) {
            makeEvaluate(2)
        } else {
            if (binding.memberInfo != null)
                VipAdDF(activity).setType(3).setOtherImage(binding.memberInfo!!.image)
                    .setMainDataSource(mainDataSource)
                    .show(activity.supportFragmentManager)
        }
    }

    /**
     * 选中图片
     */
    fun selectImage(position: Int) {
        imageAdapter.setSelectIndex(position)
        imageAdapter.notifyItemChanged(mPosition)
        imageAdapter.notifyItemChanged(position)
        mPosition = position
        xBanner.setCurrentItem(position + 1)
    }

    /**
     * 开通VIP
     */
    fun openVip(view: View) {
        activity.startActivity<OpenVipActivity>()
    }

    /**
     * 关注
     */
    fun follow(view: View) {
        if (binding.isFollow)
            cancelAttention()
        else
            attentionOther()
    }

    /**
     * 选择礼物
     */
    fun selectGift(view: View) {
        if (otherUserId == getLong("userId"))
            activity.startActivity<RewardListActivity>(
                Pair("otherUserId", otherUserId)
            )
        else
            GiftDF(activity).setMainDataSource(mainDataSource)
                .setCallBack(object : GiftDF.CallBack {
                    override fun sure(giftInfo: GiftInfo) {
                        GiftPayDF(activity).setGiftInfo(giftInfo).setOtherUserId(otherUserId)
                            .setMainDataSource(mainDataSource)
                            .setCallBack(object : GiftPayDF.CallBack {
                                override fun sure(giftNum: Int) {
                                    GiveSuccessDF(activity).setGiftInfo(giftInfo)
                                        .setGiftNum(giftNum).show(activity.supportFragmentManager)
                                    seeUserGiftRewards(1)
                                }
                            }).show(activity.supportFragmentManager)
                    }
                }).show(activity.supportFragmentManager)
    }

    /**
     * 打赏列表
     */
    fun toRewardList(view: View) {
        activity.startActivity<RewardListActivity>(
            Pair("otherUserId", otherUserId)
        )
    }

    /**
     * 关注/粉丝
     */
    fun contactNumDetail(index: Int) {
        activity.startActivity<FCLActivity>(
            Pair("index", index),
            Pair("otherUserId", otherUserId)
        )
    }

    /**
     * 动态列表
     */
    fun toDiscoverList(view: View) {
        activity.startActivity<DiscoverListActivity>(
            Pair("otherUserId", otherUserId),
            Pair("memberInfo", binding.memberInfo!!),
            Pair("contactNum", binding.contactNum!!)
        )
    }

    /**
     * 动态详情
     */
    fun toDiscoverDetail(discoverInfo: DiscoverInfo) {
        if (discoverInfo.videoUrl.isEmpty())
            activity.startActivity<DiscoverDetailActivity>(
                Pair("friendDynId", discoverInfo.friendDynId)
            )
        else
            activity.startActivity<VideoDetailActivity>(
                Pair("friendDynId", discoverInfo.friendDynId)
            )
    }

    /**
     * 不喜欢
     */
    fun dislike(view: View) {
        if (binding.memberInfo == null) {
            SCToastUtil.showToast(activity, "网络异常，请检查网络是否链接", 2)
            return
        }
        if (showLike)
            EventBus.getDefault().post(0, "lobsterCar") // 更新卡片页面 -- 喜欢
        activity.finish()
    }

    /**
     * 喜欢
     */
    fun like(view: View) {
        if (binding.memberInfo == null) {
            SCToastUtil.showToast(activity, "网络异常，请检查网络是否链接", 2)
            return
        }
        if (showLike) {
            EventBus.getDefault().post(1, "lobsterCar") // 更新卡片页面 -- 喜欢
            activity.finish()
        } else {
            isLike(binding.ivLike)
            if (MineApp.likeCount == 0 && MineApp.mineInfo.memberType == 1) {
                VipAdDF(activity).setType(6).setMainDataSource(mainDataSource)
                    .show(activity.supportFragmentManager)
                SCToastUtil.showToast(activity, "今日喜欢次数已用完", 2)
                return
            }
            makeEvaluate(1)
        }
    }

    /**
     * 用户信息
     */
    private fun otherInfo() {
        mainDataSource.enqueueLoading({ otherInfo(otherUserId) }, "获取用户信息...") {
            onSuccess {
                binding.memberInfo = it
                binding.isPlay = true
                var distant = "-"
                if (binding.memberInfo!!.distance.isNotEmpty()) {
                    distant = String.format("%.1f", binding.memberInfo!!.distance.toFloat() / 1000f)
                }
                binding.distant = distant
                binding.job = binding.memberInfo!!.job.ifEmpty { "-" }
                if (binding.memberInfo!!.height > 0) {
                    binding.height = binding.memberInfo!!.height.toString()
                }
                binding.constellation = DateUtil.getConstellations(it.birthday)

                if (it.moreImages.isEmpty()) {
                    adList.add(Ads(it.image))
                    sourceImageList.add(it.image)
                } else
                    for (image in it.moreImages.split(",")) {
                        adList.add(Ads(image))
                        sourceImageList.add(image)
                    }
                imageAdapter.notifyItemChanged(0, sourceImageList.size)
                BaseApp.fixedThreadPool.execute {
                    binding.isFollow =
                        MineApp.followDaoManager.getFollowInfo(it.userId) != null // 关注
                    likeTypeInfo =
                        MineApp.likeTypeDaoManager.getLikeTypeInfo(it.userId) // 喜欢 or 超级喜欢
                    if (likeTypeInfo != null)
                        binding.likeType = likeTypeInfo!!.likeType

                    if (isFollow && !binding.isFollow) {
                        val followInfo = FollowInfo()
                        followInfo.image = it.image
                        followInfo.nick = it.nick
                        followInfo.otherUserId = it.userId
                        followInfo.mainUserId = getLong("userId")
                        MineApp.followDaoManager.insert(followInfo)
                        activity.runOnUiThread {
                            EventBus.getDefault()
                                .post(it.userId.toString(), "lobsterUpdateFollowFrag")
                        }
                    }

                    val cityName = BaseApp.cityDaoManager.getCityName(it.provinceId, it.cityId)
                    val district =
                        BaseApp.districtDaoManager.getDistrictName(it.cityId, it.districtId)
                    if (cityName != null) {
                        binding.cityName = cityName.replace("市", "")
                        binding.district = cityName.replace("市", "")
                    }

                    if (district != null)
                        binding.district = if (district.isEmpty())
                            cityName!!.replace("市", "")
                        else
                            "${cityName!!.replace("市", "")} $district"
                }
                if (it.serviceTags.isNotEmpty()) {
                    val tags = it.serviceTags.substring(1, it.serviceTags.length - 1)
                    tagList.addAll(listOf(*tags.split("#".toRegex()).toTypedArray()))
                    tagAdapter.notifyItemRangeChanged(0, tagList.size)
                }

                setBanner()
                attentionStatus()
                seeUserGiftRewards(1)
                otherUserInfoVisit()
                contactNum()
                personOtherDyn()
            }
        }
    }

    /**
     * 动态图片
     */
    private fun setBanner() {
        binding.bannerLinear.removeAllViews()
        binding.bannerLinear.addView(xBanner)
        bannerWidth = binding.bannerLinear.width
        val height = (bannerWidth * 1.2f).toInt()
        binding.bannerLinear.viewSize(bannerWidth, height)
        xBanner.viewSize(bannerWidth, height)
        xBanner.showBanner(
            adList, 5, ImageLoader { context, ads, image, position ->
                run {
                    loadImage(
                        image!!, ads!!.smallImage, 0, R.mipmap.empty_icon, bannerWidth,
                        height, false, 10f, null, false, 0, false, 0f
                    )
                }
            },
            object : XUtils.ClickCallBack {
                override fun clickItem(position: Int, imageList: ArrayList<String>?) {
                    MyMNImage.setIndex(position).setSourceImageList(sourceImageList)
                        .setTransformType(ImageBrowserConfig.TransformType.TransformDepthPage)
                        .setCallBack(object : ImageBrowserConfig.StartBack {
                            override fun onStartActivity() {
                                activity.startActivity<MNImageBrowserActivity>()
                            }
                        })
                        .imageBrowser()
                }
            }, object : XBanner.MoveCallBack {
                override fun moveItem(position: Int) {
                    imageAdapter.setSelectIndex(position)
                    imageAdapter.notifyItemChanged(mPosition)
                    imageAdapter.notifyItemChanged(position)
                    mPosition = position

                }
            }
        )
    }

    /**
     * 访问他人
     */
    private fun otherUserInfoVisit() {
        mainDataSource.enqueue({ otherUserInfoVisit(otherUserId) })
    }

    /**
     * 礼物打赏列表
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun seeUserGiftRewards(pageNo: Int) {
        if (pageNo == 1) {
            binding.rewardNum = 0
            binding.rewardInfo = ""
            rewardList.clear()
            rewardAdapter.notifyDataSetChanged()
        }
        mainDataSource.enqueue({ seeUserGiftRewards(otherUserId, 2, pageNo, 10) }) {
            onSuccess {
                binding.rewardNum = binding.rewardNum!! + it.size
                if (rewardList.size == 0)
                    for (i in 0 until 3.coerceAtMost(it.size)) {
                        rewardList.add(it[i])
                    }
                seeUserGiftRewards(pageNo + 1)
            }
            onFailed {
                if (it.isNoData) {
                    if (binding.rewardNum == 0) {
                        rewardInfo = "送朵玫瑰花，开始我们的邂逅"
                    } else {
                        rewardInfo = if (binding.rewardNum == 1) {
                            "成为CP候选人"
                        } else {
                            "快来打榜"
                        }
                        rewardAdapter.notifyDataSetChanged()
                    }
                    temp = rewardInfo.toCharArray()
                    info = ""
                    i = 0
                    binding.rewardInfo = info
                    mHandler.postDelayed(object : Runnable {
                        override fun run() {
                            if (i < temp.size) {
                                info += temp[i]
                                binding.rewardInfo = info
                                i++
                                mHandler.postDelayed(this, 50)
                            } else {
                                mHandler.removeCallbacks(this)
                            }
                        }
                    }, 50)
                }
            }
        }
    }

    /**
     * 喜欢/超级喜欢
     */
    private fun makeEvaluate(likeOtherStatus: Int) {
        mainDataSource.enqueue({ makeEvaluate(otherUserId, likeOtherStatus) }) {
            onSuccess {
                val myHead = MineApp.mineInfo.image
                val otherHead = binding.memberInfo!!.image
                // 1喜欢成功 2匹配成功 3喜欢次数用尽
                if (it == 1) {
                    // 不喜欢成功  喜欢成功  超级喜欢成功
                    when (likeOtherStatus) {
                        0 -> activity.finish()
                        1 -> {
                            EventBus.getDefault().post("更新喜欢次数", "lobsterUpdateLikeCount")
                            EventBus.getDefault().post("更新关注/粉丝/喜欢", "lobsterUpdateFCL")
                            closeBtn(binding.likeLayout, 1)
                            SCToastUtil.showToast(activity, "已喜欢成功", 2)
                        }
                        2 -> {
                            if (showLike) {
                                EventBus.getDefault().post(2, "lobsterCar") // 更新卡片页面 -- 喜欢
                                activity.finish()
                            } else {
                                EventBus.getDefault().post("更新关注/粉丝/喜欢", "lobsterUpdateFCL")
                                closeBtn(binding.likeLayout, 2)
                                SuperLikeDF(activity).setMyHead(myHead).setOtherHead(otherHead)
                                    .setMySex(MineApp.mineInfo.sex)
                                    .setOtherSex(binding.memberInfo!!.sex)
                                    .show(activity.supportFragmentManager)
                            }
                        }
                    }
                } else if (it == 2) {
                    // 匹配成功
                    SuperLikeDF(activity).setMyHead(myHead).setOtherHead(otherHead)
                        .setMySex(MineApp.mineInfo.sex)
                        .setOtherSex(binding.memberInfo!!.sex)
                        .setOtherNick(binding.memberInfo!!.nick)
                        .setCallBack(object : SuperLikeDF.CallBack {
                            override fun sure() {
//                                ActivityUtils.getChatActivity(discoverInfo.getUserId(), false)
                            }
                        })
                        .show(activity.supportFragmentManager)
                    EventBus.getDefault().post("更新喜欢次数", "lobsterUpdateLikeCount")
                    EventBus.getDefault().post("更新关注/粉丝/喜欢", "lobsterUpdateFCL")
                    EventBus.getDefault().post("更新匹配列表", "lobsterUpdatePairList")
                    closeBtn(binding.likeLayout, 2)
                } else if (it == 3) {
                    // 喜欢次数用尽
                    VipAdDF(activity).setType(6).setMainDataSource(mainDataSource)
                        .show(activity.supportFragmentManager)
                    SCToastUtil.showToast(activity, "今日喜欢次数已用完", 2)
                } else if (it == 4) {
                    // 超级喜欢时，非会员或超级喜欢次数用尽
                    if (MineApp.mineInfo.memberType == 2) {
                        SCToastUtil.showToast(activity, "今日超级喜欢次数已用完", 2)
                    } else {
                        VipAdDF(activity).setType(3).setOtherImage(otherHead)
                            .setMainDataSource(mainDataSource)
                            .show(activity.supportFragmentManager)
                    }
                } else {
                    when (likeOtherStatus) {
                        0 -> activity.finish()
                        1 -> {
                            closeBtn(binding.likeLayout, 1)
                            SCToastUtil.showToast(activity, "已喜欢成功", 2)
                        }
                        2 -> {
                            closeBtn(binding.likeLayout, 2)
                            SCToastUtil.showToast(activity, "你已超级喜欢过对方", 2)
                        }
                    }
                }
            }
        }
    }

    /**
     * 三合一接口 （返回关注数量、粉丝数量、喜欢数量、被喜欢数量）
     */
    private fun contactNum() {
        mainDataSource.enqueue({ contactNum(otherUserId) }) {
            onSuccess {
                binding.contactNum = it
            }
        }
    }

    /**
     * 喜欢动画
     */
    private fun isLike(view: View) {
        val pvhSY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.1f, 1f, 1.2f, 1f)
        val pvhSX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.1f, 1f, 1.2f, 1f)
        pvh = ObjectAnimator.ofPropertyValuesHolder(view, pvhSY, pvhSX).setDuration(500)
        pvh!!.start()
        BaseApp.fixedThreadPool.execute {
            SystemClock.sleep(500)
            activity.runOnUiThread {
                if (pvh != null) pvh!!.cancel()
                pvh = null
            }
        }
    }

    /**
     * 关闭喜欢按钮
     */
    private fun closeBtn(view: View, likeType: Int) {
        if (likeTypeInfo == null) {
            likeTypeInfo = LikeTypeInfo()
            likeTypeInfo!!.likeType = likeType
            likeTypeInfo!!.otherUserId = otherUserId
            likeTypeInfo!!.mainUserId = getLong("userId")
            BaseApp.fixedThreadPool.execute {
                MineApp.likeTypeDaoManager.insert(likeTypeInfo!!)
            }
        } else {
            BaseApp.fixedThreadPool.execute {
                MineApp.likeTypeDaoManager.updateLikeType(likeType, otherUserId)
            }
        }
        translateY = ObjectAnimator.ofFloat(view, "translationY", 0f, 1000f).setDuration(500)
        translateY!!.start()
        BaseApp.fixedThreadPool.execute {
            SystemClock.sleep(500)
            activity.runOnUiThread {
                translateY!!.cancel()
                translateY = null
                binding.likeType = likeType
                binding.isPlay = false
            }
        }
    }

    /**
     * 个人动态
     */
    private fun personOtherDyn() {
        val map = HashMap<String, String>()
        map["otherUserId"] = otherUserId.toString()
        map["pageNo"] = "1"
        map["timeSortType"] = "1"
        map["dycRootType"] = "0"
        mainDataSource.enqueue({ personOtherDyn(map) }) {
            onSuccess {
                discoverInfoList.addAll(it)
                discoverAdapter.notifyItemRangeChanged(0, discoverInfoList.size)
            }
        }
    }

    /**
     * 关注状态
     */
    private fun attentionStatus() {
        mainDataSource.enqueue({ attentionStatus(otherUserId) }) {
            onSuccess {
                binding.isFollow = true
                BaseApp.fixedThreadPool.execute {
                    val followInfo = FollowInfo()
                    followInfo.image = binding.memberInfo!!.image
                    followInfo.nick = binding.memberInfo!!.nick
                    followInfo.otherUserId = otherUserId
                    followInfo.mainUserId = getLong("userId")
                    MineApp.followDaoManager.insert(followInfo)
                }
            }
            onFailed {
                binding.isFollow = false
                BaseApp.fixedThreadPool.execute {
                    MineApp.followDaoManager.deleteFollowInfo(otherUserId)
                }
            }
        }
    }

    /**
     * 关注
     */
    private fun attentionOther() {
        mainDataSource.enqueue({ attentionOther(otherUserId) }) {
            onSuccess {
                BaseApp.fixedThreadPool.execute {
                    val followInfo = FollowInfo()
                    followInfo.image = binding.memberInfo!!.image
                    followInfo.nick = binding.memberInfo!!.nick
                    followInfo.otherUserId = otherUserId
                    followInfo.mainUserId = getLong("userId")
                    MineApp.followDaoManager.insert(followInfo)
                    activity.runOnUiThread {
                        binding.isFollow = true
                    }
                }
                EventBus.getDefault().post(true, "lobsterUpdateFollow")
            }
            onFailToast { false }
            onFailed {
                if (it.errorMessage == "已经关注过") {
                    BaseApp.fixedThreadPool.execute {
                        val followInfo = FollowInfo()
                        followInfo.image = binding.memberInfo!!.image
                        followInfo.nick = binding.memberInfo!!.nick
                        followInfo.otherUserId = otherUserId
                        followInfo.mainUserId = getLong("userId")
                        MineApp.followDaoManager.insert(followInfo)

                        activity.runOnUiThread {
                            binding.isFollow = true
                        }
                    }
                    EventBus.getDefault().post(true, "lobsterUpdateFollow")
                }
            }
        }
    }

    /**
     * 取消关注
     */
    private fun cancelAttention() {
        mainDataSource.enqueue({ cancelAttention(otherUserId) }) {
            onSuccess {
                BaseApp.fixedThreadPool.execute {
                    MineApp.followDaoManager.deleteFollowInfo(otherUserId)
                    activity.runOnUiThread {
                        binding.isFollow = false
                    }
                }
                EventBus.getDefault().post(false, "lobsterUpdateFollow")
            }
            onFailToast { false }
            onFailed {
                if (it.errorMessage == "已经取消了") {
                    BaseApp.fixedThreadPool.execute {
                        MineApp.followDaoManager.deleteFollowInfo(otherUserId)
                        activity.runOnUiThread {
                            binding.isFollow = false
                        }
                    }
                    EventBus.getDefault().post(false, "lobsterUpdateFollow")
                }
            }
        }
    }
}