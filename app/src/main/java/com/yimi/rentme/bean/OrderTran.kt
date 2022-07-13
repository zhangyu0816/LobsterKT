package com.yimi.rentme.bean

class OrderTran {
    var tranOrderId = "" //交易订单号
    var tranSimpleDesc = "" //订单简单描述
    var tranDesc = "" //订单详细描述
    var totalMoney = 0.0//金额
    var alipayNotifyUrl = "" //支付宝回调接口
    var canUseWallet = 0//是否能够使用钱包支付 //1能使用  0不能
}