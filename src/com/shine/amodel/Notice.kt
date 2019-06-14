package com.shine.amodel

/**
 * 公告
 */
data class Notice(
        var id: Int = 0,
        var gameId: Int = 0,
        var name: String = "",
        var title: String = "",
        var news: String = "",
        var time: String = ""
)