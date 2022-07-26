package com.yimi.rentme.vm

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.media.MediaMetadataRetriever
import android.view.View
import android.view.animation.Animation
import android.widget.RelativeLayout
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.MemberDetailActivity
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.DiscoverInfo
import com.yimi.rentme.bean.MemberInfo
import com.yimi.rentme.bean.Review
import com.yimi.rentme.databinding.AcVideoDetailBinding
import com.yimi.rentme.dialog.ReviewDF
import com.yimi.rentme.roomdata.ImageSize
import com.yimi.rentme.utils.PicSizeUtil
import com.yimi.rentme.utils.water.WaterMark
import com.yimi.rentme.views.VideoFunctionView
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.ObjectUtils
import com.zb.baselibs.utils.RomUtils
import com.zb.baselibs.utils.awesome.DownLoadUtil
import com.zb.baselibs.utils.getLong
import com.zb.baselibs.utils.getVideoFile
import com.zb.baselibs.utils.permission.requestPermissionsForResult
import kotlinx.coroutines.Job
import org.jetbrains.anko.startActivity

class VideoDetailViewModel : BaseViewModel(), VideoFunctionView.CallBack {

    lateinit var binding: AcVideoDetailBinding
    var friendDynId = 0L
    private var animator: ObjectAnimator? = null
    lateinit var adapter: BaseAdapter<Review>
    private val reviewList = ArrayList<Review>()
    private var isFirst = true

    override fun initViewModel() {
        if (!RomUtils.isHuawei) {
            fitComprehensiveScreen()
        }
        binding.videoPath = ""
        binding.imageUrl = ""
        binding.width = 0
        binding.height = 0
        binding.discoverInfo = DiscoverInfo()
        binding.memberInfo = MemberInfo()
        binding.isProgress = true
        binding.isPlay = true
        binding.activity = activity
        animator = ObjectAnimator.ofFloat(binding.ivProgress, "rotation", 0f, 360f).setDuration(700)
        animator!!.repeatMode = ValueAnimator.RESTART
        animator!!.repeatCount = Animation.INFINITE
        animator!!.start()

        adapter = BaseAdapter(activity, R.layout.item_video_review, reviewList, this)

        initGood(binding.viewClick, binding.ivGood, {
            if (binding.isPlay)
                stopVideo()
            else
                onResume()
        }) {
            binding.videoFunctionView.doLike(null)
        }
        dynDetail()
    }

    override fun back(view: View) {
        super.back(view)
        stopVideo()
        activity.finish()
    }

    fun onResume() {
        if (!isFirst) {
            binding.videoView.start()
            binding.videoFunctionView.setFollow()
            binding.reviewList.start()
        }
        isFirst = false
    }

    override fun stopVideo() {
        binding.videoView.pause()
        binding.reviewList.stop()
    }

    override fun download() {
        launchMain {
            activity.requestPermissionsForResult(
                Manifest.permission.WRITE_EXTERNAL_STORAGE, rationale = "为了更好的提供服务，需要获取定位权限"
            )
            showLoading(Job(), "下载中...")
            DownLoadUtil.downLoad(
                binding.discoverInfo!!.videoUrl, getVideoFile(), object : DownLoadUtil.CallBack {
                    override fun onFinish(filePath: String) {
                        val media = MediaMetadataRetriever()
                        media.setDataSource(binding.discoverInfo!!.videoUrl)
                        val bitmap =
                            media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                        val duration =
                            media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        WaterMark.createWater(
                            activity, filePath, binding.discoverInfo!!.userId,
                            bitmap!!.width, bitmap.height, duration!!.toLong(),
                            object : WaterMark.CallBack {
                                override fun sure() {
                                    dismissLoading()
                                }
                            }
                        )
                    }
                })
        }
    }

    override fun onFinish() {
        back(binding.ivBack)
    }

    /**
     * 访问评论用户详情
     */
    fun toMemberDetail(otherUserId: Long) {
        if (otherUserId != getLong("userId")) {
            stopVideo()
            activity.startActivity<MemberDetailActivity>(
                Pair("otherUserId", otherUserId)
            )
        }
    }

    /**
     * 评论列表
     */
    fun toReview(view: View) {
        ReviewDF(activity).setMainDataSource(mainDataSource)
            .setFriendDynId(binding.discoverInfo!!.friendDynId)
            .setReviews(binding.discoverInfo!!.reviews)
            .setOtherUserId(binding.discoverInfo!!.userId)
            .setCallBack(object : ReviewDF.CallBack {
                override fun sure() {
                    binding.discoverInfo!!.reviews = binding.discoverInfo!!.reviews + 1
                }
            })
            .show(activity.supportFragmentManager)
    }

