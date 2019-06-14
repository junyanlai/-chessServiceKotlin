package com.shine.amodel


data class Friend(
        var uid: Int = 0,
        var fid: Int = 0,
        var times: String = "",
        var status: Int = 0,    //0 单项好友 1 双向好友
        var aid: Int = 0
)