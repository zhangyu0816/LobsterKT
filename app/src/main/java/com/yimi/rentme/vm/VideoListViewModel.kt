package com.yimi.rentme.vm

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import android.view.View
import android.view.animation.Animation
import android.widget.RelativeLayout
import androidx.recyclerview.widget.OrientationHelper
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.DiscoverInfo
import com.yimi.rentme.bean.Review
import com.yimi.rentme.bean.VideoInfo
import com.yimi.rentme.databinding.AcVideoListBinding
import com.yimi.rentme.databinding.ItemVideoListBinding
import com.yimi.rentme.dialog.ReviewDF
import com.yimi.rentme.roomdata.ImageSize
import com.yimi.rentme.utils.PicSizeUtil
import com.yimi.rentme.utils.water.WaterMark
import com.yimi.rentme.views.DouYinLayoutManager
import com.yimi.rentme.views.OnViewPagerListener
import com.yimi.rentme.views.VideoFunctionView
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.ObjectUtils
import com.zb.baselibs.utils.SCToastUtil
import com.zb.baselibs.utils.awesome.DownLoadUtil
import com.zb.baselibs.utils.getLong
import com.zb.baselibs.utils.getVideoFile
import com.zb.baselibs.utils.permission.requestPermissionsForResult
import com.zb.baselibs.views.FullScreenVideoView
import kotlinx.coroutines.Job

class VideoListViewModel : BaseViewModel(), VideoFunctionView.CallBack {

    lateinit var binding: AcVideoListBinding
    var pageNo = 1
    lateinit var adapter: BaseAdapter<DiscoverInfo>
    private lateinit var douYinLayoutManager: DouYinLayoutManager
    private var position = -1
    private var isUp = false
    private var isOver = false
    private var showPosition = -1
    private var canUpdate = false

    @SuppressLint("StaticFieldLeak")
    private var lastVideoView: FullScreenVideoView? = null

