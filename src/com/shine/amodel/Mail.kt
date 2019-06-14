package com.shine.amodel

data class Mail(
        var id: Int = 0,
        var sendId: Int = 105,
        var sendName: String = "系统邮件",
        var receiveId: Int = 0,
        var receiveName: String = "",
        var sendDate: String = "",  //发送日期
        var receiveDate: String = "",  //接收日期
        var expireDate: String = "",    //到期日期
        var message: String = "",
        var status: Int = 0,            //0 :未读 1：已读
        var attachmentinfo: Int = 0,    //附件信息标志 0：未领取，1：已领取 继续扩展
        var mailType: Int = 0          //邮件类型  0：玩家 1：系统
) {
    override fun toString(): String {
        return """id:${id},sendId:${sendId},sendName:${sendName},receiveId:${receiveId},receiveName:${receiveName},sendDate:${sendDate},
            |receiveDate:${receiveDate},expireDate:${expireDate},message:${message},status:${status},attachmentinfo:${attachmentinfo},mailType:${mailType}""".trimMargin()
    }
}