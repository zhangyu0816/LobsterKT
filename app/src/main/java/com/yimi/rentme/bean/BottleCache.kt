package com.yimi.rentme.bean

class BottleCache {
    var driftBottleId = 0L //漂流瓶id
    var userId = 0L//好友userId
    var nick = "" //好友昵称
    var image = "" //好友头像
    var creationDate = "" //会话时间
    var stanza = "" //内容
    var msgType = 0//消息类型 1：文字 2：图片 3：语音 4：视频
    var noReadNum = 0//未读条数
    var publicTag = "" //标签   默认 null
    var effectType = 0//作用类型 1.无  2.系统内置      默认 null
    var authType = 0//权限类型  1.无限制  2.以下限制      默认 null
    var mainUserId = 0L
}