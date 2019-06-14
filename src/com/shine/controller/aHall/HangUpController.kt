package com.shine.controller.aHall

import com.alibaba.fastjson.JSON
import com.shine.agent.Agent
import com.shine.amodel.HangRecord
import com.shine.aservice.gamble.GambleService
import com.shine.aservice.user.UserService.getUserMsgByUID
import com.shine.aservice.user.UserService.updateUserCoin
import com.shine.controller.poker.xcsl.ManagerLh
import com.shine.controller.poker.xcsl.ManagerSl
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset
import java.util.*
import kotlin.math.round


object HangUpController {
    val mapRoom = Hall.gMapRoom
    val mapUserRoom = Hall.gMapUserRoom
    val rate = File("./src/Rate.csv").readLines(Charset.forName("UTF-8"))[0]    //收益率

    fun HandleAll(agent: Agent, root: JSONObject) {//command,data,type,id
        if (!root.has("data")) return
        val data = root["data"] as String
        when (data) {
            "state" -> queryState(agent, root)
            "create" -> createRoom(agent, root)
            "delete" -> deleteRoom(agent, root)
            "record" -> recordRoom(agent, root)
            "query" -> queryRecord(agent, root)
            "sort" -> querySort(agent, root)
            else -> return
        }
    }

    fun queryState(agent: Agent, root: JSONObject) {
        val type = root["type"] as Int

        var array = JSONArray()
        GambleService.queryAllRoom(type).forEach {

            if (mapRoom[it.rid] != null) {
                it.occupy = 1
            } else {
                it.occupy = 0
            }

            array.put(JSON.toJSON(it))
        }

        Send(agent, Msg(1, array, "state", "hall_hangup"))
    }

    fun createRoom(agent: Agent, root: JSONObject) {
        val type = root["type"] as Int
        val rid = root["rid"] as Int

        val gamble = GambleService.queryRid(rid)

        if (gamble.occupy == 1) {
            Send(agent, Msg(1, "mark", "create", "hall_hangup"))
            return
        }
        when (type) {
            1 -> ManagerCb.OnCreate(agent, JSONObject().put("rid", rid))
            2 -> ManagerLh.OnCreate(agent, JSONObject().put("rid", rid))
            3 -> ManagerMl.OnCreate(agent, JSONObject().put("rid", rid))
            4 -> ManagerSl.OnCreate(agent, JSONObject().put("rid", rid))
            else -> return
        }
    }

    fun deleteRoom(agent: Agent, root: JSONObject) {
        val rid = root["rid"] as Int

        mapUserRoom.remove(agent.user.uid)
        mapRoom.remove(rid)

        Send(agent, Msg(1, "success", "delete", "hall_hangup"))
        //println("#_________ Room is deleted")
    }

    fun recordRoom(agent: Agent, root: JSONObject) {
        var data = System.currentTimeMillis()
        val rid = root["rid"] as Int
        val type = root["type"] as Int
        val time = root["time"] as Int
        val money = root["money"].toString().toLong()

        if (GambleService.queryUid(agent.user.uid).size > 0) {
            Send(agent, Msg(1, "mark", "record", "hall_hangup"))
            return
        }

        var hangRecord = HangRecord()
        hangRecord.rid = rid
        hangRecord.uid = agent.user.uid
        hangRecord.type = type
        hangRecord.money = money
        hangRecord.statr = data.toString()
        hangRecord.end = (data + time.times(60).times(60).times(1000)).toString()

        if (GambleService.insertHangRecord(hangRecord) > 0) {
            GambleService.updateOccupy(rid, 1)  //更新房间状态-使用
            Send(agent, Msg(1, "success", "record", "hall_hangup"))
        } else {
            Send(agent, Msg(1, "fail", "record", "hall_hangup"))
        }
    }

