package com.shine.controller.poker.ssz.tool


import com.shine.amodel.Texas
import com.shine.controller.poker.dz.LogicDz
import com.shine.controller.poker.ssz.CardObjDz
import com.shine.controller.poker.ssz.LogicSsz
import org.json.JSONObject
import org.junit.Test


object DzUtil {

    fun getCardObj(pair: Pair<Int, IntArray>): CardObjDz {

        var dz_obj = CardObjDz(Pair(0, intArrayOf(0)), intArrayOf(0), 0)

        val nowArray = getCardValue(pair.second)  //得到转换后的值
        val array = pair.second                   //原始值

        dz_obj.card = pair
        dz_obj.newCard = nowArray

        if (checkRoyalTongHuaShun(array).first) {
            dz_obj.Boo = 8
            dz_obj.newCard = checkRoyalTongHuaShun(array).second
        } else if (checkTongHuaShun(array).first) {
            dz_obj.Boo = 7
            dz_obj.newCard = checkTongHuaShun(array).second
        } else if (checkSiMei(nowArray).first) {
            dz_obj.Boo = 6
            dz_obj.newCard = checkSiMei(nowArray).second
        } else if (checkHuLu(nowArray).first) {
            dz_obj.Boo = 5
            dz_obj.newCard = checkHuLu(nowArray).second
        } else if (checkTongHua(array).first) {         // 因为同花需要转牌所有传的是原来的值
            dz_obj.Boo = 4
            dz_obj.newCard = checkTongHua(array).second
        } else if (checkShunZi(array).first) {
            dz_obj.Boo = 3
            dz_obj.newCard = checkShunZi(array).second     //因为同花需要转牌所有传的是原来的值
        } else if (checkSanTiao(nowArray).first) {
            dz_obj.Boo = 2
            dz_obj.newCard = checkSanTiao(nowArray).second
        } else if (checkDuiZi(nowArray).first) {
            dz_obj.Boo = 1
            val list = checkDuiZi(nowArray).second
            if (list.size == 2) {
                dz_obj.newCard = arrayConnect(list.get(0), list.get(1))
            } else {
                dz_obj.newCard = list.get(0)
            }
        }
        return dz_obj
    }

    fun getCardValue(array: IntArray): IntArray {
        var cardValue = IntArray(array.size)
        for ((i, v) in array.withIndex()) {
            cardValue[i] = v and LogicDz.MASK_VALUE
        }
        return cardValue
    }

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

    fun checkHuLu(nowArray: IntArray): Pair<Boolean, IntArray> {

        var re = IntArray(5)

        var r_3 = IntArray(3)
        var r_2 = IntArray(2)

        nowArray.sort()

        val diffVlueO = nowArray.size - 3

        val huLu_3 = splitArray(nowArray, 3, diffVlueO).filter { it.toSet().size == 1 }

        if (huLu_3.size > 0) {
            if (huLu_3.minBy { it.sum() == 3 } != null) {
                r_3 = huLu_3.minBy { it.sum() == 3 } as IntArray
            } else {
                r_3 = huLu_3.maxBy { it.sum() } as IntArray
            }
        }

        val arrayList = nowArray.toMutableList()
        arrayList.removeAll(r_3.toList())

        val array_2 = arrayList.toIntArray()

        val diffVlueT = array_2.size - 2
        val huLu_2 = splitArray(array_2, 2, diffVlueT).filter { it.toSet().size == 1 }

        if (huLu_2.size > 0) {
            if (huLu_2.minBy { it.sum() == 2 } != null) {
                r_2 = huLu_2.minBy { it.sum() == 2 } as IntArray
            } else {
                r_2 = huLu_2.maxBy { it.sum() } as IntArray
            }
        }

        re = arrayConnect(r_3, r_2)

        val t = re.toMutableList()
        t.removeAll(listOf(0))

        if (t.toSet().size == 2) {
            return Pair(true, re)
        } else {
            return Pair(false, re)
        }
    }

    fun checkTongHua(array: IntArray): Pair<Boolean, IntArray> {
        val cardColor = getCardColor(array)

        val color = intArrayOf(0, 16, 32, 48)
        val list = ArrayList<Int>()

        for (i in 0..3) {
            list.add(cardColor.count { it == color.get(i) })
        }

        if (list.filter { it >= 5 }.size == 0) {
            return Pair(false, cardColor)
        } else {
            return Pair(true, list.filter { it >= 5 }.toIntArray())
        }
    }