    @SuppressLint("StaticFieldLeak")
    private var videoView: FullScreenVideoView? = null
    private lateinit var discoverInfo: DiscoverInfo
    private lateinit var mBinding: ItemVideoListBinding
    private val reviewList = ArrayList<Review>()
    private var alphaOA: ObjectAnimator? = null
    private var animator: ObjectAnimator? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun initViewModel() {
        adapter = BaseAdapter(activity, R.layout.item_video_list, MineApp.discoverInfoList, this)
        adapter.needItemBinding = true
        douYinLayoutManager = DouYinLayoutManager(activity, OrientationHelper.VERTICAL, false)
        binding.videoList.layoutManager = douYinLayoutManager
        binding.videoList.adapter = adapter
        binding.videoList.scrollToPosition(position)
        adapter.notifyDataSetChanged()

        douYinLayoutManager.setOnViewPagerListener(object : OnViewPagerListener {
            override fun onPageRelease(isNest: Boolean, view: View?) {
                val binding = adapter.getItemBinding() as ItemVideoListBinding
                binding.videoView.stopPlayback() //停止播放视频,并且释放
                binding.videoView.suspend() //在任何状态下释放媒体播放器
                binding.ivImage.visibility = View.VISIBLE
                binding.isProgress = false
                binding.isPlay = false
                binding.videoView.setBackgroundColor(activity.resources.getColor(R.color.black_252))
                binding.reviewList.stop()
                binding.reviewList.visibility = View.GONE
            }

            override fun onPageSelected(isButton: Boolean, view: View?) {
                isScroll = false
                isUp = douYinLayoutManager.drift >= 0
                position = douYinLayoutManager.findFirstCompletelyVisibleItemPosition()
                if (position == -1) return
                if (showPosition == position) return
                showPosition = position

                canUpdate = false

                playVideo(view!!)
                if (!isOver && position == MineApp.discoverInfoList.size - 1 && isUp) {
                    pageNo++
                    dynPiazzaList()
                }
            }

            override fun onScroll() {
                isScroll = true
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (alphaOA != null) {
            alphaOA!!.cancel()
        }
        alphaOA = null
    }

    override fun stopVideo(position: Int) {
        mBinding.videoView.pause()
        mBinding.reviewList.stop()
    }

    override fun download(position: Int) {
        discoverInfo = MineApp.discoverInfoList[position]
        launchMain {
            activity.requestPermissionsForResult(
                Manifest.permission.WRITE_EXTERNAL_STORAGE, rationale = "为了更好的提供服务，需要获取定位权限"
            )
            showLoading(Job(), "下载中...")
            DownLoadUtil.downLoad(
                discoverInfo.videoUrl, getVideoFile(), object : DownLoadUtil.CallBack {
                    override fun onFinish(filePath: String) {
                        val media = MediaMetadataRetriever()
                        media.setDataSource(discoverInfo.videoUrl)
                        val bitmap =
                            media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                        val duration =
                            media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        WaterMark.createWater(
                            activity, filePath, discoverInfo.userId,
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

    override fun onFinish(position: Int) {
    }

    override fun updateAuto(position: Int) {
        reviewList.clear()
        adapter.notifyItemChanged(position)
        seeReviews(MineApp.discoverInfoList[position].friendDynId)
    }

    fun toReviews(discoverInfo: DiscoverInfo, position: Int) {
        ReviewDF(activity).setMainDataSource(mainDataSource)
            .setFriendDynId(discoverInfo.friendDynId)
            .setReviews(discoverInfo.reviews)
            .setOtherUserId(discoverInfo.userId)
            .setCallBack(object : ReviewDF.CallBack {
                override fun sure() {
                    discoverInfo.reviews = discoverInfo.reviews + 1
                    adapter.notifyItemChanged(position)
                }
            })
            .show(activity.supportFragmentManager)
    }

    /**
     * 视频列表
     */
    private fun dynPiazzaList() {
        val map = HashMap<String, String>()
        map["cityId"] = MineApp.cityId.toString()
        map["pageNo"] = pageNo.toString()
        map["dynType"] = "2"
        if (MineApp.sex != -1)
            map["sex"] = if (MineApp.sex == 0) "1" else "0"
        mainDataSource.enqueue({ dynPiazzaList(map) }) {
            onSuccess {
                // 上滑  底部加载更多
                val start: Int = MineApp.discoverInfoList.size
                for (item in it) {
                    BaseApp.fixedThreadPool.execute {
                        item.isLike = MineApp.goodDaoManager.getGood(item.friendDynId) != null
                        item.isMine = item.userId == getLong("userId")
                        MineApp.discoverInfoList.add(item)
                    }
                }
                activity.runOnUiThread {
                    adapter.notifyItemRangeChanged(start, MineApp.discoverInfoList.size)
                }
            }
            onFailed {
                if (it.isNoData) {
                    SCToastUtil.showToast(activity, "视频已加载完毕", 2)
                    isOver = true
                }
            }
        }
    }

    /**
     * 播放视频
     */
    private fun playVideo(view: View) {
        if (lastVideoView != null) {
            lastVideoView!!.stopPlayback() //停止播放视频,并且释放
            lastVideoView!!.suspend() //在任何状态下释放媒体播放器
            lastVideoView = null
        }
        discoverInfo = MineApp.discoverInfoList[position]
        // 访问动态
        mainDataSource.enqueue({ dynVisit(discoverInfo.friendDynId) })

        mBinding = adapter.getItemBinding() as ItemVideoListBinding
        alphaOA = ObjectAnimator.ofFloat(mBinding.ivImage, "alpha", 1f, 0f).setDuration(500)

        animator =
            ObjectAnimator.ofFloat(mBinding.ivProgress, "rotation", 0f, 360f).setDuration(700)
        animator!!.repeatMode = ValueAnimator.RESTART
        animator!!.repeatCount = Animation.INFINITE
        animator!!.start()

        initGood(mBinding.viewClick, mBinding.ivGood, {
            if (mBinding.isPlay)
                stopVideo(position)
            else {
                mBinding.videoView.start()
                mBinding.videoFunctionView.setFollow()
                mBinding.reviewList.start()
            }
        }) {
            mBinding.videoFunctionView.showDoLike()
        }

        // 视频启动页
        BaseApp.fixedThreadPool.execute {
            var imageSize =
                MineApp.imageSizeDaoManager.getImageSize(discoverInfo.images.split(",")[0])
            if (imageSize == null) {
                PicSizeUtil.getPicSize(
                    activity, discoverInfo.images.split(",")[0],
                    object : PicSizeUtil.OnPicListener {
                        override fun onImageSize(width: Int, height: Int) {
                            imageSize = ImageSize()
                            imageSize!!.imageUrl = discoverInfo.images.split(",")[0]
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

            val videoPath = BaseApp.resFileDaoManager.getPath(discoverInfo.videoUrl)
            if (videoPath == null)
                DownLoadUtil.downLoad(
                    discoverInfo.videoUrl, getVideoFile(), object : DownLoadUtil.CallBack {
                        override fun onFinish(filePath: String) {
                            mBinding.videoPath = filePath
                            mBinding.isProgress = false
                            activity.runOnUiThread {
                                animator!!.cancel()
                                animator = null
                            }
                        }
                    })
            else {
                mBinding.videoPath = videoPath
                mBinding.isProgress = false
                activity.runOnUiThread {
                    animator!!.cancel()
                    animator = null
                }
            }
        }

        seeReviews(MineApp.discoverInfoList[position].friendDynId)
    }

    fun playAlphaOA(videoInfo: VideoInfo) {
        mBinding.isPlay = true
        mBinding.isProgress = false
        alphaOA!!.start()
    }
    /**
     * 设置图片
     */
    private fun setImageSize(imageSize: ImageSize) {
        if (ObjectUtils.getViewSizeByHeight(1.0f) * imageSize.width / imageSize.height > BaseApp.W) {
            mBinding.width = BaseApp.W
            mBinding.height = BaseApp.W * imageSize.height / imageSize.width
        } else {
            mBinding.width =
                ObjectUtils.getViewSizeByHeight(1.0f) * imageSize.width / imageSize.height
            mBinding.height = ObjectUtils.getViewSizeByHeight(1.0f)
        }
        mBinding.imageUrl = imageSize.imageUrl
        mBinding.ivImage.alpha = 1.0f
    }
    /**
     * 评论
     */
    private fun seeReviews(friendDynId: Long) {
        showLoading(Job(), "加载访问数据...")
        mainDataSource.enqueue({ seeReviews(friendDynId, 1, 1, 10) }) {
            onSuccess {
                for (item in it) {
                    item.type = 2
                    reviewList.add(item)
                }
                seeLikers(friendDynId)
            }
            onFailToast { false }
            onFailed {
                seeLikers(friendDynId)
            }
        }
    }

    /**
     * 喜欢
     */
    private fun seeLikers(friendDynId: Long) {
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
            mBinding.reviewList.visibility = View.VISIBLE
            adapter.notifyItemRangeChanged(0, reviewList.size)

            if (reviewList.size > 2) {
                mBinding.reviewList.layoutParams = RelativeLayout.LayoutParams(
                    -2, ObjectUtils.getViewSizeByWidthFromMax(400)
                )
                mBinding.reviewList.start()
            } else {
                mBinding.reviewList.layoutParams = RelativeLayout.LayoutParams(-2, -2)
            }
        } else {
            mBinding.reviewList.visibility = View.GONE
            mBinding.reviewList.stop()
        }
    }
}