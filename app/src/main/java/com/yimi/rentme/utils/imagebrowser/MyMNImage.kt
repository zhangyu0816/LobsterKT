package com.yimi.rentme.utils.imagebrowser

import android.content.Context
import android.graphics.Bitmap
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.views.imagebrowser.base.ImageBrowserConfig
import com.zb.baselibs.views.imagebrowser.listener.ImageEngine
import com.zb.baselibs.views.imagebrowser.listener.OnClickListener
import com.zb.baselibs.views.imagebrowser.listener.OnLongClickListener

object MyMNImage {

    var imageBrowserConfig = ImageBrowserConfig()
    private var index: Int = 0
    private lateinit var sourceImageList: ArrayList<String>
    private var transformType: ImageBrowserConfig.TransformType? = null
    private lateinit var callBack: ImageBrowserConfig.StartBack
    private var clickListener: OnClickListener? = null
    private var longClickListener: OnLongClickListener? = null

    fun setIndex(index: Int): MyMNImage {
        MyMNImage.index = index
        return this
    }

    fun setSourceImageList(sourceImageList: ArrayList<String>): MyMNImage {
        MyMNImage.sourceImageList = sourceImageList
        return this
    }

    fun setTransformType(transformType: ImageBrowserConfig.TransformType?): MyMNImage {
        MyMNImage.transformType = transformType
        return this
    }

    fun setCallBack(callBack: ImageBrowserConfig.StartBack): MyMNImage {
        MyMNImage.callBack = callBack
        return this
    }

    fun setClickListener(clickListener: OnClickListener?): MyMNImage {
        MyMNImage.clickListener = clickListener
        return this
    }

    fun setLongClickListener(longClickListener: OnLongClickListener?): MyMNImage {
        MyMNImage.longClickListener = longClickListener
        return this
    }

    fun imageBrowser() {
        imageBrowserConfig.imageList = sourceImageList
        imageBrowserConfig.position = index
        if (transformType != null)
            imageBrowserConfig.transformType = transformType as ImageBrowserConfig.TransformType
        if (clickListener != null)
            imageBrowserConfig.onClickListener = clickListener
        if (longClickListener != null)
            imageBrowserConfig.onLongClickListener = longClickListener
        imageBrowserConfig.imageEngine = object : ImageEngine {
            override fun loadImage(context: Context, url: String, imageView: ImageView) {
                val cropOptions = RequestOptions().fitCenter()
                Glide.with(context).asBitmap().load(url).apply(cropOptions)
                    .into(object : SimpleTarget<Bitmap?>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?,
                        ) {
                            val width = resource.width
                            val height = resource.height
                            val para: ViewGroup.LayoutParams = imageView.layoutParams
                            if (width >= height) {
                                para.width = BaseApp.W
                                para.height =
                                    (resource.height.toFloat() * BaseApp.W / resource.width
                                        .toFloat()).toInt()
                            } else {
                                para.width =
                                    (resource.width.toFloat() * BaseApp.H / resource.height
                                        .toFloat()).toInt()
                                para.height = BaseApp.H
                            }
                            imageView.layoutParams = para
                            imageView.setImageBitmap(resource)
                        }
                    })
            }
        }
        imageBrowserConfig.show(callBack)
    }
}