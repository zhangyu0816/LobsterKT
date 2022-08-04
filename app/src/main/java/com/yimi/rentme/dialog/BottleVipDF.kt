package com.yimi.rentme.dialog

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.yimi.rentme.ApiService
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.adapter.BaseAdapter
import com.yimi.rentme.bean.VipInfo
import com.yimi.rentme.databinding.DfBottleVipBinding
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.BaseDialogFragment
import com.zb.baselibs.http.MainDataSource
import com.zb.baselibs.utils.SCToastUtil

class BottleVipDF(activity: AppCompatActivity) : BaseDialogFragment(activity) {

    private lateinit var binding: DfBottleVipBinding
    lateinit var adapter: BaseAdapter<VipInfo>
    private var mPosition = -1
    private lateinit var mainDataSource: MainDataSource<ApiService>

    override val layoutId: Int
        get() = R.layout.df_bottle_vip

    fun setMainDataSource(mainDataSource: MainDataSource<ApiService>): BottleVipDF {
        this.mainDataSource = mainDataSource
        return this
    }

    override fun setDataBinding(viewDataBinding: ViewDataBinding?) {
        binding = viewDataBinding as DfBottleVipBinding
    }

    fun show(manager: FragmentManager) {
        show(manager, "${BaseApp.projectName}_BottleVipDF")
    }

    override fun onStart() {
        super.onStart()
        center(0.9)
    }

    override fun initUI() {
        binding.dialog = this

        adapter = BaseAdapter(activity, R.layout.item_bottle_vip, MineApp.vipInfoList, this)
        mPosition = if (MineApp.vipInfoList.size < 2) {
            MineApp.vipInfoList.size - 1
        } else {
            1
        }
        if (mPosition >= 0) {
            adapter.setSelectIndex(mPosition)
            adapter.notifyItemChanged(mPosition)
        }
    }

    fun selectIndex(position: Int) {
        adapter.setSelectIndex(position)
        if (mPosition != -1)
            adapter.notifyItemChanged(mPosition)
        adapter.notifyItemChanged(position)
        mPosition = position
    }

    fun sure(view: View) {
        if (mPosition == -1) {
            SCToastUtil.showToast(activity, "请选择VIP套餐", 2)
            return
        }
        submitOpenedMemberOrder()
    }

    fun cancel(view: View) {
        dismiss()
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
                dismiss()
            }
        }
    }
}