package com.shine.amodel


data class HangRecord(var id: Int = 0,
                      var rid: Int = 0,              //房间ID
                      var uid: Int = 0,
                      var type: Int = 0,             //挂机游戏类型
                      var money: Long = 0,           //挂机金额
                      var statr: String = "",        //开始时间戳
                      var end: String = "",          //结束时间戳
                      var profit: Long = 0,          //预期收益
                      var time: String = "",         //创建时间
                      var sign: Int = 1)             //记录标志 1：正在使用的记录  2：已经完成的记录

/**
 * 备注：关于sign=2的，起初设计的是把完成的记录更新成2进行伪删除，
 * 但在代码中，是直接删除已经完成的记录，而查询记录的时候是查询 sign=1
 * 如果在数据库中直接更改sign=2，那么这条记录是永远不会被删除掉的，
 * 所以目前sign正常的记录为1，完成之后不再更新为2，而是直接删除记录
 * 防止数据堆积过多
 */
