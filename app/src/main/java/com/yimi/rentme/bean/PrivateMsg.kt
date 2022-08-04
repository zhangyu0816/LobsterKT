package com.yimi.rentme.bean

class PrivateMsg {
    var id = 0L
    var fromId = 0L // 发送人ID
    var toId = 0L// 接收人ID
    var creationDate = "" // 发送时间
    var stanza = "" // 消息内容
    var msgType = 0// 消息类型 1：文字 2：图片 3：语音 4：视频
    var title = "" // 标题
    var resLink = "" // 资源链接
    var resTime = 0// 资源时长 秒

    // 新增
    var imPlatformType = 0//1.zuwoIM 2.阿里OpenIM 当前使用：2
    var thirdMessageId = "" //第三方消息id
    var isDelete = 0//状态 0：正常 1删除
    var isRead = 0//状态 0：未读 1已读
    var msgChannelType = 1 //消息渠道类型  1.普通聊天 （默认）  2. 漂流瓶
    var driftBottleId = 0L //所属漂流瓶
    var flashTalkId = 0L//所属闪聊

}