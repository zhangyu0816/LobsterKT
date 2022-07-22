package com.yimi.rentme.vm

import android.view.View
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.bean.VipInfo
import com.yimi.rentme.databinding.AcOpenVipBinding
import com.yimi.rentme.dialog.VipAdDF

class OpenVipViewModel : BaseViewModel() {

    lateinit var binding: AcOpenVipBinding
    private lateinit var vipInfo: VipInfo

    override fun initViewModel() {
        binding.title = "我的权益"
        vipInfo = MineApp.vipInfoList[0]
        goAnimator(binding.tvOpen, 0.85f, 1f, 800L)
        setBtn()
    }

    override fun back(view: View) {
        super.back(view)
        stopGo()
        activity.finish()
    }

    fun getVip(view: View) {
        VipAdDF(activity).setMainDataSource(mainDataSource).setType(0)
            .show(activity.supportFragmentManager)
    }

    fun setBtn() {
        binding.mineInfo = MineApp.mineInfo
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
}