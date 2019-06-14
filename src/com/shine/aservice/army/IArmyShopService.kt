package com.shine.aservice.army

import com.shine.agent.Agent
import com.shine.amodel.Goods

interface IArmyShopService {
    //查询军团商店信息
    fun selectShop():List<Goods>

    fun shopBuy(agent: Agent, commodityId:Int)

}