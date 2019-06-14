package com.shine.aservice.army

import com.shine.agent.Agent
import com.shine.amodel.Goods
import com.shine.aservice.shop.GoodsService


object ArmyShopService: IArmyShopService {

//获取工会商城信息
    override fun selectShop(): List<Goods> {
        return GoodsService.getGoodsByArmy()
    }

    override fun shopBuy(agent: Agent, commodityId: Int) {
    }


}