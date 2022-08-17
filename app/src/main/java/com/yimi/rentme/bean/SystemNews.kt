package com.yimi.rentme.bean

import com.zb.baselibs.utils.getLong

class SystemNews {
    var id = 0L
    var msgType = 0//消息类型 1：文字 2：图片 3：语音 4：视频 5:地图坐标
    var stanza = "" //消息内容
    var title = "" //标题
    var resLink = "" //资源链接
    var resTime = 0//资源时长  秒
    var creationDate = ""
    var noReadNum = 0//未读条数
    var showTime = false
    var mainUserId = getLong("userId")
}