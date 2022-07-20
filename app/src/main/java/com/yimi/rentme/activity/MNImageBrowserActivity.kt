package com.yimi.rentme.activity

import android.content.Intent
import android.view.KeyEvent
import com.umeng.socialize.UMShareAPI
import com.yimi.rentme.R
import com.yimi.rentme.databinding.AcMnimageBrowserBinding
import com.yimi.rentme.vm.MNImageBrowserViewModel
import com.zb.baselibs.activity.BaseScreenActivity
import kotlinx.android.synthetic.main.ac_mnimage_browser.*
import org.simple.eventbus.Subscriber

class MNImageBrowserActivity : BaseScreenActivity() {

    val viewModel by getViewModel(MNImageBrowserViewModel::class.java) {
        activity = this@MNImageBrowserActivity
        binding = mBinding as AcMnimageBrowserBinding
        binding.viewModel = this
    }

    override fun getRes(): Int {
        return R.layout.ac_mnimage_browser
    }

    override fun initView() {
        needEvenBus = true
        viewModel.initViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    /**
     * 分享重写
     */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 关注
     */
    @Subscriber(tag = "lobsterUpdateFollow")
    private fun lobsterUpdateFollow(data: Boolean) {
        viewModel.updateFollow(data)
    }

    /**
     * 点赞
     */
    @Subscriber(tag = "lobsterDoLike")
    private fun lobsterDoLike(data: String) {
        viewModel.doLike(data.toLong())
    }

    /**
     * 取消点赞
     */
    @Subscriber(tag = "lobsterCancelLike")
    private fun lobsterCancelLike(data: String) {
        viewModel.cancelLike(data.toLong())
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            viewModel.back(iv_back)
            return true
        }
        return false
    }
}