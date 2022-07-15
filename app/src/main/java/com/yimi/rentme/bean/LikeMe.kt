package com.yimi.rentme.bean

class LikeMe {
    var userId = 0L//操作人id
    var otherUserId = 0L//被喜欢的操作id
    var likeOtherStatus = 0// 0 不喜欢  1 喜欢  2.超级喜欢
    var nick = "" //昵称
    var headImage = "" // 头像
    var idAttest = 0//实名认证 0未认证  1认证
    var modifyTime = ""
    var pairTime = ""
}