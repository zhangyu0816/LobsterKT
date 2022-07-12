package com.yimi.rentme.bean

class Review {
    var reviewId = 0L// 评论id
    var userId = 0L// 评论者id  or  点赞
    var nick = "" // 昵称  or  点赞
    var image = "" // 头像  or  点赞
    var text = "" // 评论的消息

    // 被at 人的信息
    var atUserId = 0L
    var atUserNick = ""
    var atUserImage = ""
    var createTime = "" // or  点赞
    var type = 0
    var mainId = 0L // 动态主人
    var forReviewId = 0L
}