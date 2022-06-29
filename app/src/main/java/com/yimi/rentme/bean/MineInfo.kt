package com.yimi.rentme.bean

class MineInfo {
    var userId = 0L//userId
    var nick = "" //昵称
    var image = "" //头像
    var moreImages = "" // 多图
    var personalitySign = "" //个性签名
    var birthday = "" //生日
    var age = 0//年龄
    var sex = 0//性别  0女  1男
    var height = 0//身高 CM
    var constellation = 0//星座
    var job = "" //职业
    var idAttest = 0//实名认证 V  0未认证  1认证
    var memberType = 0//1免费用户   2 .会员
    var memberExpireTime = "" //会员到期时间
    var provinceId = 0L//省份ID
    var cityId = 0L //城市ID
    var districtId = 0L//地区id
    var serviceTags = ""
    var faceAttest = 0
    var surplusToDayLikeNumber = 0//当日剩余喜欢次数  如果是vip 无视
    var surplusToDaySuperLikeNumber = 0//当日剩余超级喜欢次数  如果是普通用户  无视
    var appType = 0 //205
}