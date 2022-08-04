package com.yimi.rentme.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.yimi.rentme.ApiService
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.VipInfo
import com.yimi.rentme.databinding.DfVipAdBinding
import com.yimi.rentme.databinding.ItemAdBinding
import com.zb.baselibs.adapter.viewSize
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.bean.Ads
import com.zb.baselibs.dialog.BaseDialogFragment
import com.zb.baselibs.http.MainDataSource
import com.zb.baselibs.utils.ObjectUtils
import com.zb.baselibs.utils.SCToastUtil
import com.zb.baselibs.views.xbanner.ImageLoader
import com.zb.baselibs.views.xbanner.XBanner
import org.simple.eventbus.EventBus

class VipAdDF(activity: AppCompatActivity) : BaseDialogFragment(activity) {

    private lateinit var binding: DfVipAdBinding
    private val adsList = ArrayList<Ads>()
    lateinit var adapter: BaseAdapter<VipInfo>
    private var type = 0
    private var otherImage = ""
    private lateinit var mainDataSource: MainDataSource<ApiService>
    private var mPosition = -1

    override val layoutId: Int
        get() = R.layout.df_vip_ad

    fun setType(type: Int): VipAdDF {
        this.type = type
        return this
    }

    fun setOtherImage(otherImage: String): VipAdDF {
        this.otherImage = otherImage
        return this
    }

    fun setMainDataSource(mainDataSource: MainDataSource<ApiService>): VipAdDF {
        this.mainDataSource = mainDataSource
        return this
    }

    override fun setDataBinding(viewDataBinding: ViewDataBinding?) {
        binding = viewDataBinding as DfVipAdBinding
    }

    fun show(manager: FragmentManager) {
        show(manager, "${BaseApp.projectName}_VipAdDF")
    }

    override fun onStart() {
        super.onStart()
        center(1.0)
    }

    override fun initUI() {
        binding.dialog = this
        adapter = BaseAdapter(
            activity,
            if (MineApp.isFirstOpen) if (type == 100) R.layout.item_first_vip_ad else R.layout.item_first_0_vip_ad else R.layout.item_vip_ad,
            MineApp.vipInfoList, this
        )
        binding.isFirstOpen = MineApp.isFirstOpen
        binding.type = type
        mPosition = if (MineApp.vipInfoList.size < 2) {
            MineApp.vipInfoList.size - 1
        } else {
            1
        }
        if (mPosition >= 0) {
            adapter.setSelectIndex(mPosition)
            adapter.notifyItemChanged(mPosition)
        }
        setBtn()

        binding.banner.viewSize(
            ObjectUtils.getViewSizeByWidthFromMax(1000),
            ObjectUtils.getVipExposureHeight(1000)
        )

        if (MineApp.isFirstOpen && type == 100) {
            adsList.add(Ads(adView(type)))
        } else {
            if (type == 0) {
                for (i in 1..8) {
                    adsList.add(Ads(adView(i)))
                }
            } else {
                adsList.add(Ads(adView(type)))
            }
        }

        binding.banner.setImageScaleType(ImageView.ScaleType.FIT_XY)
            .setAds(adsList)
            .setImageLoader(object : ImageLoader {
                override fun loadImages(
                    context: Context?, ads: Ads?, image: ImageView?, position: Int
                ) {
                }

                override fun loadView(linearLayout: LinearLayout?, adView: View?) {
                    super.loadView(linearLayout, adView)
                    if (adView!!.parent != null) {
                        (adView.parent as ViewGroup).removeView(adView)
                    }
                    linearLayout!!.addView(adView)
                    linearLayout.viewSize(
                        ObjectUtils.getViewSizeByWidthFromMax(1000),
                        ObjectUtils.getVipExposureHeight(1000)
                    )
                }
            })
            .setBannerTypes(XBanner.CIRCLE_INDICATOR_TITLE)
            .setIndicatorGravity(XBanner.INDICATOR_START)
            .setDelay(5000)
            .setUpIndicators(R.drawable.vip_circle_pressed, R.drawable.vip_circle_unpressed)
            .isAutoPlay(false)
            .setShowBg(true)
            .setBannerRadii(floatArrayOf(20f, 20f, 0f, 0f))
            .setType(if (MineApp.isFirstOpen && type == 100) 0 else type)
            .start()
    }

    fun selectIndex(position: Int) {
        adapter.setSelectIndex(position)
        if (mPosition != -1)
            adapter.notifyItemChanged(mPosition)
        adapter.notifyItemChanged(position)
        mPosition = position
        setBtn()
    }

    fun sure(view: View) {
        if (mPosition == -1) {
            SCToastUtil.showToast(activity, "请选择VIP套餐", 2)
            return
        }
        submitOpenedMemberOrder()
    }

    fun cancel(view: View) {
        binding.banner.releaseBanner()
        dismiss()
        if (type == 7) {
            EventBus.getDefault().post("", "lobsterFlashChat")
        }
    }

    /**
     * 提交VIP订单
     */
    private fun submitOpenedMemberOrder() {
        mainDataSource.enqueueLoading({
            submitOpenedMemberOrder(
                MineApp.vipInfoList[mPosition].memberOfOpenedProductId, 1
            )
        }, "提交VIP订单...") {
            onSuccess {
                payOrderForTran(it.orderNumber)
            }
        }
    }

