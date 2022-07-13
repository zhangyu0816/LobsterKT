package com.yimi.rentme.utils.imagebrowser

import android.content.Context
import android.graphics.Bitmap
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.yimi.rentme.bean.DiscoverInfo
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.views.imagebrowser.base.ImageBrowserConfig
import com.zb.baselibs.views.imagebrowser.listener.ImageEngine
import com.zb.baselibs.views.imagebrowser.listener.OnClickListener
import com.zb.baselibs.views.imagebrowser.listener.OnLongClickListener

object MyMNImage {

    var imageBrowserConfig = MyImageBrowserConfig()

    fun setIndex(index: Int): MyMNImage {
        imageBrowserConfig.position = index
        return this
    }

    fun setSourceImageList(sourceImageList: ArrayList<String>): MyMNImage {
        imageBrowserConfig.imageList = sourceImageList
        return this
    }

    fun setTransformType(transformType: ImageBrowserConfig.TransformType): MyMNImage {
        imageBrowserConfig.transformType = transformType
        return this
    }

    fun setCallBack(callBack: ImageBrowserConfig.StartBack): MyMNImage {
        imageBrowserConfig.callBack = callBack
        return this
    }

    fun setClickListener(clickListener: OnClickListener?): MyMNImage {
        imageBrowserConfig.onClickListener = clickListener
        return this
    }

    fun setLongClickListener(longClickListener: OnLongClickListener?): MyMNImage {
        imageBrowserConfig.onLongClickListener = longClickListener
        return this
    }

    fun setDiscoverClickListener(discoverClickListener: OnDiscoverClickListener): MyMNImage {
        imageBrowserConfig.onDiscoverClickListener = discoverClickListener
        return this
    }

    fun setDeleteListener(deleteListener: OnDeleteListener):MyMNImage{
        imageBrowserConfig.onDeleteListener = deleteListener
        return this
    }

    fun setDiscoverInfo(discoverInfo: DiscoverInfo): MyMNImage {
        imageBrowserConfig.discoverInfo = discoverInfo
        return this
    }

    fun imageBrowser() {
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
        imageBrowserConfig.show(imageBrowserConfig.callBack)
    }
}