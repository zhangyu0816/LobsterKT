package com.yimi.rentme.vm

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.github.chrisbanes.photoview.PhotoView
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.activity.ReportActivity
import com.yimi.rentme.activity.RewardListActivity
import com.yimi.rentme.bean.DiscoverInfo
import com.yimi.rentme.databinding.AcMnimageBrowserBinding
import com.yimi.rentme.dialog.FunctionDF
import com.yimi.rentme.dialog.ReviewDF
import com.yimi.rentme.utils.imagebrowser.MyMNImage
import com.yimi.rentme.utils.imagebrowser.OnDeleteListener
import com.yimi.rentme.utils.imagebrowser.OnDiscoverClickListener
import com.yimi.rentme.utils.imagebrowser.OnFinishListener
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.RemindDF
import com.zb.baselibs.views.imagebrowser.base.ImageBrowserConfig
import com.zb.baselibs.views.imagebrowser.listener.ImageEngine
import com.zb.baselibs.views.imagebrowser.listener.OnClickListener
import com.zb.baselibs.views.imagebrowser.listener.OnLongClickListener
import com.zb.baselibs.views.imagebrowser.transforms.*
import org.jetbrains.anko.startActivity

class MNImageBrowserViewModel : BaseViewModel() {
    lateinit var binding: AcMnimageBrowserBinding

    //图片地址
    private var imageUrlList = ArrayList<String>()

    //当前位置
    private var currentPosition = 0

    //当前切换的动画
    private lateinit var transformType: ImageBrowserConfig.TransformType

    //图片加载引擎
    private lateinit var imageEngine: ImageEngine

    //监听
    private var onLongClickListener: OnLongClickListener? = null
    private var onClickListener: OnClickListener? = null
    private var onDiscoverClickListener: OnDiscoverClickListener? = null
    private var onDeleteListener: OnDeleteListener? = null
    private var onFinishListener: OnFinishListener? = null
    private lateinit var imageBrowserAdapter: MyAdapter

    @SuppressLint("StaticFieldLeak")
    private lateinit var mCurrentView: View

    private var translationY: PropertyValuesHolder? = null
    private var alpha: PropertyValuesHolder? = null
    private var pvh: ObjectAnimator? = null

    @SuppressLint("SetTextI18n")
    override fun initViewModel() {
        imageUrlList = MyMNImage.imageBrowserConfig.imageList
        currentPosition = MyMNImage.imageBrowserConfig.position
        transformType = MyMNImage.imageBrowserConfig.transformType
        imageEngine = MyMNImage.imageBrowserConfig.imageEngine!!
        onClickListener = MyMNImage.imageBrowserConfig.onClickListener
        onLongClickListener = MyMNImage.imageBrowserConfig.onLongClickListener
        onDiscoverClickListener = MyMNImage.imageBrowserConfig.onDiscoverClickListener
        onDeleteListener = MyMNImage.imageBrowserConfig.onDeleteListener
        onFinishListener = MyMNImage.imageBrowserConfig.onFinishListener
        binding.numberIndicator.text = "${currentPosition + 1}/${imageUrlList.size}"
        binding.isFollow = false
        binding.showDelete = onDeleteListener != null

        if (MyMNImage.imageBrowserConfig.discoverInfo == null)
            binding.discoverInfo = DiscoverInfo()
        else {
            binding.discoverInfo = MyMNImage.imageBrowserConfig.discoverInfo
            BaseApp.fixedThreadPool.execute {
                binding.isFollow =
                    MineApp.followDaoManager.getFollowInfo(binding.discoverInfo!!.userId) != null
                binding.discoverInfo!!.isLike =
                    MineApp.goodDaoManager.getGood(binding.discoverInfo!!.friendDynId) != null
            }
        }
        initViewPager()
    }

    override fun back(view: View) {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        activity.finish()
    }