    /**
     * 获取交易订单号
     */
    private fun payOrderForTran(orderNumber: String) {
        mainDataSource.enqueue({ payOrderForTran(orderNumber) }) {
            onSuccess {
                PaymentDF(activity).setOrderTran(it).setMainDataSource(mainDataSource)
                    .setPayType(1).show(activity.supportFragmentManager)
                binding.banner.releaseBanner()
                dismiss()
            }
        }
    }

    /**
     * 底部按钮
     */
    private fun setBtn() {
        binding.vipInfo = MineApp.vipInfoList[mPosition]
        val vipInfo = binding.vipInfo!!
        if (MineApp.isFirstOpen) {
            binding.tvBtn.text = activity.resources.getString(
                R.string.open_btn_month, vipInfo.dayCount / 30 * 2, vipInfo.price
            )
        } else if (MineApp.mineInfo.memberType == 2) {
            binding.tvBtn.text = "立即续费VIP特权"
        } else {
            binding.tvBtn.text = activity.resources.getString(R.string.open_btn, vipInfo.price)
        }
    }

    /**
     * 添加图片
     */
    private fun adView(type: Int): View {
        val binding: ItemAdBinding =
            DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.item_ad, null, false)
        binding.type = type
        binding.sex = MineApp.mineInfo.sex
        if (MineApp.isFirstOpen && type == 100) {
            binding.title = ""
            binding.content = ""
            binding.myHead = "empty_icon"
            binding.otherHead = "empty_icon"
            binding.ivBigBg.setBackgroundResource(R.mipmap.icon_first_ad)
            binding.ivBigBg.viewSize(
                ObjectUtils.getViewSizeByWidthFromMax(1000),
                ObjectUtils.getVipExposureHeight(1000)
            )
        } else {
            when (type) {
                1 -> {
                    binding.title = "超级曝光"
                    binding.content = "增加10倍曝光度，让更多人先发现你"
                    binding.myHead = MineApp.mineInfo.image.replace("YM0000", "240X240")
                    binding.otherHead = "empty_icon"
                    binding.ivVipBg.setBackgroundResource(R.drawable.empty_bg)
                }
                2 -> {
                    binding.title = "划错反悔"
                    binding.content = "手滑了？立即找回不要错过任何一个缘分！"
                    binding.myHead = "empty_icon"
                    binding.otherHead = "empty_icon"
                    binding.ivVipBg.setBackgroundResource(if (MineApp.mineInfo.sex == 0) R.mipmap.vip_ad_2_male else R.mipmap.vip_ad_2)
                }
                3 -> {
                    binding.title = "超级喜欢"
                    binding.content = "每天10个超级喜欢，开通专属私信通道"
                    binding.myHead = MineApp.mineInfo.image.replace("YM0000", "240X240")
                    binding.otherHead =
                        otherImage.ifEmpty { if (MineApp.mineInfo.sex == 0) "vip_ad_3_logo_male" else "vip_ad_3_logo" }
                    binding.ivVipBg.setBackgroundResource(R.drawable.empty_bg)
                }
                4 -> {
                    binding.title = "立即查看谁喜欢我？"
                    binding.content = "第一时间查看喜欢你的人！立即匹配哦～"
                    binding.myHead = MineApp.mineInfo.image.replace("YM0000", "240X240")
                    binding.otherHead = "empty_icon"
                    binding.ivVipBg.setBackgroundResource(if (MineApp.mineInfo.sex == 0) R.mipmap.vip_ad_4_male else R.mipmap.vip_ad_4)
                }
                5 -> {
                    binding.title = "位置漫游"
                    binding.content = "让你随时随地认识全世界的朋友！"
                    binding.myHead = MineApp.mineInfo.image.replace("YM0000", "240X240")
                    binding.otherHead = "empty_icon"
                    binding.ivVipBg.setBackgroundResource(if (MineApp.mineInfo.sex == 0) R.mipmap.vip_ad_5_male else R.mipmap.vip_ad_5)
                }
                6 -> {
                    binding.title = "无限次数喜欢"
                    binding.content = "左滑喜欢不限次数，不要错过每个机会"
                    binding.myHead = "empty_icon"
                    binding.otherHead = "empty_icon"
                    binding.ivVipBg.setBackgroundResource(if (MineApp.mineInfo.sex == 0) R.mipmap.vip_ad_6_male else R.mipmap.vip_ad_6)
                }
                7 -> {
                    binding.title = "立即匹配闪聊"
                    binding.content = "随机匹配，无需互相喜欢，直接在线闪聊"
                    binding.myHead = MineApp.mineInfo.image.replace("YM0000", "240X240")
                    binding.otherHead = "empty_icon"
                    binding.ivVipBg.setBackgroundResource(R.drawable.empty_bg)
                }
                8 -> {
                    binding.title = "查看所有看过我的人"
                    binding.content = "让偷偷关注着你的ta，无处可藏！"
                    binding.myHead = MineApp.mineInfo.image.replace("YM0000", "240X240")
                    binding.otherHead = "empty_icon"
                    binding.ivVipBg.setBackgroundResource(if (MineApp.mineInfo.sex == 0) R.mipmap.vip_ad_4_male else R.mipmap.vip_ad_4)
                }
            }
        }
        return binding.root
    }
}