package com.yimi.rentme.views

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.yimi.rentme.ApiService
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.MemberDetailActivity
import com.yimi.rentme.activity.ReportActivity
import com.yimi.rentme.activity.RewardListActivity
import com.yimi.rentme.bean.DiscoverInfo
import com.yimi.rentme.databinding.VideoFunctionViewBinding
import com.yimi.rentme.dialog.FunctionDF
import com.yimi.rentme.dialog.ReviewDF
import com.yimi.rentme.dialog.SuperLikeDF
import com.yimi.rentme.dialog.VipAdDF
import com.yimi.rentme.roomdata.FollowInfo
import com.yimi.rentme.roomdata.GoodInfo
import com.yimi.rentme.roomdata.LikeTypeInfo
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.RemindDF
import com.zb.baselibs.http.MainDataSource
import com.zb.baselibs.utils.SCToastUtil
import com.zb.baselibs.utils.getLong
import org.jetbrains.anko.startActivity
import org.simple.eventbus.EventBus

/**
 * 视频操作页
 */
class VideoFunctionView : LinearLayout {
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        initView(context)
    }

    constructor(context: Context?) : super(context!!) {
        initView(context)
    }

    private lateinit var binding: VideoFunctionViewBinding
    private lateinit var activity: AppCompatActivity
    private lateinit var mainDataSource: MainDataSource<ApiService>
    private lateinit var discoverInfo: DiscoverInfo
    private lateinit var callBack: CallBack
    private var pvhSY: PropertyValuesHolder? = null
    private var pvhSX: PropertyValuesHolder? = null
    private var pvhA: PropertyValuesHolder? = null
    private var pvhR: PropertyValuesHolder? = null
    private var pvh: ObjectAnimator? = null

    fun setActivity(activity: AppCompatActivity) {
        this.activity = activity
    }

    fun setMainDataSource(mainDataSource: MainDataSource<ApiService>) {
        this.mainDataSource = mainDataSource
    }

    fun setDiscoverInfo(discoverInfo: DiscoverInfo) {
        this.discoverInfo = discoverInfo
        binding.isMine = discoverInfo.userId == getLong("userId")
        BaseApp.fixedThreadPool.execute {
            discoverInfo.isLike = MineApp.goodDaoManager.getGood(discoverInfo.friendDynId) != null
            binding.isFollow = MineApp.followDaoManager.getFollowInfo(discoverInfo.userId) != null
            binding.discoverInfo = discoverInfo
        }
    }

    fun setCallBack(callBack: CallBack) {
        this.callBack = callBack
    }

    private fun initView(context: Context) {
        binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(context), R.layout.video_function_view, null, false
        ) as VideoFunctionViewBinding
        addView(binding.root)
        binding.videoView = this
    }

    /**
     * 用户详情
     */
    fun toMemberDetail(view: View) {
        activity.startActivity<MemberDetailActivity>(
            Pair("otherUserId", binding.discoverInfo!!.userId)
        )
    }

    /**
     * 关注
     */
    fun attentionOther(view: View) {
        if (!binding.isFollow) {
            mainDataSource.enqueue({ attentionOther(binding.discoverInfo!!.userId) }) {
                onSuccess {
                    BaseApp.fixedThreadPool.execute {
                        val followInfo = FollowInfo()
                        followInfo.image = binding.discoverInfo!!.image
                        followInfo.nick = binding.discoverInfo!!.nick
                        followInfo.otherUserId = binding.discoverInfo!!.userId
                        followInfo.mainUserId = getLong("userId")
                        MineApp.followDaoManager.insert(followInfo)
                        activity.runOnUiThread {
                            payAttention()
                        }
                    }
                    EventBus.getDefault().post(true, "lobsterUpdateFollow")
                }
                onFailToast { false }
                onFailed {
                    if (it.errorMessage == "已经关注过") {
                        BaseApp.fixedThreadPool.execute {
                            val followInfo = FollowInfo()
                            followInfo.image = binding.discoverInfo!!.image
                            followInfo.nick = binding.discoverInfo!!.nick
                            followInfo.otherUserId = binding.discoverInfo!!.userId
                            followInfo.mainUserId = getLong("userId")
                            MineApp.followDaoManager.insert(followInfo)

                            activity.runOnUiThread {
                                payAttention()
                            }
                        }
                        EventBus.getDefault().post(true, "lobsterUpdateFollow")
                    }
                }
            }
        }
    }

    /**
     * 点赞
     */
    fun doLike(view: View) {
        if (binding.discoverInfo!!.isLike) {
            binding.ivUnLike.visibility = View.VISIBLE
            binding.ivLike.visibility = View.GONE
            likeOrNot(binding.ivUnLike)
            dynCancelLike()
        } else {
            binding.ivUnLike.visibility = View.GONE
            binding.ivLike.visibility = View.VISIBLE
            likeOrNot(binding.ivLike)
            dynDoLike()
        }
    }

    /**
     * 评论
     */
    fun toReview(view: View) {
        ReviewDF(activity).setMainDataSource(mainDataSource)
            .setFriendDynId(binding.discoverInfo!!.friendDynId)
            .setReviews(binding.discoverInfo!!.reviews)
            .setOtherUserId(binding.discoverInfo!!.userId)
            .setCallBack(object : ReviewDF.CallBack {
                override fun sure() {
                    discoverInfo.reviews = discoverInfo.reviews + 1
                    binding.discoverInfo = discoverInfo
                }
            })
            .show(activity.supportFragmentManager)
    }

    /**
     * 分享
     */
    fun toShare(view: View) {
        mainDataSource.enqueue({ memberInfoConf() }) {
            onSuccess {
                val sharedName = it.text.replace("{userId}", discoverInfo.userId.toString())
                    .replace("{nick}", discoverInfo.nick)
                val content = discoverInfo.text.ifEmpty { discoverInfo.friendTitle }
                val sharedUrl =
                    BaseApp.baseUrl + "mobile/Dyn_dynDetail?friendDynId=" + discoverInfo.friendDynId
                FunctionDF(activity).setUmImage(
                    discoverInfo.image.replace("YM0000", "430X430")
                ).setSharedName(sharedName).setContent(content).setSharedUrl(sharedUrl)
                    .setOtherUserId(discoverInfo.userId).setIsVideo(true)
                    .setIsDiscover(true).setCallBack(object : FunctionDF.CallBack {
                        override fun report() {
                            callBack.stopVideo()
                            activity.startActivity<ReportActivity>(
                                Pair("otherUserId", discoverInfo.userId)
                            )
                        }

                        override fun rewardList() {
                            callBack.stopVideo()
                            activity.startActivity<RewardListActivity>(
                                Pair("friendDynId", binding.discoverInfo!!.friendDynId)
                            )
                        }

                        override fun delete() {
                            RemindDF(activity).setTitle("删除动态").setContent("删除后，动态不可找回！")
                                .setSureName("删除").setCallBack(object : RemindDF.CallBack {
                                    override fun sure() {
                                        deleteDyn()
                                    }
                                }).show(activity.supportFragmentManager)
                        }

                        override fun like() {
                            if (MineApp.mineInfo.memberType == 2) { // 会员
                                makeEvaluate()
                            } else {
                                VipAdDF(activity).setType(3).setOtherImage(discoverInfo.image)
                                    .setMainDataSource(mainDataSource)
                                    .show(activity.supportFragmentManager)
                            }
                        }
                    })
                    .show(activity.supportFragmentManager)
            }
        }
    }

    interface CallBack {
        fun stopVideo()
        fun onFinish()
    }

    /**
     * 关注动画
     */
    private fun payAttention() {
        pvhR = PropertyValuesHolder.ofFloat("rotation", 0f, 90f)
        pvhA = PropertyValuesHolder.ofFloat("alpha", 1f, 0f)
        pvh =
            ObjectAnimator.ofPropertyValuesHolder(binding.ivAttention, pvhR, pvhA).setDuration(200)
        pvh!!.start()
        BaseApp.fixedThreadPool.execute {
            SystemClock.sleep(200)
            activity.runOnUiThread {
                binding.isFollow = true
//                binding.ivAttention.setBackgroundResource(R.drawable.attention_get_icon)
                pvhR = PropertyValuesHolder.ofFloat("rotation", 90f, 0f)
                pvh =
                    ObjectAnimator.ofPropertyValuesHolder(binding.ivAttention, pvhR).setDuration(50)
                pvh!!.start()
            }
            SystemClock.sleep(50)
            activity.runOnUiThread {
                pvhA = PropertyValuesHolder.ofFloat("alpha", 0f, 1f)
                pvh = ObjectAnimator.ofPropertyValuesHolder(binding.ivAttention, pvhA)
                    .setDuration(100)
                pvh!!.start()
            }
            SystemClock.sleep(750)
            activity.runOnUiThread { binding.attentionLayout.visibility = View.INVISIBLE }
        }
    }

    /**
     * 点赞动画
     */
    private fun likeOrNot(view: View) {
        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 0f, 1f, 0.8f, 1f)
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 0f, 1f, 0.8f, 1f)
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
     * 点赞
     */
    private fun dynDoLike() {
        mainDataSource.enqueue({ dynDoLike(binding.discoverInfo!!.friendDynId) }) {
            onSuccess {
                val goodInfo = GoodInfo()
                goodInfo.friendDynId = binding.discoverInfo!!.friendDynId
                goodInfo.mainUserId = getLong("userId")
                BaseApp.fixedThreadPool.execute {
                    MineApp.goodDaoManager.insert(goodInfo)

                    discoverInfo.goodNum = discoverInfo.goodNum + 1
                    discoverInfo.isLike = true
                    binding.discoverInfo = discoverInfo
                    SystemClock.sleep(200)
                    EventBus.getDefault()
                        .post(binding.discoverInfo!!.friendDynId.toString(), "lobsterDoLike")
//                    activity.runOnUiThread {
//                        adapter.notifyItemChanged(prePosition)
//                    }
                }
            }
            onFailToast { false }
            onFailed {
                if (it.errorMessage == "已经赞过了") {
                    val goodInfo = GoodInfo()
                    goodInfo.friendDynId = binding.discoverInfo!!.friendDynId
                    goodInfo.mainUserId = getLong("userId")
                    BaseApp.fixedThreadPool.execute {
                        MineApp.goodDaoManager.insert(goodInfo)
                        discoverInfo.isLike = true
                        binding.discoverInfo = discoverInfo
                        SystemClock.sleep(200)
                        EventBus.getDefault()
                            .post(binding.discoverInfo!!.friendDynId.toString(), "lobsterDoLike")
//                        activity.runOnUiThread {
//                            adapter.notifyItemChanged(prePosition)
//                        }
                    }
                }
            }
        }
    }

    /**
     * 取消点赞
     */
    private fun dynCancelLike() {
        mainDataSource.enqueue({ dynCancelLike(binding.discoverInfo!!.friendDynId) }) {
            onSuccess {
                BaseApp.fixedThreadPool.execute {
                    MineApp.goodDaoManager.deleteGood(binding.discoverInfo!!.friendDynId)
                    discoverInfo.goodNum = discoverInfo.goodNum - 1
                    discoverInfo.isLike = false
                    binding.discoverInfo = discoverInfo
                    SystemClock.sleep(200)
                    EventBus.getDefault()
                        .post(binding.discoverInfo!!.friendDynId.toString(), "lobsterCancelLike")
//                    activity.runOnUiThread {
//                        adapter.notifyItemChanged(prePosition)
//                    }
                }
            }
            onFailToast { false }
            onFailed {
                if (it.errorMessage == "已经取消过") {
                    MineApp.goodDaoManager.deleteGood(binding.discoverInfo!!.friendDynId)
                    discoverInfo.isLike = false
                    binding.discoverInfo = discoverInfo
                    SystemClock.sleep(200)
                    EventBus.getDefault()
                        .post(binding.discoverInfo!!.friendDynId.toString(), "lobsterCancelLike")
//                    activity.runOnUiThread {
//                        adapter.notifyItemChanged(prePosition)
//                    }
                }
            }
        }
    }

    /**
     * 删除动态
     */
    private fun deleteDyn() {
        mainDataSource.enqueueLoading({ deleteDyn(discoverInfo.friendDynId) }) {
            onSuccess {
                EventBus.getDefault().post("删除动态", "lobsterDeleteDyn")
                SCToastUtil.showToast(activity, "删除成功", 2)
                callBack.onFinish()
            }
        }
    }

    /**
     * 喜欢/超级喜欢
     */
    private fun makeEvaluate() {
        mainDataSource.enqueue({ makeEvaluate(discoverInfo.userId, 2) }) {
            onSuccess {
                val myHead = MineApp.mineInfo.image
                val otherHead = binding.discoverInfo!!.image
                // 1喜欢成功 2匹配成功 3喜欢次数用尽
                if (it == 1) {
                    SuperLikeDF(activity).setMyHead(myHead).setOtherHead(otherHead)
                        .setMySex(MineApp.mineInfo.sex)
                        .setOtherSex(binding.discoverInfo!!.sex)
                        .show(activity.supportFragmentManager)
                    EventBus.getDefault().post("更新关注/粉丝/喜欢", "lobsterUpdateFCL")
                    BaseApp.fixedThreadPool.execute {
                        if (MineApp.likeTypeDaoManager.getLikeTypeInfo(binding.discoverInfo!!.userId) == null) {
                            val likeTypeInfo = LikeTypeInfo()
                            likeTypeInfo.likeType = 2
                            likeTypeInfo.otherUserId = binding.discoverInfo!!.userId
                            likeTypeInfo.mainUserId = getLong("userId")
                            MineApp.likeTypeDaoManager.insert(likeTypeInfo)
                        } else {
                            MineApp.likeTypeDaoManager.updateLikeType(
                                2,
                                binding.discoverInfo!!.userId
                            )
                        }
                    }
                } else if (it == 4) {
                    // 超级喜欢时，非会员或超级喜欢次数用尽
                    SCToastUtil.showToast(activity, "今日超级喜欢次数已用完", 2)
                } else {
                    BaseApp.fixedThreadPool.execute {
                        if (MineApp.likeTypeDaoManager.getLikeTypeInfo(binding.discoverInfo!!.userId) == null) {
                            val likeTypeInfo = LikeTypeInfo()
                            likeTypeInfo.likeType = 2
                            likeTypeInfo.otherUserId = binding.discoverInfo!!.userId
                            likeTypeInfo.mainUserId = getLong("userId")
                            MineApp.likeTypeDaoManager.insert(likeTypeInfo)
                        } else {
                            MineApp.likeTypeDaoManager.updateLikeType(
                                2,
                                binding.discoverInfo!!.userId
                            )
                        }
                    }
                    SCToastUtil.showToast(activity, "你已超级喜欢过对方", 2)
                }
            }
        }
    }
}