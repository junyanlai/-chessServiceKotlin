package com.shine.controller.poker.cdd

/**
 *  Create by Colin
 *  Date:2018/6/11.
 *  Time:11:10
 */
enum class cardTypeCdd(val order:Int){

    ERROR(0),
    SINGLE(1),
    DOUNLE(2),
    THREE(3),
    FOUR(12),
    FIVE(4),        //shunzi
    HULU(5),        //hulu
    FOUR1(6),       //tiezhi
    FIVESC(7),      //tonghuashun
    FOUR2(8),       //2tiezhi
    WUHU(9),        //wuhujiang
    WUHU2(10),      //2wuhujiang
    DRAGON(11),     //yitiaolong
    TONGHUA(13)     //同花

}