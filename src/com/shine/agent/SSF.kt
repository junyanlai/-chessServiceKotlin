package com.shine.agent

import com.shine.Config
import com.shine.controller.aHall.Hall
import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import redis.clients.jedis.Jedis

object SSF {

    val resource = Config.mybatis
    var inputStream = Resources.getResourceAsStream(resource)
    var sqlSessionFactory = SqlSessionFactoryBuilder().build(inputStream)
    val jedis = Jedis(Config.hostIp)
    fun getSessionFactory():SqlSessionFactory{
        return sqlSessionFactory
    }

    fun getJedisMethod(agent: Agent):Jedis{
        try {
            var   jedis=Jedis(Config.hostIp)
            return jedis
        }catch (e:Exception){
            println("断开连接。。。。。。。")
            var  jedis=Jedis(Config.hostIp)
            Hall.everyDayClearArmyShop(agent)
            return jedis
        }
    }

}