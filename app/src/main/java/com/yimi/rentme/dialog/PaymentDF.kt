package com.yimi.rentme.dialog

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.yimi.rentme.ApiService
import com.yimi.rentme.R
import com.yimi.rentme.bean.OrderTran
import com.yimi.rentme.databinding.DfPaymentBinding
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.BaseDialogFragment
import com.zb.baselibs.http.MainDataSource
import com.zb.baselibs.utils.aliPay
import com.zb.baselibs.utils.wxPay

class PaymentDF(activity: AppCompatActivity) : BaseDialogFragment(activity) {

    private lateinit var binding: DfPaymentBinding
    private lateinit var orderTran: OrderTran
    private var payType = 0 // 1 开通VIP  2 充值
    private lateinit var mainDataSource: MainDataSource<ApiService>

    override val layoutId: Int
        get() = R.layout.df_payment

    fun setOrderTran(orderTran: OrderTran):PaymentDF{
        this.orderTran = orderTran
        return this
    }

    fun setPayType(payType: Int): PaymentDF {
        this.payType = payType
        return this
    }

    fun setMainDataSource(mainDataSource: MainDataSource<ApiService>): PaymentDF {
        this.mainDataSource = mainDataSource
        return this
    }

    override fun setDataBinding(viewDataBinding: ViewDataBinding?) {
        binding = viewDataBinding as DfPaymentBinding
    }

    fun show(manager: FragmentManager) {
        show(manager, "${BaseApp.projectName}_PaymentDF")
    }

    override fun onStart() {
        super.onStart()
        center(0.8)
    }

    override fun initUI() {
        binding.dialog = this
    }

    fun selectIndex(index: Int) {
        when (index) {
            1 -> mainDataSource.enqueueLoading(
                { alipayFastPayTran(orderTran.tranOrderId) }, "调起支付宝支付..."
            ) {
                onSuccess {
                    aliPay(activity, it)
                    dismiss()
                }
            }
            2 -> mainDataSource.enqueueLoading(
                { wxpayAppPayTran(orderTran.tranOrderId) }, "调起微信支付..."
            ) {
                onSuccess {
                    wxPay(it)
                    dismiss()
                }
            }
        }
    }
}