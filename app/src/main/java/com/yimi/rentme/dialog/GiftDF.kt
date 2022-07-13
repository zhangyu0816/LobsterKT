package com.yimi.rentme.dialog

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.yimi.rentme.ApiService
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.GiftInfo
import com.yimi.rentme.databinding.DfGiftBinding
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.BaseDialogFragment
import com.zb.baselibs.http.MainDataSource
import com.zb.baselibs.utils.SCToastUtil

class GiftDF(activity: AppCompatActivity) : BaseDialogFragment(activity) {

    private lateinit var binding: DfGiftBinding
    private lateinit var mainDataSource: MainDataSource<ApiService>
    lateinit var adapter: BaseAdapter<GiftInfo>
    private lateinit var callBack: CallBack
    private var mPosition = -1

    override val layoutId: Int
        get() = R.layout.df_gift

    fun setMainDataSource(mainDataSource: MainDataSource<ApiService>): GiftDF {
        this.mainDataSource = mainDataSource
        return this
    }

    fun setCallBack(callBack: CallBack): GiftDF {
        this.callBack = callBack
        return this
    }

    override fun setDataBinding(viewDataBinding: ViewDataBinding?) {
        binding = viewDataBinding as DfGiftBinding
    }

    fun show(manager: FragmentManager) {
        show(manager, "${BaseApp.projectName}_GiftDF")
    }

    override fun initUI() {
        binding.dialog = this
        binding.walletInfo = MineApp.walletInfo
        adapter = BaseAdapter(activity, R.layout.item_gift_list, MineApp.giftInfoList, this)
    }

    fun selectIndex(position: Int) {
        adapter.setSelectIndex(position)
        if (mPosition != -1)
            adapter.notifyItemChanged(mPosition)
        adapter.notifyItemChanged(position)
        mPosition = position
    }

    fun payGift(giftInfo: GiftInfo) {
        if (MineApp.walletInfo.wallet < giftInfo.payMoney) {
            SCToastUtil.showToast(activity, "钱包余额不足，请先充值", 2)
            return
        }
        callBack.sure(giftInfo)
        dismiss()
    }

    fun recharge(view: View) {
        RechargeDF(activity).setMainDataSource(mainDataSource).show(activity.supportFragmentManager)
        dismiss()
    }

    fun cancel(view: View) {
        dismiss()
    }

    interface CallBack {
        fun sure(giftInfo: GiftInfo)
    }
}