    fun checkShunZi(array: IntArray): Pair<Boolean, IntArray> {
        var re = IntArray(5)             //return of value

        val cardArray = DzUtil.getCardValue(array).toSet().toIntArray()
        cardArray.sort()

        val diffVale = cardArray.size - 5       //Difference value

        if (diffVale <= 0) return Pair(false, intArrayOf(0))

        var map = HashMap<IntArray, Int>()
        val sunZiMax = intArrayOf(10, 11, 12, 13, 1)

        if (array.contains(1)) {
            var boo = true
            for (i in 0 until sunZiMax.size) {
                if (!cardArray.contains(sunZiMax[i])) {
                    boo = false
                    break
                }
            }
            if (boo) re = sunZiMax
        }

        if (re.sum() == 0) {
            val list = splitArray(cardArray, 5, diffVale).filter {
                arrayAddSum(it[0], 5) == it.sum()
            }
            if (list.size > 0) {
                re = list.maxBy { it.sum() } as IntArray
            }
        }

        if (re.sum() > 0) return Pair(true, re)
        else return Pair(false, re)
    }

    fun checkSanTiao(array: IntArray): Pair<Boolean, IntArray> {

        var re = IntArray(3)
        val diffValue = array.size - 3
        array.sort()

        val sanTiao = splitArray(array, 3, diffValue).filter { it.toSet().size == 1 }
        if (sanTiao.size > 0) {
            if (sanTiao.minBy { it.sum() == 3 } != null) {
                re = sanTiao.minBy { it.sum() == 3 } as IntArray
            } else {
                re = sanTiao.maxBy { it.sum() } as IntArray
            }
        }

        if (re.sum() > 0) {
            return Pair(true, re)
        } else {
            return Pair(false, re)
        }
    }

    fun checkDuiZi(array: IntArray): Pair<Boolean, ArrayList<IntArray>> {
        var re = ArrayList<IntArray>()

        array.sort()
        val diffValue = array.size - 2

        val list = splitArray(array, 2, diffValue)

        val duiZi = list.filter { it.toSet().size == 1 }.toMutableList()

        if (duiZi.size > 0) {
            val AA = duiZi.minBy { it.sum() }
            if (AA != null) {
                re.add(AA)
            } else {
                duiZi.sortByDescending { it.sum() }

                if (duiZi.size == 1) re.add(duiZi.get(0))
                if (duiZi.size == 2) re.addAll(duiZi)
            }
        }

        if (re.size > 0) {
            return Pair(true, re)
        } else {
            return Pair(false, ArrayList<IntArray>())
        }
    }

    fun checkSiMei(nowArray: IntArray): Pair<Boolean, IntArray> {
        var re = IntArray(4)

        val diffVale = nowArray.size - 4
        nowArray.sort()

        val list = splitArray(nowArray, 4, diffVale)

        val siMei = list.filter { it.toSet().size == 1 }

        if (siMei.size > 0) {
            if (siMei.minBy { it.sum() == 4 } != null) {
                re = siMei.minBy { it.sum() == 4 } as IntArray
            } else {
                re = siMei.maxBy { it.sum() } as IntArray
            }
        }

        if (re.sum() > 0) {
            return Pair(true, re)
        } else {
            return Pair(false, IntArray(0))
        }
    }

    fun checkRoyalTongHuaShun(array: IntArray): Pair<Boolean, IntArray> {
        var boo: Boolean = false

        val p = checkShunZi(array)
        if (p.first and checkTongHua(array).first) {
            if (p.second.sum() == 47) boo = true
        }

        if (boo) return p
        else return Pair(boo, IntArray(0))
    }

    fun checkTongHuaShun(array: IntArray): Pair<Boolean, IntArray> {
        var boo: Boolean = false
        val p = checkShunZi(array)
        if (p.first and checkTongHua(array).first) boo = true
        if (boo) {
            return p
        } else {
            return Pair(boo, IntArray(0))
        }
    }

    fun splitArray(array: IntArray, size: Int, diffVale: Int): ArrayList<IntArray> {
        var list = ArrayList<IntArray>()
        for (i in 0..diffVale) {
            list.add(array.copyOfRange(i, size + i))
        }
        return list
    }

    fun arrayAddSum(value: Int, size: Int): Int {
        var oldV = value
        var list = ArrayList<Int>()
        for (i in 0 until size) {
            list.add(oldV + i)
        }
        return list.sum()
    }

    fun arrayConnect(array_1: IntArray, array_2: IntArray): IntArray {
        val v1 = array_1.size
        val v2 = array_2.size

        var array = IntArray(v1 + v2)

        for (i in 0 until v1) {
            array[i] = array_1[i]
        }
        for (i in 0 until array_2.size) {
            array[i + v1] = array_2[i]
        }
        return array
    }

    fun getTexasJson(texas: Texas): JSONObject {
        var json = JSONObject()
        json.put("blindness", texas.blindness)
        json.put("bets", texas.bets)
        json.put("allIn", texas.allIn)
        json.put("count", texas.count)
        json.put("discard", texas.discard)
        json.put("di", texas.di)
        return json
    }
}