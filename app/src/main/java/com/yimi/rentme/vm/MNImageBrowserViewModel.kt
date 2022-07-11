package com.yimi.rentme.vm

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.github.chrisbanes.photoview.PhotoView
import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcMnimageBrowserBinding
import com.yimi.rentme.utils.imagebrowser.MyMNImage
import com.zb.baselibs.views.imagebrowser.base.ImageBrowserConfig
import com.zb.baselibs.views.imagebrowser.listener.ImageEngine
import com.zb.baselibs.views.imagebrowser.listener.OnClickListener
import com.zb.baselibs.views.imagebrowser.listener.OnLongClickListener
import com.zb.baselibs.views.imagebrowser.transforms.*

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
    private lateinit var imageBrowserAdapter: MyAdapter

    @SuppressLint("StaticFieldLeak")
    private lateinit var mCurrentView: View

    @SuppressLint("SetTextI18n")
    override fun initViewModel() {
        imageUrlList = MyMNImage.imageBrowserConfig.imageList
        currentPosition = MyMNImage.imageBrowserConfig.position
        transformType = MyMNImage.imageBrowserConfig.transformType
        imageEngine = MyMNImage.imageBrowserConfig.imageEngine!!
        onClickListener = MyMNImage.imageBrowserConfig.onClickListener
        onLongClickListener = MyMNImage.imageBrowserConfig.onLongClickListener
        binding.numberIndicator.text = "${currentPosition + 1}/${imageUrlList.size}"
        initViewPager()
    }

    override fun back(view: View) {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        activity.finish()
    }

    @SuppressLint("SetTextI18n")
    fun delete(view: View) {
        imageBrowserAdapter.destroyItem(
            binding.viewPagerBrowser, currentPosition, mCurrentView
        )
        imageUrlList.removeAt(currentPosition)
        imageBrowserAdapter.notifyDataSetChanged()
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
                if (onClickListener != null)
                    onClickListener!!.onClick(activity, imageView, position, url)
            }
            imageView.setOnLongClickListener {
                if (onLongClickListener != null)
                    onLongClickListener!!.onLongClick(activity, imageView, position, url)
                false
            }
            container.addView(inflate)
            return inflate
        }

    }
}