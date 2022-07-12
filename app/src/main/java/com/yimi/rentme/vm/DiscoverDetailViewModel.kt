package com.yimi.rentme.vm

import android.os.SystemClock
import android.view.View
import com.yimi.rentme.MineApp
import com.yimi.rentme.activity.MNImageBrowserActivity
import com.yimi.rentme.activity.MemberDetailActivity
import com.yimi.rentme.bean.DiscoverInfo
import com.yimi.rentme.bean.MemberInfo
import com.yimi.rentme.databinding.AcDiscoverDetailBinding
import com.yimi.rentme.roomdata.FollowInfo
import com.yimi.rentme.roomdata.GoodInfo
import com.yimi.rentme.roomdata.LikeTypeInfo
import com.yimi.rentme.utils.imagebrowser.MyMNImage
import com.yimi.rentme.utils.imagebrowser.OnDiscoverClickListener
import com.zb.baselibs.adapter.loadImage
import com.zb.baselibs.adapter.viewSize
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.bean.Ads
import com.zb.baselibs.utils.getLong
import com.zb.baselibs.views.imagebrowser.base.ImageBrowserConfig
import com.zb.baselibs.views.xbanner.ImageLoader
import com.zb.baselibs.views.xbanner.XUtils
import com.zb.baselibs.views.xbanner.XUtils.showBanner
import org.jetbrains.anko.startActivity
import org.simple.eventbus.EventBus

class DiscoverDetailViewModel : BaseViewModel() {

    lateinit var binding: AcDiscoverDetailBinding
    var friendDynId = 0L
    private var discoverInfo = DiscoverInfo()
    private var likeTypeInfo: LikeTypeInfo? = null
    private val adList = ArrayList<Ads>()
    private val sourceImageList = ArrayList<String>()

    override fun initViewModel() {
        binding.title = "动态详情"
        binding.discoverInfo = DiscoverInfo()
        binding.memberInfo = MemberInfo()
        binding.likeTypeInfo = LikeTypeInfo()
        binding.isMine = false
        binding.isFollow = false

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

                            override fun share() {
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
     * 动态详情
     */
    private fun dynDetail() {
        mainDataSource.enqueueLoading({ dynDetail(friendDynId) }, "获取动态详情...") {
            onSuccess {
                discoverInfo = it
                binding.discoverInfo = it
                binding.isMine = discoverInfo.userId == getLong("userId")
                if (it.images.isEmpty()) {
                    adList.add(Ads(it.image))
                    sourceImageList.add(it.image)
                } else
                    for (image in it.images.split(",")) {
                        adList.add(Ads(image))
                        sourceImageList.add(image)
                    }
                setBanner()
                otherInfo()
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
                        SystemClock.sleep(200)
                        EventBus.getDefault().post(friendDynId.toString(), "lobsterDoLike")
                    }
                }
            }
        }
    }
}