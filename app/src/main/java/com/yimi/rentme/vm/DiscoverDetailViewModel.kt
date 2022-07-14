package com.yimi.rentme.vm

import android.annotation.SuppressLint
import android.os.Handler
import android.os.SystemClock
import android.view.View
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.MNImageBrowserActivity
import com.yimi.rentme.activity.MemberDetailActivity
import com.yimi.rentme.activity.ReportActivity
import com.yimi.rentme.activity.RewardListActivity
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.*
import com.yimi.rentme.databinding.AcDiscoverDetailBinding
import com.yimi.rentme.dialog.*
import com.yimi.rentme.roomdata.FollowInfo
import com.yimi.rentme.roomdata.GoodInfo
import com.yimi.rentme.roomdata.LikeTypeInfo
import com.yimi.rentme.utils.imagebrowser.MyMNImage
import com.yimi.rentme.utils.imagebrowser.OnDiscoverClickListener
import com.yimi.rentme.views.GoodView
import com.zb.baselibs.adapter.loadImage
import com.zb.baselibs.adapter.viewSize
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.bean.Ads
import com.zb.baselibs.utils.SCToastUtil
import com.zb.baselibs.utils.getLong
import com.zb.baselibs.views.imagebrowser.base.ImageBrowserConfig
import com.zb.baselibs.views.xbanner.ImageLoader
import com.zb.baselibs.views.xbanner.XUtils
import com.zb.baselibs.views.xbanner.XUtils.showBanner
import org.jetbrains.anko.startActivity
import org.simple.eventbus.EventBus

class DiscoverDetailViewModel : BaseViewModel(), OnRefreshListener, OnLoadMoreListener {

    lateinit var binding: AcDiscoverDetailBinding
    var friendDynId = 0L
    private var discoverInfo = DiscoverInfo()
    private var likeTypeInfo: LikeTypeInfo? = null
    private val adList = ArrayList<Ads>()
    private val sourceImageList = ArrayList<String>()

    lateinit var rewardAdapter: BaseAdapter<Reward>
    private val rewardList = ArrayList<Reward>()
    private lateinit var temp: CharArray
    private var i = 0
    private var info = ""
    private var rewardInfo = ""
    private val mHandler = Handler()

    lateinit var reviewAdapter: BaseAdapter<Review>
    private val reviewList = ArrayList<Review>()
    private var pageNo = 1
    private var reviewId = 0L

    override fun initViewModel() {
        binding.title = "动态详情"
        binding.discoverInfo = DiscoverInfo()
        binding.memberInfo = MemberInfo()
        binding.likeTypeInfo = LikeTypeInfo()
        binding.isMine = false
        binding.isFollow = false
        binding.rewardNum = 0
        binding.rewardInfo = ""
        binding.content = ""

        // 打赏
        rewardAdapter = BaseAdapter(activity, R.layout.item_reward, rewardList, this)
        // 评论
        reviewAdapter = BaseAdapter(activity, R.layout.item_review, reviewList, this)

        dynDetail()
    }

    override fun back(view: View) {
        super.back(view)
        activity.finish()
    }

