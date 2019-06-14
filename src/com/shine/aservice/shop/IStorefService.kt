package com.shine.aservice.shop

interface IStorefService {

    fun DateGetAll(uid:Int):MutableMap<String,String>

    fun dateApp(map:MutableMap<String,String>):Int        //uid,[gid,time]...

    fun dateSub(map:MutableMap<String,String>):Int        //uid,[gid,time]...

    fun dateGetOne(map: MutableMap<String, String>):String   //uid,gid

    fun dateClear(map: MutableMap<String, String>):Int    //uid,[gid,time]...

}