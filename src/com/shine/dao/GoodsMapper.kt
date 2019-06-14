package com.shine.dao

import com.shine.amodel.Goods
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import java.util.*

@Mapper
interface GoodsMapper {


    //查询商城商品
    fun getGoodsByCommon():List<Goods>
    //查询军团商品
    fun getGoodsByArmy():List<Goods>
    fun getGoodsByType(typeCode:Int):List<Goods>
    fun goodsSelectOneByGoods(goods:Goods): Goods
    fun goodsSelectOne(commodityId:Int): Goods


    fun  getGoods():List<Goods>
    fun insertGoods( goods:Goods)
    fun updateGoods( goods:Goods)
    fun deleteGoods( goods:Goods)
}