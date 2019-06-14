package com.shine.dao

import com.shine.agent.SSF.getSessionFactory
import com.shine.amodel.Goods

class GoodsDao : GoodsMapper {
    val session = getSessionFactory().openSession()
    val goodsMapper = session.getMapper(GoodsMapper::class.java)

    override fun getGoodsByCommon(): List<Goods> {
        val list = goodsMapper.getGoodsByCommon()
        session.close()
        return list
    }

    override fun getGoodsByArmy(): List<Goods> {
        val list = goodsMapper.getGoodsByArmy()
        session.close()
        return list
    }

    override fun getGoodsByType(typeCode: Int): List<Goods> {
        val list = goodsMapper.getGoodsByType(typeCode)
        session.close()
        return list
    }

    override fun goodsSelectOneByGoods(goods: Goods): Goods {
        val goods = goodsMapper.goodsSelectOneByGoods(goods)
        session.close()
        return goods
    }

    override fun goodsSelectOne(commodityId: Int): Goods {
        val goods = goodsMapper.goodsSelectOne(commodityId)
        session.close()
        return goods
    }

    override fun getGoods(): List<Goods> {
        val list = goodsMapper.getGoods()
        session.close()
        return list
    }

    override fun insertGoods(goods: Goods) {
        val r = goodsMapper.insertGoods(goods)
        session.commit()
        session.close()
        return r
    }

    override fun updateGoods(goods: Goods) {
        val r = goodsMapper.updateGoods(goods)
        session.commit()
        session.close()
        return r
    }

    override fun deleteGoods(goods: Goods) {
        val r = goodsMapper.deleteGoods(goods)
        session.commit()
        session.close()
        return r
    }
}