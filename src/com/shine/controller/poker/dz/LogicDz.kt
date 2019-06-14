package com.shine.controller.poker.dz

import com.shine.controller.poker.ssz.CardObjDz
import com.shine.controller.poker.ssz.tool.DzUtil
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


object LogicDz {

    val MASK_COLOR = 0xF0
    val MASK_VALUE = 0x0F

    val cardList = intArrayOf(
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, //方块 A - K
            0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, //梅花 A - K
            0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x2D, //红桃 A - K
            0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x3B, 0x3C, 0x3D //黑桃 A - K
    )


    var list = cardList.toMutableList()

    fun CompareCard(map: HashMap<Int, IntArray>): ArrayList<CardObjDz> {
        val cardObjList = ArrayList<CardObjDz>()

        map.forEach { t, u -> cardObjList.add(DzUtil.getCardObj(Pair(t, u))) }

        val result = ArrayList<CardObjDz>()

        val maxBooObj = cardObjList.maxBy { it.Boo }

        if (maxBooObj != null) {
            val win = cardObjList.filter { it.Boo == maxBooObj.Boo }

            var temp = ArrayList<CardObjDz>()
            temp.add(win.get(0))

            for (i in 1 until win.size) {
                0
                win.get(i).newCard = Ato14(win.get(i).newCard)
            }

//            val maxCard = win.maxBy { it.newCard.sum() }

            val fighting = win.filter { it.newCard.sum() == maxBooObj.newCard.sum() }

            for (i in 0 until fighting.size) {
                result.add(fighting.get(i))
            }
        }
        return result
    }


    fun Ato14(array: IntArray): IntArray {

        for (i in 0 until array.size) {
            if (array[i] == 1) array[i] = 14
        }
        return array
    }

}