    fun queryRecord(agent: Agent, root: JSONObject) {

        val hangRecord = GambleService.queryUid(agent.user.uid)
        if (hangRecord.size >= 2) {
            //触发删除操作，只保留一条记录
            for (i in 1 until hangRecord.size) {
                GambleService.deleteHangRecord(hangRecord[i].id)
            }
        }

        //如果存在多条记录是错误的，会无条件被上面清理掉，因此里面只能有一条记录
        var stata = JSONObject()
        if (hangRecord.size == 0) {
            stata.put("type", "-1")
            //记录不存在
            //查询记录不存在
            stata.put("state", 2)
            Send(agent, Msg(stata, "", "query", "hall_hangup"))
        } else {
            //返回开始时间-结束时间-收益金币
            val m = hangRecord[0].end.toLong() - System.currentTimeMillis()
            //println("系统时间>>>${System.currentTimeMillis()}")
            //println("剩余时间>>>${m}")

            val h = m.div(1000).div(60).div(60).times(0.1)    //时间比率
            val profit = hangRecord[0].money.times(h) + hangRecord[0].money.times(rate.toDouble())

            //游戏类型和返回状态
            stata.put("type", hangRecord[0].type)

            if (hangRecord[0].end.toLong() <= Date().time) {
                var json = JSONObject()
                json.put("start", hangRecord[0].statr.toLong())
                json.put("end", hangRecord[0].end.toLong())
                json.put("profit", round(hangRecord[0].money + profit))

                //游戏结束
                stata.put("state", 1)
                Send(agent, Msg(stata, json, "query", "hall_hangup"))

                /**
                 *如果完成离线，就把 sign 更新成成2
                 *GambleService.updateHangRecordSign(hangRecord[0].id)
                 *已经更改逻辑，完成之后不再更新为2
                 */


                //如果完成则更新房间状态-未被使用
                GambleService.updateOccupy(hangRecord[0].rid, 1)
                //更新金币
                val c = getUserMsgByUID(agent.user.uid).coin
                updateUserCoin(c + round(hangRecord[0].money + profit).toLong(), agent.user.uid)
                //发送邮件
                var email = JSONObject()
                email.put("msg", "挂机结束，获得 ${json["profit"]} 金币")
                email.put("remark", "挂机结束邮件")
                EmailController.Handle_sysEmail(email, agent.user.uid)
                //删除已经完成的挂机记录
                GambleService.deleteHangRecord(hangRecord[0].id)
            } else {
                stata.put("type", hangRecord[0].type)
                //返回剩余时间
                //挂机时间未到
                stata.put("state", 3)
                Send(agent, Msg(stata, m, "query", "hall_hangup"))
            }
        }


    }

    /**
     * 每8个小时检测一次挂机记录，清理掉已经完成的记录
     */
    fun hangRecordAndClear() {
        val hangRecord = GambleService.queryOccupyRecord()
        println("查询到的记录：" + hangRecord.size)

        for (i in 0 until hangRecord.size) {
            val m = hangRecord[i].end.toLong() - System.currentTimeMillis()
            val h = m.div(1000).div(60).div(60).times(0.1)    //时间比率
            val profit = hangRecord[i].money.times(h) + hangRecord[i].money.times(rate.toDouble())

            if (hangRecord[i].end.toLong() <= Date().time) {

                /*如果完成离线，就把 sign 更新成2
                 *已经更改逻辑，完成之后不再更新为2
                 *GambleService.updateHangRecordSign(hangRecord[i].id)
                 */

                //如果完成则更新房间状态-未被使用
                GambleService.updateOccupy(hangRecord[i].rid, 1)
                //更新金币
                val c = getUserMsgByUID(hangRecord[i].uid).coin
                updateUserCoin(c + round(hangRecord[i].money + profit).toLong(), hangRecord[i].uid)
                //发送邮件
                var email = JSONObject()
                email.put("msg", "掛機結束，獲得 ${round(hangRecord[i].money + profit)} 金幣")
                email.put("remark", "掛機結束郵件")
                EmailController.Handle_sysEmail(email, hangRecord[i].uid)
                //删除已经完成的挂机记录
                GambleService.deleteHangRecord(hangRecord[i].id)
            }
        }
    }


    fun querySort(agent: Agent, root: JSONObject) {
        val type = root["type"] as Int

        var array = JSONArray()

        val list = GambleService.queryAllRoom(type)
        list.sortByDescending { it.rate }

        for (i in 0..3) {
            var gamble = list[i]
            //前端是1-100
            gamble.rid = gamble.rid - 399
            array.put(JSON.toJSON(gamble))
        }

        Send(agent, Msg(1, array, "sort", "hall_hangup"))
    }

    fun Msg(result: Any?, detail: Any?, data: Any?, command: Any?): String {
        val msg = JSONObject()
        msg.put("command", command)
        msg.put("result", result)
        msg.put("data", data)
        msg.put("detail", detail)
        return msg.toString()
    }

    fun Send(agent: Agent, msg: String) = agent.Send(agent.CID, msg)
}