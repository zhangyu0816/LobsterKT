package com.yimi.rentme.dialog

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.yimi.rentme.ApiService
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.RechargeInfo
import com.yimi.rentme.databinding.DfRechargeBinding
import com.zb.baselibs.activity.WebActivity
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.BaseDialogFragment
import com.zb.baselibs.http.MainDataSource
import com.zb.baselibs.utils.SCToastUtil
import org.jetbrains.anko.startActivity

class RechargeDF(activity: AppCompatActivity) : BaseDialogFragment(activity) {

    private lateinit var binding: DfRechargeBinding
    private lateinit var mainDataSource: MainDataSource<ApiService>
    lateinit var adapter: BaseAdapter<RechargeInfo>
    private var mPosition = -1

    override val layoutId: Int
        get() = R.layout.df_recharge

    fun setMainDataSource(mainDataSource: MainDataSource<ApiService>): RechargeDF {
        this.mainDataSource = mainDataSource
        return this
    }

    override fun setDataBinding(viewDataBinding: ViewDataBinding?) {
        binding = viewDataBinding as DfRechargeBinding
    }

    fun show(manager: FragmentManager) {
        show(manager, "${BaseApp.projectName}_RechargeDF")
    }

    override fun initUI() {
        binding.dialog = this
        binding.walletInfo = MineApp.walletInfo
        adapter = BaseAdapter(activity, R.layout.item_recharge_list, MineApp.rechargeInfoList, this)
    }

    fun selectIndex(position: Int) {
        adapter.setSelectIndex(position)
        if (mPosition != -1)
            adapter.notifyItemChanged(mPosition)
        adapter.notifyItemChanged(position)
        mPosition = position
    }

    fun showRule(view: View) {
        activity.startActivity<WebActivity>(
            Pair("webTitle", "用户充值协议"),
            Pair(
                "webUrl",
                "${BaseApp.baseUrl}mobile/xiagu_recharge_protocol.html"
            )
        )
    }

    fun recharge(view: View) {
        if (mPosition == -1) {
            SCToastUtil.showToast(activity, "请选择充值套餐", 2)
            return
        }
        val rechargeInfo = MineApp.rechargeInfoList[mPosition]
        mainDataSource.enqueueLoading({
            rechargeWallet(rechargeInfo.originalMoney, rechargeInfo.id)
        }, "提交充值订单...") {
            onSuccess {
                PaymentDF(activity).setOrderTran(it).setPayType(2).setMainDataSource(mainDataSource)
                    .show(activity.supportFragmentManager)
                dismiss()
            }
        }
    }

    fun cancel(view: View) {
        dismiss()
    }
}