    /**
     * 删除图片
     */
    @SuppressLint("SetTextI18n")
    fun delete(view: View) {
        imageBrowserAdapter.destroyItem(
            binding.viewPagerBrowser, currentPosition, mCurrentView
        )
        imageUrlList.removeAt(currentPosition)
        imageBrowserAdapter.notifyDataSetChanged()
        if (onDeleteListener != null)
            onDeleteListener!!.delete(currentPosition)
        if (imageUrlList.size == 0) {
            back(binding.ivBack)
        } else {
            if (currentPosition > 0) {
                currentPosition--
            }
            binding.numberIndicator.text = "${currentPosition + 1}/${imageUrlList.size}"
            initViewPager()
        }
    }

    /**
     * 关注
     */
    fun follow(view: View) {
        if (onDiscoverClickListener != null) {
            onDiscoverClickListener!!.follow()
        }
    }

    /**
     * 更新关注
     */
    fun updateFollow(isFollow: Boolean) {
        binding.isFollow = isFollow
    }

    /**
     * 点赞
     */
    fun doLike(view: View) {
        if (!binding.discoverInfo!!.isLike) {
            binding.likeLayout.visibility = View.VISIBLE
            translationY = PropertyValuesHolder.ofFloat("translationY", 0f, -50f)
            alpha = PropertyValuesHolder.ofFloat("alpha", 1f, 0f)
            pvh = ObjectAnimator.ofPropertyValuesHolder(binding.likeLayout, translationY, alpha)
                .setDuration(1000)
            pvh!!.repeatCount = Animation.INFINITE
            pvh!!.start()
            BaseApp.fixedThreadPool.execute {
                SystemClock.sleep(1000)
                activity.runOnUiThread {
                    binding.likeLayout.visibility = View.GONE
                }
            }
            if (onDiscoverClickListener != null) {
                onDiscoverClickListener!!.good()
            }
        }
    }

    /**
     * 点赞
     */
    fun doLike(friendDynId: Long) {
        MyMNImage.imageBrowserConfig.discoverInfo!!.goodNum++
        BaseApp.fixedThreadPool.execute {
            MyMNImage.imageBrowserConfig.discoverInfo!!.isLike =
                MineApp.goodDaoManager.getGood(friendDynId) != null
            binding.discoverInfo = MyMNImage.imageBrowserConfig.discoverInfo
        }
    }

