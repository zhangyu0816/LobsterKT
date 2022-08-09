package com.yimi.rentme.bean

class PairInfo {
    var userId = 0L//用户id  预匹配列表中是对方的id
    var otherUserId = 0L//对方用户的id
    var nick = "" //昵称
    var headImage = "" // 头像
    var moreImages = "" //多图
    var personalitySign = "" //个性签名
    var birthday = ""
    var age = 0//年龄
    var sex = 0//性别  0女  1男
    var idAttest = 0//是否实名认证  0未认证  1认证
    var memberType = 1
    var faceAttest = 0
    var provinceId = 0L//省份id
    var cityId = 0L //地区id
    var districtId = 0L
    var distance = ""
    var singleImage = ""
    var imageList = ArrayList<String>()
    var position = 0
}