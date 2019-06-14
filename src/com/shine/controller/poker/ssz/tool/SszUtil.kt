package com.shine.controller.poker.ssz.tool

import com.shine.controller.poker.ssz.CardObj
import com.shine.controller.poker.ssz.LogicSsz

object SszUtil {

    /**
     * 把数组传过去获得obj
     */
    fun getCardObj(array: IntArray): CardObj {
        var cardObj = CardObj(array, array, 0)
        array.sortDescending()    //必须排序[降序]，这个排序是为了好进行 牌型的比较

        var nowArray = IntArray(array.size) //得到转换后的值

        for ((i, v) in getCardValue(array).withIndex()) {
            nowArray[i] = v
        }
        nowArray.sortDescending()
        cardObj.Card = nowArray
        //前敦
        if (array.size == 3) {
            if (LogicSsz.checkSanTiao(nowArray)) cardObj.Boo = 2    //三条
            else if (LogicSsz.checkDuiZi(nowArray)) cardObj.Boo = 1 //对子
        }
        //中后敦
        if (array.size > 3) {
            if (LogicSsz.checkTongHuaShun(array)) cardObj.Boo = 7    //同花顺   //因为对比同花，所以需要传原来的值，顺子逻辑需要的转过的值
            else if (LogicSsz.checkSiMei(nowArray)) cardObj.Boo = 6     //四梅
            else if (LogicSsz.checkHuLu(nowArray)) cardObj.Boo = 5      //葫芦
            else if (LogicSsz.checkSanTiao(nowArray)) cardObj.Boo = 2   //三条
            else if (LogicSsz.checkDuiZi(nowArray)) cardObj.Boo = 1     //对子
            else if (LogicSsz.checkTongHua(array)) cardObj.Boo = 4   //同花       因为同花需要转牌所有传的是原来的值
            else if (LogicSsz.checkShunZi(nowArray)) cardObj.Boo = 3    //顺子
        }
        return cardObj
    }

    /**
     * 转牌值
     */
    fun getCardValue(array: IntArray): IntArray {

        var cardValue = IntArray(array.size)
        for ((i, v) in array.withIndex()) {
            cardValue[i] = v and LogicSsz.MASK_VALUE
        }
        return cardValue
    }

    /**
     * 转花色值
     */
    fun getCardColor(array: IntArray): IntArray {

        var cardColor = IntArray(array.size)
        for ((i, v) in array.withIndex()) {
            cardColor[i] = v and LogicSsz.MASK_COLOR
        }
        return cardColor
    }

    /**
     * 把有序的牌随机打乱顺序
     */
    fun GetCardMount(): IntArray {
        val cm = LogicSsz.cardList.toMutableList()
        cm.shuffle()
        return cm.toIntArray()
    }

}