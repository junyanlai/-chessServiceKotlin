package com.shine.amodel

/**
 * 公告日志
 */
data class RecordLog(var uid: Int = 110,
                     var name: String = "test",
                     var type: String = "jp",      //jp:jp系统产生的日志  gift：发送礼物产生的日志
                     var msg: String = "test")