    fun onResume() {
        if (discoverInfo.friendDynId != 0L)
            attentionStatus()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRefresh(refreshLayout: RefreshLayout) {
        binding.refresh.setEnableLoadMore(true)
        pageNo = 1
        reviewList.clear()
        reviewAdapter.notifyDataSetChanged()
        seeReviews()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNo++
        seeReviews()
    }

    /**
     * 更多
     */
    override fun right(view: View) {
        super.right(view)
        if (discoverInfo.friendDynId == 0L) return
        mainDataSource.enqueue({ memberInfoConf() }) {
            onSuccess {
                val sharedName = it.text.replace("{userId}", discoverInfo.userId.toString())
                    .replace("{nick}", discoverInfo.nick)
                val content = discoverInfo.text.ifEmpty { discoverInfo.friendTitle }
                val sharedUrl: String =
                    BaseApp.baseUrl + "mobile/Dyn_dynDetail?friendDynId=" + friendDynId
                FunctionDF(activity).setUmImage(
                    discoverInfo.image.replace("YM0000", "430X430")
                ).setSharedName(sharedName).setContent(content).setSharedUrl(sharedUrl)
                    .setOtherUserId(discoverInfo.userId).setIsVideo(false)
                    .setIsDiscover(true).setIsList(false)
                    .setCallBack(object : FunctionDF.CallBack {
                        override fun report() {
                            activity.startActivity<ReportActivity>(
                                Pair("otherUserId", discoverInfo.userId)
                            )
                        }

                        override fun rewardList() {
                            activity.startActivity<RewardListActivity>(
                                Pair("friendDynId", binding.discoverInfo!!.friendDynId)
                            )
                        }

                        override fun delete() {
                        }

                        override fun like() {
                        }
                    })
                    .show(activity.supportFragmentManager)
            }
        }
    }

    /**
     * 跳至用户详情
     */
    fun toMemberDetail(view: View) {
        activity.startActivity<MemberDetailActivity>(
            Pair("otherUserId", discoverInfo.userId)
        )
    }

    /**
     * 关注
     */
    fun follow(view: View) {
        if (discoverInfo.friendDynId == 0L) return
        if (binding.isFollow)
            cancelAttention()
        else
            attentionOther()
    }

    /**
     * 选择礼物
     */
    fun selectGift(view: View?) {
        if (discoverInfo.userId == getLong("userId"))
            activity.startActivity<RewardListActivity>(
                Pair("friendDynId", binding.discoverInfo!!.friendDynId)
            )
        else
            GiftDF(activity).setMainDataSource(mainDataSource)
                .setCallBack(object : GiftDF.CallBack {
                    override fun sure(giftInfo: GiftInfo) {
                        GiftPayDF(activity).setGiftInfo(giftInfo).setFriendDynId(friendDynId)
                            .setMainDataSource(mainDataSource)
                            .setCallBack(object : GiftPayDF.CallBack {
                                override fun sure(giftNum: Int) {
                                    GiveSuccessDF(activity).setGiftInfo(giftInfo)
                                        .setGiftNum(giftNum).show(activity.supportFragmentManager)
                                    seeGiftRewards(1)
                                }
                            }).show(activity.supportFragmentManager)
                    }
                }).show(activity.supportFragmentManager)
    }

    /**
     * 礼物打赏列表
     */
    fun toRewardList(view: View) {
        activity.startActivity<RewardListActivity>(
            Pair("friendDynId", binding.discoverInfo!!.friendDynId)
        )
    }

    /**
     * 用户详情
     */
    fun toReviewMemberDetail(review: Review) {
        if (review.userId != getLong("userId") && review.userId != 0L)
            activity.startActivity<MemberDetailActivity>(
                Pair("otherUserId", review.userId)
            )
    }

    /**
     * 编辑评论
     */
    fun doReview(view: View?) {
        DiscoverReviewDF(activity).setDiscoverInfo(discoverInfo).setContent(binding.content!!)
            .setIsMine(binding.isMine).setIsLike(binding.discoverInfo!!.isLike)
            .setHint(binding.edContent.hint.toString())
            .setCallBack(object : DiscoverReviewDF.CallBack {
                override fun dynDoReview(content: String) {
                    binding.content = content
                    dynDoReview()
                }

                override fun selectGift() {
                    selectGift(null)
                }

                override fun dynLike() {
                    dynLike(null)
                }

                override fun toReviewList() {
                    toReviewList(null)
                }
            }).show(activity.supportFragmentManager)
    }

    /**
     * 选择评论用户
     */
    fun selectReview(review: Review) {
        reviewId = if (reviewId == review.reviewId) 0L else review.reviewId
        binding.edContent.hint = if (reviewId == 0L) "表白一句，成功率超高～" else "评论 ${review.nick}"
        doReview(null)
    }

    /**
     * 点赞
     */
    fun dynLike(view: View?) {
        val goodView = view as GoodView
        BaseApp.fixedThreadPool.execute {
            val goodInfo = MineApp.goodDaoManager.getGood(friendDynId)
            activity.runOnUiThread {
                if (goodInfo == null) {
                    goodView.playLike()
                    dynDoLike()
                } else {
                    goodView.playUnlike()
                    dynCancelLike()
                }
            }
        }
    }

    /**
     * 评论列表
     */
    fun toReviewList(view: View?) {
        binding.appbar.setExpanded(false)
    }

    /**
     * 动态图片
     */
    private fun setBanner() {
        binding.xBanner.viewSize(BaseApp.W, BaseApp.W)
        binding.xBanner.showBanner(
            adList, 0,
            ImageLoader { context, ads, image, position ->
                run {
                    loadImage(
                        image!!, ads!!.smallImage, 0, 0, BaseApp.W,
                        BaseApp.W, false, 0f, false, 0, false, 0f
                    )
                }
            },
            object : XUtils.ClickCallBack {
                override fun clickItem(position: Int, imageList: ArrayList<String>?) {
                    if (discoverInfo.friendDynId == 0L) return
                    MyMNImage.setIndex(0).setSourceImageList(sourceImageList)
                        .setTransformType(ImageBrowserConfig.TransformType.TransformDepthPage)
                        .setDiscoverInfo(discoverInfo)
                        .setDiscoverClickListener(object : OnDiscoverClickListener {
                            override fun follow() {
                                follow(binding.tvFollow)
                            }

                            override fun good() {
                                dynDoLike()
                            }
                        })
                        .setCallBack(object : ImageBrowserConfig.StartBack {
                            override fun onStartActivity() {
                                activity.startActivity<MNImageBrowserActivity>()
                            }
                        })
                        .imageBrowser()
                }
            }, null
        )
    }

    /**
     * 访问动态
     */
    private fun dynVisit() {
        mainDataSource.enqueue({ dynVisit(friendDynId) })
    }

    /**
     * 动态详情
     */
    private fun dynDetail() {
        mainDataSource.enqueueLoading({ dynDetail(friendDynId) }, "获取动态详情...") {
            onSuccess {
                discoverInfo = it
                binding.isMine = discoverInfo.userId == getLong("userId")
                BaseApp.fixedThreadPool.execute {
                    discoverInfo.isLike = MineApp.goodDaoManager.getGood(friendDynId) != null
                    binding.discoverInfo = it
                }
                if (it.images.isEmpty()) {
                    adList.add(Ads(it.image))
                    sourceImageList.add(it.image)
                } else
                    for (image in it.images.split(",")) {
                        adList.add(Ads(image))
                        sourceImageList.add(image)
                    }
                setBanner()
                dynVisit()
                otherInfo()
                seeGiftRewards(1)
                BaseApp.fixedThreadPool.execute {
                    binding.isFollow =
                        MineApp.followDaoManager.getFollowInfo(it.userId) != null // 关注
                    likeTypeInfo =
                        MineApp.likeTypeDaoManager.getLikeTypeInfo(it.userId) // 喜欢 or 超级喜欢
                    if (likeTypeInfo != null)
                        binding.likeTypeInfo = likeTypeInfo
                }
            }
        }
    }

    /**
     * 用户信息
     */
    private fun otherInfo() {
        mainDataSource.enqueue({ otherInfo(discoverInfo.userId) }) {
            onSuccess {
                binding.memberInfo = it
                attentionStatus()
                seeReviews()
            }
        }
    }

    /**
     * 关注状态
     */
    private fun attentionStatus() {
        mainDataSource.enqueue({ attentionStatus(discoverInfo.userId) }) {
            onSuccess {
                binding.isFollow = true
                BaseApp.fixedThreadPool.execute {
                    val followInfo = FollowInfo()
                    followInfo.image = binding.memberInfo!!.image
                    followInfo.images =
                        binding.memberInfo!!.moreImages.ifEmpty { binding.memberInfo!!.singleImage }
                    followInfo.nick = binding.memberInfo!!.nick
                    followInfo.otherUserId = discoverInfo.userId
                    followInfo.mainUserId = getLong("userId")
                    MineApp.followDaoManager.insert(followInfo)
                }
            }
            onFailed {
                binding.isFollow = false
                BaseApp.fixedThreadPool.execute {
                    MineApp.followDaoManager.deleteFollowInfo(discoverInfo.userId)
                }
            }
        }
    }

    /**
     * 关注
     */
    private fun attentionOther() {
        mainDataSource.enqueue({ attentionOther(discoverInfo.userId) }) {
            onSuccess {
                BaseApp.fixedThreadPool.execute {
                    val followInfo = FollowInfo()
                    followInfo.image = binding.memberInfo!!.image
                    followInfo.images =
                        binding.memberInfo!!.moreImages.ifEmpty { binding.memberInfo!!.singleImage }
                    followInfo.nick = binding.memberInfo!!.nick
                    followInfo.otherUserId = discoverInfo.userId
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
                        followInfo.images =
                            binding.memberInfo!!.moreImages.ifEmpty { binding.memberInfo!!.singleImage }
                        followInfo.nick = binding.memberInfo!!.nick
                        followInfo.otherUserId = discoverInfo.userId
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
        mainDataSource.enqueue({ cancelAttention(discoverInfo.userId) }) {
            onSuccess {
                BaseApp.fixedThreadPool.execute {
                    MineApp.followDaoManager.deleteFollowInfo(discoverInfo.userId)
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
                        MineApp.followDaoManager.deleteFollowInfo(discoverInfo.userId)
                        activity.runOnUiThread {
                            binding.isFollow = false
                        }
                    }
                    EventBus.getDefault().post(false, "lobsterUpdateFollow")
                }
            }
        }
    }

    /**
     * 点赞
     */
    private fun dynDoLike() {
        mainDataSource.enqueue({ dynDoLike(friendDynId) }) {
            onSuccess {
                val goodInfo = GoodInfo()
                goodInfo.friendDynId = friendDynId
                goodInfo.mainUserId = getLong("userId")
                BaseApp.fixedThreadPool.execute {
                    MineApp.goodDaoManager.insert(goodInfo)
                    discoverInfo.goodNum = discoverInfo.goodNum + 1
                    discoverInfo.isLike = true
                    binding.discoverInfo = discoverInfo
                    SystemClock.sleep(200)
                    EventBus.getDefault().post(friendDynId.toString(), "lobsterDoLike")
                }
            }
            onFailToast { false }
            onFailed {
                if (it.errorMessage == "已经赞过了") {
                    val goodInfo = GoodInfo()
                    goodInfo.friendDynId = friendDynId
                    goodInfo.mainUserId = getLong("userId")
                    BaseApp.fixedThreadPool.execute {
                        MineApp.goodDaoManager.insert(goodInfo)
                        discoverInfo.isLike = true
                        binding.discoverInfo = discoverInfo
                        SystemClock.sleep(200)
                        EventBus.getDefault().post(friendDynId.toString(), "lobsterDoLike")
                    }
                }
            }
        }
    }

    /**
     * 取消点赞
     */
    private fun dynCancelLike() {
        mainDataSource.enqueue({ dynCancelLike(friendDynId) }) {
            onSuccess {
                BaseApp.fixedThreadPool.execute {
                    MineApp.goodDaoManager.deleteGood(friendDynId)
                    discoverInfo.goodNum = discoverInfo.goodNum - 1
                    discoverInfo.isLike = false
                    binding.discoverInfo = discoverInfo
                    SystemClock.sleep(200)
                    EventBus.getDefault().post(friendDynId.toString(), "lobsterCancelLike")
                }
            }
            onFailToast { false }
            onFailed {
                if (it.errorMessage == "已经取消过") {
                    MineApp.goodDaoManager.deleteGood(friendDynId)
                    discoverInfo.isLike = false
                    binding.discoverInfo = discoverInfo
                    SystemClock.sleep(200)
                    EventBus.getDefault().post(friendDynId.toString(), "lobsterCancelLike")
                }
            }
        }
    }

    /**
     * 礼物打赏
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun seeGiftRewards(pageNo: Int) {
        if (pageNo == 1) {
            binding.rewardNum = 0
            binding.rewardInfo = ""
            rewardList.clear()
            rewardAdapter.notifyDataSetChanged()
        }
        mainDataSource.enqueue({ seeGiftRewards(friendDynId, 2, pageNo, 100) }) {
            onSuccess {
                binding.rewardNum = binding.rewardNum!! + it.size
                if (rewardList.size == 0)
                    for (i in 0 until 3.coerceAtMost(it.size)) {
                        rewardList.add(it[i])
                    }
                seeGiftRewards(pageNo + 1)
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
     * 动态评论
     */
    private fun seeReviews() {
        mainDataSource.enqueue({ seeReviews(friendDynId, 1, pageNo, 10) }) {
            onSuccess {
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                var start = reviewList.size
                if (start == 0) {
                    val review = Review()
                    review.image = MineApp.mineInfo.image
                    review.text = "说句打动人心的表白，成功率高达99%"
                    review.mainId = getLong("userId")
                    reviewList.add(review)
                }
                for (item in it) {
                    item.mainId = binding.memberInfo!!.userId
                }
                reviewList.addAll(it)
                if (start > 0) start--
                reviewAdapter.notifyItemRangeChanged(start, reviewList.size)
            }
            onFailed {
                binding.refresh.setEnableLoadMore(false)
                binding.refresh.finishRefresh()
                binding.refresh.finishLoadMore()
                if (reviewList.size == 0) {
                    val review = Review()
                    review.image = MineApp.mineInfo.image
                    review.text = "说句打动人心的表白，成功率高达99%"
                    review.mainId = getLong("userId")
                    reviewList.add(review)
                    reviewAdapter.notifyItemRangeChanged(0, reviewList.size)
                }
            }
        }
    }

    /**
     * 发送评论
     */
    private fun dynDoReview() {
        if (binding.content!!.isEmpty()) {
            SCToastUtil.showToast(activity, "请输入评论内容", 2)
            return
        }
        val map = HashMap<String, String>()
        if (reviewId > 0)
            map["reviewId"] = reviewId.toString()
        map["friendDynId"] = friendDynId.toString()
        map["text"] = binding.content!!
        mainDataSource.enqueueLoading({ dynDoReview(map) }, "提交评论...") {
            onSuccess {
                SCToastUtil.showToast(activity, "发布成功", 2)
                binding.content = ""
                discoverInfo.reviews += 1
                binding.discoverInfo = discoverInfo
                onRefresh(binding.refresh)
                reviewId = 0
                binding.edContent.hint = "表白一句，成功率超高～"
            }
        }
    }

}