package com.shine.amodel

data class Attachmentinfo(
        var id: Int = 0,
        var mailId: Int = 0,
        var gId: Int = 0,
        var count: Int = 0

) {
    override fun toString(): String {
        return "id:${id},gId:${gId},count:${count}"
    }
}