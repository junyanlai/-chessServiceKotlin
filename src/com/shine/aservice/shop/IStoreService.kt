package com.shine.aservice.shop

import com.shine.agent.Agent
import com.shine.amodel.Goods
import com.shine.amodel.Prize
import com.shine.amodel.Store
import org.json.JSONObject

interface IStoreService {

/*
    fun storeGet(uid:Int):MutableMap<String,Int>

    fun storeAdd(map:MutableMap<String,Int>):Int        //uid,[gid,order]...

    fun storeSub(map:MutableMap<String,Int>):Int        //uid,[gid,order]...

    fun storeNum(map: MutableMap<String, Int>):Int      //uid,[gid]...

    fun storeClear(map: MutableMap<String, Int>):Int    //uid,[gid]...
*/

    fun goodsAdd(store: Store):Int        //uid,[gid,order]...

    //获取背包信息
    fun storeGet(uid:Int): List<Store>

    //    fun storeGets(uid: Int): List<Map<String,Any>>
//获取背包商品数量
    fun selectStoreCount(store: Store):Int

    //获取背包单一商品信息
    fun storeGetOne(store: Store): Store

    //修改背包商品数量
    fun updateGoodsCount(store: Store):Int

    fun deleteStore(store: Store): Unit

    //存入物品
    fun Buy_Into_Backpacker(agent: Agent, goods: Goods, data: JSONObject,prize: Prize?)
    //重写
    fun Buy_Into_Backpacker(agent: Agent,root:JSONObject)
    //使用
    fun Buy_Out_Backpacker(agent: Agent, data : JSONObject): Boolean
    //出售商品
    fun Buy_Out_Backpacker(agent: Agent, data : JSONObject, handle:String): Boolean

    //获得奖品
    fun Get_Prize(agent: Agent, data : JSONObject,atype:String)


//根据用id 状态查询 数据
    fun storeGetByState(store: Store):List<Store>

    fun updateStroeState(store: Store): Int
}