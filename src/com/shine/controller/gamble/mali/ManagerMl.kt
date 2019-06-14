
import com.shine.agent.Agent
import com.shine.amodel.Manager
import com.shine.controller.aHall.Hall
import org.json.JSONObject
import java.util.*

/**
 *  Create by Colin
 *  Date:2018/6/25.
 *  Time:9:39
 *  14803101
 */
object ManagerMl:Manager {

    override val type="hall_hangup"
    override val rand=Random()
    override val mapRoom = Hall.gMapRoom
    override val mapUserRoom = Hall.gMapUserRoom

    override fun OnCreate(agent: Agent, root: JSONObject) {
        val uid = agent.UID
        val rid = root["rid"] as Int

        val table = TableMl(rid,0)

        mapRoom[rid] = table
        mapUserRoom.put(uid, table)
        Send(agent, Msg(rid, "success", "create", type))
    }


    override fun HandleAll(agent: Agent, root: JSONObject){
        val doo = root["data"] as String
        val uid = agent.UID
        val user= agent.user
        if (mapUserRoom[uid]==null) return
        val table = mapUserRoom[uid] as TableMl
        when(doo){
            "leave"     -> table.OnUserLeave(uid)          //离开游戏
            "money"   -> table.money(agent,root)             //兑换积分
//            "ya"        -> table.ya(agent,root)             //压倍
            "kai"       -> table.kai(agent,root)            //开始
        }
    }

}