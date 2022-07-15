package com.yimi.rentme.bean

class RentInfo {
    var userId = 0L
    var price = ""
    var timeScope = "" // 时间范围
    var serviceTags = "" // 服务范围说明
    var serviceScope = "" // 服务范围说明
    var weixinNum = "" //
    var phoneNum = "" // (这就是绑定的手机号码)
    var imgVerifyStatus = 0// 0审核 1通过 -1 未通过
    var hasWeixinNum = 0
}