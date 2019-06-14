package com.shine.aservice.ranking


import com.shine.Config
import com.shine.agent.SSF
import com.shine.amodel.userRanking
import com.shine.dao.ArmyAdminDao
import com.shine.dao.UserDao
import org.json.JSONArray
import org.json.JSONObject
import redis.clients.jedis.Jedis
import java.io.File
import java.nio.charset.Charset
import java.rmi.server.UID
import java.util.*
import kotlin.collections.ArrayList

object RankingService : IRankingService {
    val jedis = SSF.jedis
    // 删除指定的key
    fun flushDB(key: String) {
        if (jedis.exists(key)) jedis.del(key)
    }

    override fun glamour(): JSONArray {
        flushDB("glamour")
        var list = UserDao().queryByAll()

        Testing(list)       //检测是否有30条数据，如果没有则添加
        println("魅力值更新的大小" + list.size)
        for ((i, v) in list.withIndex()) {
            var map = HashMap<String, Double>()
            map.put(v.uid.toString(), (v.expFashion ?: 0).toDouble())
            jedis.zadd("glamour", map)
        }
        var array = JSONArray()
        val gold = jedis.zrevrange("glamour", 0, 29)
        for ((i, v) in gold.withIndex()) {
            var json = JSONObject()
            val u = list.filter { it.uid == v.toInt() }[0]
            json.put("avatar", u.avatar)
            json.put("nick", u.nick)
            json.put("score", jedis.zscore("glamour", v))
            array.put(json)
        }
        return array
    }

    override fun gold(): JSONArray {
        flushDB("gold")
        var list = UserDao().queryByAll()

        Testing(list)       //检测是否有30条数据，如果没有则添加

        for ((i, v) in list.withIndex()) {
            var map = HashMap<String, Double>()
            map.put(v.uid.toString(), v.coin.toDouble())
            jedis.zadd("gold", map)
        }

        var array = JSONArray()
        val gold = jedis.zrevrange("gold", 0, 29)
        for ((i, v) in gold.withIndex()) {
            var json = JSONObject()
            val u = list.filter { it.uid == v.toInt() }[0]
            json.put("avatar", u.avatar)
            json.put("nick", u.nick)
            json.put("score", jedis.zscore("gold", v))
            array.put(json)
        }

        return array
    }

    override fun armyGroup(): JSONArray {
        flushDB("army")
        val list = ArmyAdminDao().armyExp()
        for ((i, v) in list.withIndex()) {
            var map = HashMap<String, Double>()
            map.put(v.armyId.toString(), v.numberExp.toDouble())
            jedis.zadd("army", map)
        }
        var array = JSONArray()
        val gold = jedis.zrevrange("army", 0, 29)
        for ((i, v) in gold.withIndex()) {
            var json = JSONObject()
            val n = ArmyAdminDao().armyByName(v.toInt())
            if (n == null) continue
            json.put("name", n.name)
            json.put("icon", n.icon)
            json.put("score", jedis.zscore("army", v))
            array.put(json)
        }

        return array
    }

    override fun Testing(list: ArrayList<userRanking>) {
        val listRobotName = File("./src/nickname.csv").readLines(Charset.forName("UTF-8"))
        if (list.size < 30) {
            for (i in 0..(30 - list.size)) {
                val avatar = Random().nextInt(10).toLong()
                list.add(userRanking(Random().nextInt(100), avatar.toString(), listRobotName[Random().nextInt(listRobotName.size)], avatar, Random().nextInt(10000)))
            }
        }
    }
}