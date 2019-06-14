package com.shine.aservice.shop

import com.shine.amodel.Goods

interface IGoodsService {


    //查询商城商品
    fun getGoodsByCommon():List<Goods>

    //查询军团商品
    fun getGoodsByArmy():List<Goods>

    fun getGoodsByType(typeCode:Int):List<Goods>

    fun goodsSelectOne(commodityId:Int): Goods
    fun goodsSelectOneByGoods(goods:Goods): Goods


    fun  getGoods():List<Goods>
   //修改 暂行办法
    fun insertGoods( goods:Goods)
    fun updateGoods( goods:Goods)
    fun deleteGoods( goods:Goods)
//根据军团id获取军团商店商品
    fun getArmyShopGoods(armyId:Int): MutableList<Goods>
}