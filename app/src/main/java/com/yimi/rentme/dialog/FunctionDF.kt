package com.yimi.rentme.dialog

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import com.yimi.rentme.R
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.databinding.DfFunctionBinding
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.bean.ShareItem
import com.zb.baselibs.dialog.BaseDialogFragment
import com.zb.baselibs.utils.SCToastUtil
import com.zb.baselibs.utils.clipboard
import com.zb.baselibs.utils.getLong

class FunctionDF(activity: AppCompatActivity) : BaseDialogFragment(activity) {

    private lateinit var binding: DfFunctionBinding
    private lateinit var umImage: UMImage
    private var sharedName = ""
    private var content = ""
    private var sharedUrl = ""
    private var otherUserId = 0L
    private var isDiscover = false
    private var isList = false
    var isVideo = false
    private var callBack: CallBack? = null

    lateinit var topAdapter: BaseAdapter<ShareItem>
    private val shareList = ArrayList<ShareItem>()

    lateinit var bottomAdapter: BaseAdapter<ShareItem>
    private val funcList = ArrayList<ShareItem>()
    private lateinit var web: UMWeb
    private lateinit var media: SHARE_MEDIA

    override val layoutId: Int
        get() = R.layout.df_function

    fun setUmImage(url: String): FunctionDF {
        umImage = UMImage(activity, url)
        umImage.compressStyle = UMImage.CompressStyle.SCALE //大小压缩，默认为大小压缩，适合普通很大的图
        umImage.compressFormat = Bitmap.CompressFormat.PNG
        return this
    }

    fun setSharedName(sharedName: String): FunctionDF {
        this.sharedName = sharedName
        return this
    }

    fun setContent(content: String): FunctionDF {
        this.content = content.ifEmpty { "这里藏着喜欢你的人" }
        return this
    }

    fun setSharedUrl(sharedUrl: String): FunctionDF {
        this.sharedUrl = sharedUrl
        return this
    }

    fun setOtherUserId(otherUserId: Long): FunctionDF {
        this.otherUserId = otherUserId
        return this
    }

    fun setIsDiscover(isDiscover: Boolean): FunctionDF {
        this.isDiscover = isDiscover
        return this
    }

    fun setIsList(isList: Boolean): FunctionDF {
        this.isList = isList
        return this
    }

    fun setIsVideo(isVideo: Boolean): FunctionDF {
        this.isVideo = isVideo
        return this
    }

    fun setCallBack(callBack: CallBack?): FunctionDF {
        this.callBack = callBack
        return this
    }

    override fun setDataBinding(viewDataBinding: ViewDataBinding?) {
        binding = viewDataBinding as DfFunctionBinding
    }

    fun show(manager: FragmentManager) {
        show(manager, "${BaseApp.projectName}_FunctionDF")
    }

