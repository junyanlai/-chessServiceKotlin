package com.shine.aservice.currency

import com.shine.amodel.AchievementTemp
import com.shine.amodel.Fmcc
import com.shine.amodel.User
import com.shine.aservice.achievement.AchievementTempService
import com.shine.aservice.achievement.DailyAchievement.consumeTemp
import com.shine.aservice.shop.FmccService
import com.shine.aservice.user.UserService
import java.text.SimpleDateFormat
import java.util.*

object BonusCalculationService:IBonusCalculationService {


    //结算加成金额
    override fun bonusCalculation(num: Map<String, Int>, user: User): Map<String, Int> {
        var exp = num.get("exp")
        var coin = num.get("coin")
        val cid=user.cid
        var user = UserService.getUserMsgByUID(user.uid)
        var allMap = mutableMapOf<String, Int>()
        if(exp != null)
            allMap.put("exp", Title_of_the_class(user, calculateExp(exp, user)))
        if(coin != null)
            allMap.put("coin", Gold_num(cid,user,calculateCoin(coin, user)))
        return allMap
    }
    //结算加成金额
    override fun bonusCalculation(coin:Int,exp:Int, user: User): Map<String, Int> {
        val exp = exp
        val coin = coin
        val cid=user.cid
        val user = UserService.getUserMsgByUID(user.uid)
        var allMap = mutableMapOf<String, Int>()
       if(exp != null)
           allMap.put("exp", Title_of_the_class(user, calculateExp(exp, user)))
        if(coin != null)
            allMap.put("coin", Gold_num(cid,user,calculateCoin(coin, user)))
        return allMap
    }
    //计算金币加成后数量
    fun calculateCoin(coin: Int, user: User): Double {
        return rank_vip_verification("coin", coin, user)  //vip
        + rank_prop_verification("coin", coin, user) //道具
        + coin
    }

    //计算加成后的exp数量
    fun calculateExp(exp: Int, user: User): Double {
        if(exp<0) return exp.toDouble()
        return rank_vip_verification("exp", exp, user)
        + rank_prop_verification("exp", exp, user)
        + exp
    }

    //vip等级称号
    fun rank_vip_verification(type: String, num: Int, user: User): Double {
        //根据用户expLevel查询称号属性
        if (user.vipLevel == 0) return num.toDouble()
        var aTemp = AchievementTempService.selectAchievementTempOne(AchievementTemp(atid = user.vipLevel))
        when (type) {
            "coin" -> return (aTemp.addGold * num)
            "exp" -> return (aTemp.addExp * num)
        }
        return num.toDouble()
    }

    //道具加成
    fun rank_prop_verification(type: String, num: Int, user: User): Double {
        //用户id 查询所有buf
        var fmcc = FmccService.selectFmccByUid(Fmcc(uid=user.uid,type=type))
        if (fmcc == null) return num.toDouble()
        var money = num.toDouble()
        var sp= SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        for (add in fmcc) {
            when(add.outDateType){
                "hour"->{
                    if(sp.parse(add.closingTime)> Date()){
                        money = (add.addition * money)
                    }else{
                        FmccService.deleteFmcc(Fmcc(fid=add.fid))
                    }
                }
                "num"->{
                    if((add.outDate?:0)>0){
                        money = (add.addition * money)
                        FmccService.updateFmcc(Fmcc(fid = add.fid,outDate = add.outDate?:0-1))
                    }else{
                        FmccService.deleteFmcc(Fmcc(fid=add.fid))
                    }
                }
            }
        }
        return money
    }

    //更换用积分等级称号
    fun Title_of_the_class(user: User, exp: Double): Int {
        var tempLevel=AchievementTempService.selectAchievementTempOne(AchievementTemp(triggerType = "exp"))
        if(user.level==tempLevel.atid||user.exp>tempLevel.teps){
            UserService.updateUserMsgByUID(User(uid = user.uid, level = tempLevel.atid, exp = (exp.toInt() + user.exp)))
            return exp.toInt()
        }
        var aTemp = AchievementTempService.selectAchievementTempByTeps(AchievementTemp(triggerType = "exp", teps = (exp + user.exp).toInt()))
        UserService.updateUserMsgByUID(User(uid = user.uid, level = aTemp.atid, exp = (exp.toInt() + user.exp)))
        return exp.toInt()
    }

    //修改用户对局累计金币数量，如果用户获得成就记录成就表
    fun Gold_num(cid:Int,user: User, coin: Double) :Int{
        //println("用户单词金币获得数量"+coin)
        if(coin.toInt()>0) {
            UserService.updateUserMsgByUID(User(uid = user.uid, gameCoin = (user.gameCoin + coin.toInt()))) //修改用户累计金币
        }
        if (coin.toInt()>=0||user.coin>-coin.toInt()) {
            UserService.updateUserCoin(user.coin + coin.toInt(), user.uid)
        } else {
            UserService.updateUserCoin(0, user.uid)
        }
        consumeTemp(cid,coin.toInt(),user)
     return coin.toInt()
    }

}