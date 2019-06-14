package com.shine.amodel

class Gamble(var rid: Int = 0,                     //房间ID
             var total: Long = 0L,                 //总场次数
             var win: Long = 0L,                   //赢得场次数
             var rate: Double = 0.0,               //赢得概率
             var type: Int = 0,                    //游戏类型 1：超八 2：老虎机 3：玛丽 4：小丑斯洛
             var occupy: Int = 0,                  //默认为0：未被使用  1：正在被使用
             var bb: Int = 0,                      //bb次数 -小丑斯洛需要得特殊属性
             var rr: Int = 0,                      //rr次数 -小丑斯洛需要得特殊属性
             var banker: Int = 0)                  //连转次数 -小丑斯洛需要得特殊属性
