package com.yimi.rentme.wxapi

import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.umeng.socialize.weixin.view.WXCallbackActivity

class WXEntryActivity : WXCallbackActivity() {
    override fun onResp(resp: BaseResp) {
        if (resp.type == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
            finish()
        } else {
            super.onResp(resp) //一定要加super，实现我们的方法，否则不能回调   重重重要   关键在于这一句
        }
    }
}
