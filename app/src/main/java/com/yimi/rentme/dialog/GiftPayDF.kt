package com.yimi.rentme.dialog

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.yimi.rentme.ApiService
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.bean.GiftInfo
import com.yimi.rentme.databinding.DfGiftPayBinding
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.BaseDialogFragment
import com.zb.baselibs.http.MainDataSource
import com.zb.baselibs.utils.SCToastUtil
import org.simple.eventbus.EventBus

class GiftPayDF(activity: AppCompatActivity) : BaseDialogFragment(activity) {

    private lateinit var binding: DfGiftPayBinding
    private lateinit var giftInfo: GiftInfo
    private var friendDynId = 0L
    private var otherUserId = 0L
    private lateinit var mainDataSource: MainDataSource<ApiService>
    private lateinit var callBack: CallBack

    override val layoutId: Int
        get() = R.layout.df_gift_pay

    fun setGiftInfo(giftInfo: GiftInfo): GiftPayDF {
        this.giftInfo = giftInfo
        return this
    }

    fun setFriendDynId(friendDynId: Long): GiftPayDF {
        this.friendDynId = friendDynId
        return this
    }

    fun setOtherUserId(otherUserId: Long): GiftPayDF {
        this.otherUserId = otherUserId
        return this
    }

    fun setMainDataSource(mainDataSource: MainDataSource<ApiService>): GiftPayDF {
        this.mainDataSource = mainDataSource
        return this
    }

    fun setCallBack(callBack: CallBack): GiftPayDF {
        this.callBack = callBack
        return this
    }

    override fun setDataBinding(viewDataBinding: ViewDataBinding?) {
        binding = viewDataBinding as DfGiftPayBinding
    }

    fun show(manager: FragmentManager) {
        show(manager, "${BaseApp.projectName}_GiftPayDF")
    }

    override fun initUI() {
        binding.dialog = this
        binding.content = ""
    }

    fun sure(view: View) {
        if (binding.content!!.isEmpty() || binding.content!!.toInt() == 0) {
            SCToastUtil.showToast(activity, "请输入赠送数量", 2)
            return
        }
        val money: Double = binding.content!!.toInt() * giftInfo.payMoney
        if (money > MineApp.walletInfo.wallet) {
            SCToastUtil.showToast(activity, "钱包余额不足，请先充值", 2)
            return
        }
        if (friendDynId != 0L) submitOrder() else submitUserOrder()
    }

    /**
     * 动态礼物打赏下单
     */
    private fun submitOrder() {
        mainDataSource.enqueue({
            submitOrder(friendDynId, giftInfo.giftId, binding.content!!.toInt())
        }) {
            onSuccess {
                if (it.isPayed == 0)
                    walletPayTran(it.orderNumber)
                else
                    success()
            }
        }
    }

    /**
     * 用户打赏下单
     */
    private fun submitUserOrder() {
        mainDataSource.enqueue({
            submitUserOrder(otherUserId, giftInfo.giftId, binding.content!!.toInt())
        }) {
            onSuccess {
                if (it.isPayed == 0)
                    walletPayTran(it.orderNumber)
                else
                    success()
            }
        }
    }

    /**
     * 钱包支付
     */
    private fun walletPayTran(tranOrderId: String) {
        mainDataSource.enqueue({ walletPayTran(tranOrderId) }) {
            onSuccess {
                success()
            }
        }
    }

    /**
     * 支付成功
     */
    private fun success() {
        EventBus.getDefault().post("", "lobsterUpdateWallet")
        callBack.sure(binding.content!!.toInt())
        dismiss()
    }

    interface CallBack {
        fun sure(giftNum: Int)
    }
}