package com.shine.aservice.shop

import com.shine.amodel.ArmyBuild
import com.shine.amodel.Goods
import com.shine.aservice.army.ArmyBuildService
import com.shine.dao.GoodsDao
import java.util.*

object GoodsService:IGoodsService {

    override fun insertGoods(goods: Goods) {
        GoodsDao().insertGoods(goods)
    }
    override fun getGoods(): List<Goods> {
      return  GoodsDao().getGoods()
    }

    override fun updateGoods(goods: Goods) {
        GoodsDao().updateGoods(goods)
    }

    override fun deleteGoods(goods: Goods) {
        GoodsDao().deleteGoods(goods)
    }
    override fun getGoodsByCommon(): List<Goods> {
        var r=GoodsDao().getGoodsByCommon()
        return r
    }

    override fun getGoodsByArmy(): List<Goods> {
        return GoodsDao().getGoodsByArmy()
    }

    override fun getGoodsByType(typeCode: Int): List<Goods> {
       return GoodsDao().getGoodsByType(typeCode)
    }

    override fun goodsSelectOne(commodityId:Int): Goods {
        return GoodsDao().goodsSelectOne(commodityId)
    }
    override fun goodsSelectOneByGoods(goods:Goods): Goods {
        return GoodsDao().goodsSelectOneByGoods(goods)
    }

    override fun getArmyShopGoods(armyId:Int): MutableList<Goods> {

        var build = ArmyBuildService.selectArmyBuildOne(ArmyBuild(armyId = armyId, type = "shop"))//
        var goods = GoodsService.getGoodsByArmy()
        if (build.count > goods.size) {
            build.count = goods.size
        }
        var newList = mutableListOf<Goods>()
        var indexList = mutableListOf<Int>()
        while (true) {
            var index = Random().nextInt(goods.size)
            if (index !in indexList) {
                indexList.add(index)
                newList.add(goods.get(index))
            }
            if (indexList.size == build.count) {
                println(indexList)
                indexList.clear()
                break
            }
        }
     return newList
    }
}