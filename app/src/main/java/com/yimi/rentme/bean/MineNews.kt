package com.yimi.rentme.bean

class MineNews {
    var reviewMsgId = 0L //消息id

    //动态的信息
    var friendDynamicId = 0L
    var friendDynamicText = ""
    var friendDynamicImages = ""
    var friendDynamicImageSize = 0
    var friendDynamicVideoUrl = ""
    var friendDynamicTitle = ""
    var friendDynamicDycType = 0

    //    未知   0
    //    文字   1
    //    图片   2
    //    图文   3
    //    视频   4
    //    视频_文字  5
    //    视频_图片  6
    //    视频_图片_文字   7
    var reviewUserId = 0L//评论者的UserId
    var reviewUserNick = ""
    var reviewUserImage = ""
    var reviewType = 0//1评论  2.点赞 3.礼物
    var text = "" //评论的消息
    var createTime = "" //消息时间
    var friendDynamicGiftId = 0L//赠礼记录id
    var giftNumber = 0L//数量
    var giftImage = "" //礼物图片
    var giftName = "" //礼物图片

}