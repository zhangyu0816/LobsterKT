package com.yimi.rentme.dialog

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.yimi.rentme.R
import com.yimi.rentme.bean.GiftInfo
import com.yimi.rentme.databinding.DfGiveSuccessBinding
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.dialog.BaseDialogFragment

class GiveSuccessDF(activity: AppCompatActivity) : BaseDialogFragment(activity) {

    private lateinit var binding: DfGiveSuccessBinding
    private lateinit var giftInfo: GiftInfo
    private var giftNum = 0

    override val layoutId: Int
        get() = R.layout.df_give_success

    fun setGiftInfo(giftInfo: GiftInfo): GiveSuccessDF {
        this.giftInfo = giftInfo
        return this
    }

    fun setGiftNum(giftNum: Int): GiveSuccessDF {
        this.giftNum = giftNum
        return this
    }

    override fun setDataBinding(viewDataBinding: ViewDataBinding?) {
        binding = viewDataBinding as DfGiveSuccessBinding
    }

    fun show(manager: FragmentManager) {
        show(manager, "${BaseApp.projectName}_GiveSuccessDF")
    }

    override fun onStart() {
        super.onStart()
        center(0.8)
    }

    override fun initUI() {
        binding.dialog = this
        binding.giftInfo = giftInfo
        binding.giftNum = giftNum
    }

    fun sure(view: View) {
        dismiss()
    }
}