    override fun initUI() {
        binding.dialog = this
        binding.isVideo = isVideo
        binding.showBottom = callBack != null

        shareList.add(ShareItem(R.mipmap.share_wx_ico, "微信", SHARE_MEDIA.WEIXIN))
        shareList.add(ShareItem(R.mipmap.share_wxcircle_ico, "朋友圈", SHARE_MEDIA.WEIXIN_CIRCLE))
        shareList.add(ShareItem(R.mipmap.share_qq_ico, "QQ", SHARE_MEDIA.QQ))
        shareList.add(ShareItem(R.mipmap.share_qqzore_ico, "QQ空间", SHARE_MEDIA.QZONE))
        topAdapter = BaseAdapter(activity, R.layout.item_share_function, shareList, this)

        if (binding.showBottom) {
            if (otherUserId == getLong("userId")) { // 自己的动态
                if (isDiscover) {
                    funcList.add(
                        ShareItem(
                            if (isVideo) R.mipmap.share_gift_ico else R.mipmap.share_gift_white_ico,
                            "查看礼物", null
                        )
                    )
                    if (!isList)
                        funcList.add(
                            ShareItem(
                                if (isVideo) R.mipmap.share_delete_ico else R.mipmap.share_delete_white_ico,
                                "删除动态", null
                            )
                        )
                }
                funcList.add(
                    ShareItem(
                        if (isVideo) R.mipmap.share_copy_ico else R.mipmap.share_copy_white_ico,
                        "复制链接", null
                    )
                )
            } else { // 别人的动态
                funcList.add(
                    ShareItem(
                        if (isVideo) R.mipmap.share_report_ico else R.mipmap.share_report_white_ico,
                        "举报", null
                    )
                )
                if (isVideo)
                    funcList.add(ShareItem(R.mipmap.share_download_ico, "保存至相册", null))
                funcList.add(
                    ShareItem(
                        if (isVideo) R.mipmap.share_copy_ico else R.mipmap.share_copy_white_ico,
                        "复制链接", null
                    )
                )
                funcList.add(
                    ShareItem(
                        if (isVideo) R.mipmap.share_like_ico else R.mipmap.share_like_white_ico,
                        "超级喜欢", null
                    )
                )
            }

            bottomAdapter = BaseAdapter(activity, R.layout.item_share_function, funcList, this)
        }
        web = UMWeb(sharedUrl)
        web.setThumb(umImage)
    }

    /**
     * 选择
     */
    fun selectItem(item: ShareItem) {
        when (item.shareName) {
            "微信" -> {
                web.title = sharedName //标题
                web.description = content //描述
                media = SHARE_MEDIA.WEIXIN
            }
            "朋友圈" -> {
                web.title = """
                        $sharedName
                        $content
                        """.trimIndent() //标题
                web.description = """
                        $sharedName
                        $content
                        """.trimIndent() //描述
                media = SHARE_MEDIA.WEIXIN_CIRCLE
            }
            "QQ" -> {
                web.title = sharedName //标题
                web.description = content //描述
                media = SHARE_MEDIA.QQ
            }
            "QQ空间" -> {
                web.title = sharedName //标题
                web.description = content //描述
                media = SHARE_MEDIA.QZONE
            }
            "举报" -> if (callBack != null) callBack!!.report()
            "查看礼物" -> if (callBack != null) callBack!!.rewardList()
            "删除动态" -> if (callBack != null) callBack!!.delete()
            "超级喜欢" -> if (callBack != null) callBack!!.like()
            "保存至相册" -> if (callBack != null) callBack!!.download()
            "复制链接" -> {
                BaseApp.context.clipboard(sharedUrl)
                SCToastUtil.showToast(activity, "复制成功", 2)
            }
        }

        if (item.shareName == "微信" || item.shareName == "朋友圈" || item.shareName == "QQ" || item.shareName == "QQ空间")
            ShareAction(activity).setPlatform(media)
                .withMedia(web)
                .setCallback(umShareListener)
                .share()
        dismiss()
    }

    fun cancel(view: View) {
        dismiss()
    }

    interface CallBack {
        fun report() // 举报
        fun rewardList() {} // 礼物
        fun delete() {} // 删除动态
        fun like() {} // 喜欢
        fun download() {} // 下载
    }

    private val umShareListener: UMShareListener = object : UMShareListener {
        override fun onStart(platform: SHARE_MEDIA) {
            SCToastUtil.showToast(activity, "开始分享", 2)
        }

        override fun onResult(platform: SHARE_MEDIA) {
            SCToastUtil.showToast(activity, "分享成功啦", 2)
        }

        override fun onError(platform: SHARE_MEDIA, t: Throwable) {
            SCToastUtil.showToast(activity, "分享失败啦", 2)
            Log.d("throw", "throw:" + t.message)
        }

        override fun onCancel(platform: SHARE_MEDIA) {
            SCToastUtil.showToast(activity, "分享取消了", 2)
        }
    }
}