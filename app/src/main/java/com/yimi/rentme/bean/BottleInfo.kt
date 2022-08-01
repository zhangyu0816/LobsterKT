package com.yimi.rentme.bean

import java.io.Serializable

class BottleInfo : Serializable {
    var driftBottleId = 0L //漂流瓶id
    var userId = 0L
    var text = "" //内容
    var driftBottleType = 0//漂流瓶状态 .1.漂流中  2.被拾起  3.销毁
    var noReadNum = 0//未读数量
    var otherHeadImage = "" //头像
    var otherNick = "" //昵称
    var otherUserId = 0L //
    var modifyTime = ""
    var createTime = ""
    var destroyType = 0 // 0未销毁  1 单方销毁 所属人  2 单方销毁 拾起人  3 双方销毁

}