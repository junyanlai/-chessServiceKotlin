package com.shine.dao

import com.shine.agent.SSF.getSessionFactory
import com.shine.amodel.Store

class StoreDao : StoreMapper {

    val session = getSessionFactory().openSession()

    val storeMapper = session.getMapper(StoreMapper::class.java)

    override fun updateStroeState(store: Store): Int {
        val r = storeMapper.updateStroeState(store)
        session.commit()
        session.close()
        return r
    }

    override fun userAdd(uid: Int): Int {
        val r = storeMapper.userAdd(uid)
        session.commit()
        session.close()
        return r
    }


    override fun storeGetByState(store: Store): List<Store> {
        val list = storeMapper.storeGetByState(store)
        session.close()
        return list
    }

    override fun goodsAdd(store: Store): Int {
        val r=storeMapper.goodsAdd(store)
        session.commit()
        session.close()
        return r
    }

    override fun storeGetOne(store: Store): Store {
        val obj=storeMapper.storeGetOne(store)
        session.close()
        return obj
    }

    override fun storeGet(uid: Int): List<Store> {
        val list=storeMapper.storeGet(uid)
        session.close()
        return list
    }

    override fun updateGoodsCount(store: Store): Int {
        val r=storeMapper.updateGoodsCount(store)
        session.commit()
        session.close()
        return r
    }

    override fun deleteStore(store: Store) {
        val r=storeMapper.deleteStore(store)
        session.commit()
        session.close()
        return r
    }

    override fun selectStoreCount(store: Store): Int {
        val c=storeMapper.selectStoreCount(store)
        session.close()
        return c
    }
}