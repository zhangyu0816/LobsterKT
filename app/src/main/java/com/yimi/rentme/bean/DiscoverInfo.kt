package com.yimi.rentme.bean

class DiscoverInfo {
    var friendDynId = 0L
    var otherUserId = 0L// otherUserId
    var userId = 0L// otherUserId
    var nick = "" // 昵称 艺名//1-16
    var image = "" // 头像
    var text = ""
    var images = ""
    var imageSize = 0 // 图片数量
    var dycType =
        0 // 未知 0文字 1图片 2 图文 3 视频 4 视频_文字 5 视频_图片 6 视频_图片_文字 7  问题 视频_图片 22  问题 视频_图片_文字 23
    var videoUrl = "" // 视频地址
    var createTime = "2020-04-16 09:41:00"
    var isDoGood = 0 // 是否已经点赞//1已经点赞 0未点赞
    var pageviews = 0 // 查看数
    var goodNum = 0 // 赞的数量
    var reviews = 0 // 评论数
    var rewardNum = 0 //打赏数量
    var resTime = 0
    var privateRedPageNum = 0 //私密红包数量
    var friendTitle = ""
    var addressInfo = ""
    var videoPath = ""
    var width = 0
    var height = 0
    var headImage = ""
    var sex = 0
    var constellation = 0
    var birthday = ""
    var age = 0
    var isLike = false // 是否点赞
    var isMine = false
    var reviewList = ArrayList<Review>()
}