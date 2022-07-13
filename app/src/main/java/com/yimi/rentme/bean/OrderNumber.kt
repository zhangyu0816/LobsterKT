package com.yimi.rentme.bean

class OrderNumber {
    var orderNumber = "" //订单号
    var orderTitle = ""
    var orderStatus = 0// 10 代付款  200 支付成功. TranStatusType
    var productPrice = 0.0//产品单价
    var productCount = 0//数量
    var totalMoney = 0.0//订单总金额
    var createTime = ""
    var isPayed = 0//是否已支付  0未支付  调用支付  1.已支付-提示下单成功
    var number = ""
}