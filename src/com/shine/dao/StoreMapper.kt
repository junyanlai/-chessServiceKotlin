package com.shine.dao

import com.shine.amodel.Store
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Update

@Mapper
interface StoreMapper {

    @Insert("""
        INSERT into store(uid) VALUES (#{UID})
    """)
    fun userAdd(uid: Int): Int

    fun storeGetByState(store: Store): List<Store>

    fun goodsAdd(store: Store): Int

    fun storeGetOne(store: Store): Store

    fun storeGet(uid: Int): List<Store>

    fun updateGoodsCount(store: Store): Int

    @Update("""
        update store set priority=0,goodsState=0  where  uid=#{uid} and priority=#{priority}
    """)
    fun updateStroeState(store: Store): Int

    fun deleteStore(store: Store): Unit

    fun selectStoreCount(store: Store): Int

}