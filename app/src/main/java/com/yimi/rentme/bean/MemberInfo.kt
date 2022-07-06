package com.yimi.rentme.bean

import android.text.Spanned

class MemberInfo {
    var userId = 0L//userId
    var nick = "" //昵称
    var image = "" //头像
    var moreImages = "" // 多图
    var personalitySign = "" //个性签名
    var shopUrl = "" //网店地址
    var birthday = "" //生日
    var age = 0//年龄
    var sex = 0//性别  0女  1男
    var height = 0//身高 CM
    var weight = 0//体重 公斤
    var constellation = 0//星座
    var bloodType = 0 //AB0血型系统 A B O AB 其他
    var job = "" //职业
    var education = 0//学历
    var provinceId = 0L//省份ID
    var cityId = 0L//地区ID
    var districtId = 0L//地区id
    var rstatus = 0//1：好友  2：非好友
    var remark = "" //备注
    var attentionQuantity = 0//关注数量
    var fansQuantity = 0//粉丝数量
    var popularity = 0//人气(浏览量)
    var pollQuantity = 0 //赞数
    var rentQuantity = 0// 订单量（出租多少次 ）
    var beLikeQuantity = 0//被喜欢数
    var memberType = 0//1免费用户   2 .会员
    var serviceTags = ""
    var newDycCreateTime = "" //最新创建时间   3天内的属于新增动态
    var idAttest = 0//实名认证 V  0未认证  1认证
    var faceAttest = 0//人脸 V  0未认证  1认证
    var newDycType = 0// 最新的动态类型       配上对应的文字
    var attentionStatus = 0//关注关系  1 关注  0 未关注
    var distance = ""
    var singleImage = ""
    var title = ""
    var content: Spanned? = null
}