    /**
     * 取消点赞
     */
    fun cancelLike(friendDynId: Long) {
        MyMNImage.imageBrowserConfig.discoverInfo!!.goodNum--
        BaseApp.fixedThreadPool.execute {
            MyMNImage.imageBrowserConfig.discoverInfo!!.isLike =
                MineApp.goodDaoManager.getGood(friendDynId) != null
            binding.discoverInfo = MyMNImage.imageBrowserConfig.discoverInfo
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
                    MyMNImage.imageBrowserConfig.discoverInfo!!.reviews += 1
                    binding.discoverInfo = MyMNImage.imageBrowserConfig.discoverInfo
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
                val sharedName =
                    it.text.replace("{userId}", binding.discoverInfo!!.userId.toString())
                        .replace("{nick}", binding.discoverInfo!!.nick)
                val content =
                    binding.discoverInfo!!.text.ifEmpty { binding.discoverInfo!!.friendTitle }
                val sharedUrl: String =
                    BaseApp.baseUrl + "mobile/Dyn_dynDetail?friendDynId=" + binding.discoverInfo!!.friendDynId
                FunctionDF(activity).setUmImage(
                    binding.discoverInfo!!.image.replace(
                        "YM0000",
                        "430X430"
                    )
                ).setSharedName(sharedName).setContent(content).setSharedUrl(sharedUrl)
                    .setOtherUserId(binding.discoverInfo!!.userId).setIsDiscover(true)
                    .setCallBack(object : FunctionDF.CallBack {
                        override fun report() {
                            activity.startActivity<ReportActivity>(
                                Pair("otherUserId", binding.discoverInfo!!.userId)
                            )
                        }

                        override fun rewardList() {
                            activity.startActivity<RewardListActivity>(
                                Pair("friendDynId", binding.discoverInfo!!.friendDynId)
                            )
                        }

                        override fun delete() {
                            RemindDF(activity).setTitle("删除动态").setContent("删除后，动态不可找回！")
                                .setSureName("删除").setCallBack(object : RemindDF.CallBack {
                                    override fun sure() {
                                        if (onFinishListener != null)
                                            onFinishListener!!.onFinish()
                                        activity.finish()
                                    }
                                }).show(activity.supportFragmentManager)
                        }

                        override fun like() {
                        }
                    })
                    .show(activity.supportFragmentManager)
            }
        }

    }

    private fun initViewPager() {
        imageBrowserAdapter = MyAdapter()
        binding.viewPagerBrowser.adapter = imageBrowserAdapter
        binding.viewPagerBrowser.currentItem = currentPosition
        setViewPagerTransforms()
        binding.viewPagerBrowser.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
                Log.e("", position.toString() + "")
                Log.e("", positionOffset.toString() + "")
                Log.e("", positionOffsetPixels.toString() + "")
            }

            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                currentPosition = position
                binding.numberIndicator.text = "${position + 1}/${imageUrlList.size}"
            }

            override fun onPageScrollStateChanged(state: Int) {
                Log.e("", state.toString() + "")
            }
        })
    }

    private fun setViewPagerTransforms() {
        when (transformType) {
            ImageBrowserConfig.TransformType.TransformDefault -> {
                binding.viewPagerBrowser.setPageTransformer(true, DefaultTransformer())
            }
            ImageBrowserConfig.TransformType.TransformDepthPage -> {
                binding.viewPagerBrowser.setPageTransformer(true, DepthPageTransformer())
            }
            ImageBrowserConfig.TransformType.TransformRotateDown -> {
                binding.viewPagerBrowser.setPageTransformer(true, RotateDownTransformer())
            }
            ImageBrowserConfig.TransformType.TransformRotateUp -> {
                binding.viewPagerBrowser.setPageTransformer(true, RotateUpTransformer())
            }
            ImageBrowserConfig.TransformType.TransformZoomIn -> {
                binding.viewPagerBrowser.setPageTransformer(true, ZoomInTransformer())
            }
            ImageBrowserConfig.TransformType.TransformZoomOutSlide -> {
                binding.viewPagerBrowser.setPageTransformer(true, ZoomOutSlideTransformer())
            }
            ImageBrowserConfig.TransformType.TransformZoomOut -> {
                binding.viewPagerBrowser.setPageTransformer(true, ZoomOutTransformer())
            }
            else -> {
                binding.viewPagerBrowser.setPageTransformer(true, DefaultTransformer())
            }
        }
    }

    private inner class MyAdapter : PagerAdapter() {
        private val layoutInflater: LayoutInflater = LayoutInflater.from(activity)
        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            mCurrentView = `object` as View
        }

        override fun getCount(): Int {
            return imageUrlList.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val inflate =
                layoutInflater.inflate(R.layout.mn_image_browser_item_show_image, container, false)
            val imageView = inflate.findViewById<View>(R.id.imageView) as PhotoView
            val url = imageUrlList[position]
            //图片加载
            imageEngine.loadImage(activity, url, imageView)
            imageView.setOnClickListener { //单击事件
                if (onClickListener != null) {
                    onClickListener!!.onClick(activity, imageView, position, url)
                }
            }
            imageView.setOnLongClickListener {
                if (onLongClickListener != null) {
                    onLongClickListener!!.onLongClick(activity, imageView, position, url)
                    binding.saveRelative.visibility = View.VISIBLE
                }
                false
            }
            container.addView(inflate)
            return inflate
        }
    }

    fun closeView(view: View) {
        binding.saveRelative.visibility = View.GONE
    }

    fun downloadImage(view: View) {

    }
}