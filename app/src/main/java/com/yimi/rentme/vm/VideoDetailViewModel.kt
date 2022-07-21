package com.yimi.rentme.vm

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.animation.Animation
import com.yimi.rentme.MineApp
import com.yimi.rentme.bean.DiscoverInfo
import com.yimi.rentme.bean.MemberInfo
import com.yimi.rentme.databinding.AcVideoDetailBinding
import com.yimi.rentme.roomdata.ImageSize
import com.yimi.rentme.utils.PicSizeUtil
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.ObjectUtils
import com.zb.baselibs.utils.RomUtils
import com.zb.baselibs.utils.awesome.DownLoadUtil
import com.zb.baselibs.utils.getVideoFile

class VideoDetailViewModel : BaseViewModel() {

    lateinit var binding: AcVideoDetailBinding
    var friendDynId = 0L
    private var animator: ObjectAnimator? = null

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
        animator = ObjectAnimator.ofFloat(binding.ivProgress, "rotation", 0f, 360f).setDuration(700)
        animator!!.repeatMode = ValueAnimator.RESTART
        animator!!.repeatCount = Animation.INFINITE
        animator!!.start()
        dynDetail()
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

                otherInfo()
            }
        }
    }

    /**
     * 设置图片
     */
    private fun setImageSize(imageSize:ImageSize){
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
}