package com.yimi.rentme.wxapi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.yimi.rentme.R
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.SCToastUtil

class WXPayEntryActivity : AppCompatActivity(), IWXAPIEventHandler {
    private lateinit var api: IWXAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pay_result)
        api = WXAPIFactory.createWXAPI(this, BaseApp.ymData[1])
        api.handleIntent(intent, this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        api.handleIntent(intent, this)
    }

    override fun onReq(req: BaseReq) {}
    override fun onResp(resp: BaseResp) {
        if (resp.type == ConstantsAPI.COMMAND_PAY_BY_WX) {
            val code = resp.errCode
            val msg: String
            when (code) {
                0 -> {
                    msg = "支付成功！"
                    LocalBroadcastManager.getInstance(BaseApp.context)
                        .sendBroadcast(Intent("lobster_paySuccess"))
                }
                -2 -> msg = "您取消了支付！"
                else -> msg = "支付失败！"
            }
            SCToastUtil.showToast(this, msg, 2)
            finish()
        }
    }
}