    /**
     * 动态详情
     */
    private fun dynDetail() {
        mainDataSource.enqueue({ dynDetail(friendDynId) }) {
            onSuccess {
                binding.discoverInfo = it
                // 视频启动页
                BaseApp.fixedThreadPool.execute {
                    var imageSize =
                        MineApp.imageSizeDaoManager.getImageSize(it.images.split(",")[0])
                    if (imageSize == null) {
                        PicSizeUtil.getPicSize(
                            activity, it.images.split(",")[0],
                            object : PicSizeUtil.OnPicListener {
                                override fun onImageSize(width: Int, height: Int) {
                                    imageSize = ImageSize()
                                    imageSize!!.imageUrl = it.images.split(",")[0]
                                    imageSize!!.width = width
                                    imageSize!!.height = height
                                    BaseApp.fixedThreadPool.execute {
                                        MineApp.imageSizeDaoManager.insert(imageSize!!)
                                    }
                                    setImageSize(imageSize!!)
                                }
                            })
                    } else {
                        setImageSize(imageSize!!)
                    }

                    val videoPath = BaseApp.resFileDaoManager.getPath(it.videoUrl)
                    if (videoPath == null)
                        DownLoadUtil.downLoad(
                            it.videoUrl, getVideoFile(), object : DownLoadUtil.CallBack {
                                override fun onFinish(filePath: String) {
                                    binding.videoPath = filePath
                                    binding.isProgress = false
                                    activity.runOnUiThread {
                                        animator!!.cancel()
                                        animator = null
                                    }
                                }
                            })
                    else {
                        binding.videoPath = videoPath
                        binding.isProgress = false
                        activity.runOnUiThread {
                            animator!!.cancel()
                            animator = null
                        }
                    }
                }
                seeReviews()
                otherInfo()
                dynVisit()
            }
        }
    }

    /**
     * 访问动态
     */
    private fun dynVisit() {
        mainDataSource.enqueue({ dynVisit(friendDynId) })
    }

    /**
     * 设置图片
     */
    private fun setImageSize(imageSize: ImageSize) {
        if (ObjectUtils.getViewSizeByHeight(1.0f) * imageSize.width / imageSize.height > BaseApp.W) {
            binding.width = BaseApp.W
            binding.height = BaseApp.W * imageSize.height / imageSize.width
        } else {
            binding.width =
                ObjectUtils.getViewSizeByHeight(1.0f) * imageSize.width / imageSize.height
            binding.height = ObjectUtils.getViewSizeByHeight(1.0f)
        }
        binding.imageUrl = imageSize.imageUrl
        binding.ivImage.alpha = 1.0f
    }

    /**
     * 用户信息
     */
    private fun otherInfo() {
        mainDataSource.enqueue({ otherInfo(binding.discoverInfo!!.userId) }) {
            onSuccess {
                binding.memberInfo = it
            }
        }
    }

    /**
     * 评论
     */
    private fun seeReviews() {
        showLoading(Job(), "加载访问数据...")
        mainDataSource.enqueue({ seeReviews(friendDynId, 1, 1, 10) }) {
            onSuccess {
                for (item in it) {
                    item.type = 2
                    reviewList.add(item)
                }
                seeLikers()
            }
            onFailToast { false }
            onFailed {
                seeLikers()
            }
        }
    }

    /**
     * 喜欢
     */
    private fun seeLikers() {
        mainDataSource.enqueue({ seeLikers(friendDynId, 1, 20) }) {
            onSuccess {
                for (item in it) {
                    item.type = 1
                    reviewList.add(item)
                }
                dismissLoading()
                showAutoList()
            }
            onFailToast { false }
            onFailed {
                dismissLoading()
                showAutoList()
            }
        }
    }

    /**
     * 显示列表
     */
    private fun showAutoList() {
        if (reviewList.size > 0) {
            binding.reviewList.visibility = View.VISIBLE
            adapter.notifyItemRangeChanged(0, reviewList.size)

            if (reviewList.size > 2) {
                binding.reviewList.layoutParams = RelativeLayout.LayoutParams(
                    -2, ObjectUtils.getViewSizeByWidthFromMax(400)
                )
                binding.reviewList.start()
            } else {
                binding.reviewList.layoutParams = RelativeLayout.LayoutParams(-2, -2)
            }
        }
    }
}