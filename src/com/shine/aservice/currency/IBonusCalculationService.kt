package com.shine.aservice.currency
import com.shine.amodel.User

/**
 * 结算加成计算
 */
interface IBonusCalculationService {

//结算加成计算
     fun bonusCalculation(msg:Map<String,Int>,user: User):Map<String,Int>

     fun bonusCalculation(coin:Int,exp:Int, user: User): Map<String, Int>
}