package com.yimi.rentme.activity.bottle

import com.yimi.rentme.R
import com.yimi.rentme.bean.SelectImage
import com.yimi.rentme.databinding.AcBaseChatBinding
import com.yimi.rentme.vm.BaseChatViewModel
import com.zb.baselibs.activity.BaseScreenActivity
import com.zb.baselibs.mimc.CustomMessageBody
import org.simple.eventbus.Subscriber

class BottleChatActivity : BaseScreenActivity() {

    private val viewModel by getViewModel(BaseChatViewModel::class.java) {
        binding = mBinding as AcBaseChatBinding
        activity = this@BottleChatActivity
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_base_chat
    }

    override fun initView() {
        fitComprehensiveScreen()
        needEvenBus = true
        val extras = intent.extras
        if (extras != null) {
            viewModel.isNotice = extras.getBoolean("isNotice")
            viewModel.msgChannelType = extras.getInt("msgChannelType")
            viewModel.driftBottleId = extras.getLong("driftBottleId")
        }
        viewModel.initViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }
    /**
     * 更新是否显示毛玻璃
     */
    @Subscriber(tag = "lobsterUpdateBtn")
    private fun lobsterUpdateBtn(data: String) {
        viewModel.updateBlur()
    }

    /**
     * 上传图片
     */
    @Subscriber(tag = "lobsterUploadImageList")
    private fun lobsterUploadImageList(data: ArrayList<SelectImage>) {
        viewModel.uploadImageList(data)
    }
    /**
     * 上传图片
     */
    @Subscriber(tag = "lobsterUpdateChat")
    private fun lobsterUpdateChat(data: CustomMessageBody) {
        viewModel.updateChat(data)
    }
}