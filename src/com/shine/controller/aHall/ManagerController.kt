package com.shine.controller.aHall

import com.shine.agent.Agent
import com.shine.amodel.*
import com.shine.aservice.achievement.AchievementTempService
import com.shine.aservice.army.ArmyAdminService
import com.shine.aservice.gamble.GambleService
import com.shine.aservice.notice.NoticeService
import com.shine.aservice.prize.PrizeService
import com.shine.aservice.shop.GoodsService
import com.shine.aservice.user.UserService
import com.shine.controller.poker.Landlords.tool.JSONTool
import org.json.JSONObject

/**
 * 奖励系统
 */
object ManagerController {

    fun HandleAll(agent: Agent, root: JSONObject){
        var detail=root.get("data") as String
        when(detail){
            "deleteUser"     ->deleteUser(agent,root)           //删除用户
            "updateGoods"    ->updateGoods(agent,root)       //修改商品信息
            "insertGoods"    ->insertGoods(agent,root)       //添加商品信息
            "deleteGoods"    ->deleteGoods(agent,root)       //删除商品
            "updateAtemp"    ->updateAtemp(agent,root)       //修改成就信息
            "deleteAtemp"    ->deleteAtemp(agent,root)       //删除成就信息
            "updatePrize"    ->updatePrize(agent,root)       //修改成就奖励信息
            "insertPrize"    ->insertPrize(agent,root)       //添加成就奖励信息
            "deletePrize"    ->deletePrize(agent,root)       //删除成就奖励信息
            "updateNotify"    ->updateNotify(agent,root)       //修改游戏公告信息
            "insertNotify"    ->insertNotify(agent,root)       //添加游戏公告信息
            "deleteNotify"    ->deleteNotify(agent,root)       //删除游戏公告信息
            "updateArmyWar"    ->updateArmyWar(agent,root)       //修改军团战信息
            "updateGameNum"  -> updateGamble(agent,root)       //修改挂机奖励比例
             else            ->{
                println(String.format("未知命令:%s%s",root["data"],root["command"]))
                return
            }
        }
    }
    fun updateGamble(agent: Agent, root: JSONObject){
        var num=root["detail"] as String
        if(num.split(".").size!=2) {
            var res = GambleService.modifYieldRate(num.toDouble())
//            println("修改结果："+ if(res==1) true else false)
            Send(agent, RoomController.Msg(1, detail = "success", data = root["data"], command = root["command"]))
        }
    }
    fun updateArmyWar(agent: Agent, root: JSONObject){
//        println(JSONTool.toObj(root["detail"].toString(),ArmyAction::class.java))
        ArmyAdminService.updateArmyAction(JSONTool.toObj(root["detail"].toString(),ArmyAction::class.java) as ArmyAction)
        Send(agent, RoomController.Msg(1, detail = "success", data = root["data"], command = root["command"]))
    }
    fun updateNotify(agent: Agent, root: JSONObject){
//        println(JSONTool.toObj(root["detail"].toString(),Notice::class.java))
        NoticeService.updateNotice(JSONTool.toObj(root["detail"].toString(),Notice::class.java) as Notice)
        Send(agent, RoomController.Msg(1, detail = "success", data = root["data"], command = root["command"]))
    }
    fun insertNotify(agent: Agent, root: JSONObject){
//        println(JSONTool.toObj(root["detail"].toString(),Notice::class.java))
        NoticeService.insertNotice(JSONTool.toObj(root["detail"].toString(),Notice::class.java) as Notice)
        Send(agent, RoomController.Msg(1, detail = "success", data = root["data"], command = root["command"]))
    }
    fun deleteNotify(agent: Agent, root: JSONObject){
//        println(JSONTool.toObj(root["detail"].toString(),Notice::class.java))
        NoticeService.deleteNotice(JSONTool.toObj(root["detail"].toString(),Notice::class.java) as Notice)
        Send(agent, RoomController.Msg(1, detail = "success", data = root["data"], command = root["command"]))
    }
    fun updatePrize(agent: Agent, root: JSONObject){
//        println(JSONTool.toObj(root["detail"].toString(),Prize::class.java))
        PrizeService.updatePrize(JSONTool.toObj(root["detail"].toString(),Prize::class.java) as Prize)
        Send(agent, RoomController.Msg(1, detail = "success", data = root["data"], command = root["command"]))
    }
    fun insertPrize(agent: Agent, root: JSONObject){
//        println(JSONTool.toObj(root["detail"].toString(),Prize::class.java))
        PrizeService.insertPrize(JSONTool.toObj(root["detail"].toString(),Prize::class.java) as Prize)
        Send(agent, RoomController.Msg(1, detail = "success", data = root["data"], command = root["command"]))
    }
    fun deletePrize(agent: Agent, root: JSONObject){
//        println(JSONTool.toObj(root["detail"].toString(),Prize::class.java))
        PrizeService.deletePrize(JSONTool.toObj(root["detail"].toString(),Prize::class.java) as Prize)
        Send(agent, RoomController.Msg(1, detail = "success", data = root["data"], command = root["command"]))
    }
    fun updateAtemp(agent: Agent, root: JSONObject){
//        println(JSONTool.toObj(root["detail"].toString(),AchievementTemp::class.java))
        AchievementTempService.updateAchievementTemp(JSONTool.toObj(root["detail"].toString(),AchievementTemp::class.java) as AchievementTemp)
        Send(agent, RoomController.Msg(1, detail = "success", data = root["data"], command = root["command"]))
    }
    fun deleteAtemp(agent: Agent, root: JSONObject){
//        println(JSONTool.toObj(root["detail"].toString(),AchievementTemp::class.java))
        AchievementTempService.deleteAchievementTemp(JSONTool.toObj(root["detail"].toString(),AchievementTemp::class.java) as AchievementTemp)
        Send(agent, RoomController.Msg(1, detail = "success", data = root["data"], command = root["command"]))
    }
    fun deleteUser(agent: Agent, root: JSONObject){
//        println(JSONTool.toObj(root["detail"].toString(),User::class.java))
       UserService.deleteUser(JSONTool.toObj(root["detail"].toString(),User::class.java) as User)
        Send(agent, RoomController.Msg(1, detail = "success", data = root["data"], command = root["command"]))
    }
    fun updateGoods(agent: Agent, root: JSONObject){
//        println(JSONTool.toObj(root["detail"].toString(),Goods::class.java))
        GoodsService.updateGoods(JSONTool.toObj(root["detail"].toString(),Goods::class.java) as Goods)
        Send(agent, RoomController.Msg(1, detail = "success", data = root["data"], command = root["command"]))
    }
    fun insertGoods(agent: Agent, root: JSONObject){
//        println(JSONTool.toObj(root["detail"].toString(),Goods::class.java))
        GoodsService.insertGoods(JSONTool.toObj(root["detail"].toString(),Goods::class.java) as Goods)
        Send(agent, RoomController.Msg(1, detail = "success", data = root["data"], command = root["command"]))
    }
    fun deleteGoods(agent: Agent, root: JSONObject){
//        println(JSONTool.toObj(root["detail"].toString(),Goods::class.java))
        GoodsService.deleteGoods(JSONTool.toObj(root["detail"].toString(),Goods::class.java) as Goods)
        Send(agent, RoomController.Msg(1, detail = "success", data = root["data"], command = root["command"]))
    }
    fun Send(agent: Agent,msg:String)=agent.Send(agent.CID,msg)
}