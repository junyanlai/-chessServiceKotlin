package com.shine.controller.poker.ssz

import com.shine.controller.poker.ssz.tool.SszUtil
import org.json.JSONArray


object LogicSsz {

    val MASK_COLOR = 0xF0
    val MASK_VALUE = 0x0F

    var numberOfScore = IntArray(4) { 0 }
    var numberOfGold = IntArray(4) { 0 }
    val cardList = intArrayOf(
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, //方块 A - K
            0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, //梅花 A - K
            0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x2D, //红桃 A - K
            0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x3B, 0x3C, 0x3D //黑桃 A - K
    )

    fun CompareCard(list: MutableList<IntArray>, di: Int): ArrayList<ArrayList<IntArray>> {
        var firstArray: MutableList<IntArray> = ArrayList()
        var twoArry: MutableList<IntArray> = ArrayList()
        var threeArray: MutableList<IntArray> = ArrayList()
        for (item in list) {
            val arr_1 = item.copyOfRange(0, 3)
            val arr_2 = item.copyOfRange(3, 8)
            val arr_3 = item.copyOfRange(8, 13)

            firstArray.add(arr_1)
            twoArry.add(arr_2)
            threeArray.add(arr_3)
        }

        var firstJson = getCalculateJson(firstArray, 1, di)
        var twoJson = getCalculateJson(twoArry, 2, di)
        var threeJson = getCalculateJson(threeArray, 3, di)

        isAllWin(firstJson, twoJson, threeJson)
        var reList = isDaQiang(firstJson, twoJson, threeJson)  //打枪 这里使用这个list 是因为打枪把返回值的逻辑处理好了
        isShuangGuaiCS(firstArray, twoArry, threeArray)   //双怪冲三


        val scoreJson = ArrayList<IntArray>() //分数和金币
        scoreJson.add(numberOfScore) //原始分
        scoreJson.add(numberOfGold)//乘上金币的分
        reList.add(scoreJson)

        return reList
    }

    /**
     *  最终会获得四个数组对象，进行比对>>>cardOb 里面保存两个数组，一个转换后的数组（方面下面的牌型判断） 一个是原值数据
     */
    fun getCalculateJson(list: MutableList<IntArray>, dun: Int, di: Int): JSONArray {
        val cardObjList: MutableList<CardObj> = ArrayList()
        var msg = JSONArray()

        for (item in list) {
            cardObjList.add(SszUtil.getCardObj(item))
        }

        for ((i, v) in cardObjList.withIndex()) {
            var json = JSONArray()
            for ((j, v) in cardObjList.withIndex()) {
                if (i == j) continue    //自己不和自己对比
                if (cardObjList.get(i).Boo > cardObjList.get(j).Boo) {
                    json.put(1)
                    if (cardObjList.get(i).Boo == 2 && dun == 1) bonusPoint(i, 3, j, di)       //  铁三加分
                    if (cardObjList.get(i).Boo == 5 && dun == 2) bonusPoint(i, 2, j, di)//  中墩葫芦加分
                    if (cardObjList.get(i).Boo == 6) {  //四梅自己加4分对手扣4分
                        bonusPoint(i, 4, j, di)
                        subtraction(j, 4)
                    }

                    if (cardObjList.get(i).Boo == 7) {  //同花顺自己加5分对手扣5分
                        bonusPoint(i, 5, j, di)
                        subtraction(j, 5)
                    }

                    bonusPoint(i, 1, j, di)
                    continue
                } else if (cardObjList.get(i).Boo < cardObjList.get(j).Boo) {
                    json.put(-1)
                    continue
                } else if (cardObjList.get(i).Boo == cardObjList.get(j).Boo) {
                    if (cardObjList.get(i).Boo == 0) {
                        json.put(singleCard(cardObjList.get(i).Card, cardObjList.get(j).Card, i, j, di))
                    } else if (cardObjList.get(i).Boo == 1) {
                        json.put(contrastDuiZi(cardObjList.get(i).Card, cardObjList.get(j).Card, i, j, di))    //对子
                    } else if (cardObjList.get(i).Boo == 2) {
                        json.put(contrastSanTiao(cardObjList.get(i).Card, cardObjList.get(j).Card, dun, i, j, di))  //三条 i 为座位
                    } else if (cardObjList.get(i).Boo == 3) {
                        json.put(contrastShunZi(cardObjList.get(i).Card, cardObjList.get(j).Card))   //顺子
                    } else if (cardObjList.get(i).Boo == 4) {
                        json.put(contrastTongHua(cardObjList.get(i).Card, cardObjList.get(j).Card, i, j, di))  //同花
                    } else if (cardObjList.get(i).Boo == 5) {
                        json.put(contrastHuLu(cardObjList.get(i).Card, cardObjList.get(j).Card, dun, i, j, di))     //葫芦
                    } else if (cardObjList.get(i).Boo == 6) {
                        json.put(contrastSiMei(cardObjList.get(i).Card, cardObjList.get(j).Card, i, j, di))  //四梅
                    } else if (cardObjList.get(i).Boo == 7) {
                        json.put(contrastTongHuaShun(cardObjList.get(i).Card, cardObjList.get(j).Card, i, j, di))    //同花顺
                    }
                }
            }

            msg.put(json)
        }
        return msg
    }

    /**
     * 加分操作（position：当前玩家 j:pk的玩家,这个加分操作 对应的也操作金币，加金币的同时对应被减金币的玩家）
     * 如果 position和 j 这两个参数都是-1 就代表是特殊牌型不参与加减金币，只能加分
     */
    fun bonusPoint(position: Int, scroe: Int, j: Int, di: Int) {
        if (position != -1 && j != -1) {
            // print("加分操作 玩家：${position}>>>分值:${scroe}>>>目前分：${numberOfScore[position]}>>>")
            LogicSsz.numberOfGold[position] = LogicSsz.numberOfGold[position] + (di.times(scroe))     //赢的人加分（金币）
            LogicSsz.numberOfGold[j] = LogicSsz.numberOfGold[j] - (di.times(scroe))                   //输的人减分，并且会一无所有 阿门
        } else {
            LogicSsz.numberOfScore[position] = LogicSsz.numberOfScore[position] + scroe
            //  print("加过之后的分：${numberOfScore[position]}")
        }
    }

    /**
     * 减分操作
     */
    fun subtraction(position: Int, scroe: Int) {
        if (position != -1) {
            //  print("减分操作  玩家：${position}>>>分值:${scroe}>>>目前分：${numberOfScore[position]}>>>")
            LogicSsz.numberOfScore[position] = LogicSsz.numberOfScore[position] - scroe
            //  print("减过之后的分：${numberOfScore[position]}")
        }
    }

    /**
     * 双怪冲三
     */
    fun isShuangGuaiCS(firstList: MutableList<IntArray>, twoList: MutableList<IntArray>, threeList: MutableList<IntArray>) {
        var list_1: MutableList<CardObj> = ArrayList()
        var list_2: MutableList<CardObj> = ArrayList()
        var list_3: MutableList<CardObj> = ArrayList()
        var list_4: MutableList<CardObj> = ArrayList()

        for ((i, v) in firstList.withIndex()) {     //第一敦
            if (i == 0) list_1.add(SszUtil.getCardObj(v))
            if (i == 1) list_2.add(SszUtil.getCardObj(v))
            if (i == 2) list_3.add(SszUtil.getCardObj(v))
            if (i == 3) list_4.add(SszUtil.getCardObj(v))
        }

        for ((i, v) in twoList.withIndex()) {
            if (i == 0) list_1.add(SszUtil.getCardObj(v))
            if (i == 1) list_2.add(SszUtil.getCardObj(v))
            if (i == 2) list_3.add(SszUtil.getCardObj(v))
            if (i == 3) list_4.add(SszUtil.getCardObj(v))
        }

        for ((i, v) in threeList.withIndex()) {
            if (i == 0) list_1.add(SszUtil.getCardObj(v))
            if (i == 1) list_2.add(SszUtil.getCardObj(v))
            if (i == 2) list_3.add(SszUtil.getCardObj(v))
            if (i == 3) list_4.add(SszUtil.getCardObj(v))
        }

        if (list_1.get(0).Boo == 2) {
            if ((list_1.get(1).Boo == 6).or(list_1.get(1).Boo == 7).and((list_1.get(2).Boo == 6).or(list_1.get(2).Boo == 7))) {
                bonusPoint(0, 6, -1, -1)
                // println("双怪冲三:玩家 0")
            }
        }

        if (list_2.get(0).Boo == 2) {
            if ((list_2.get(1).Boo == 6).or(list_2.get(1).Boo == 7).and((list_2.get(2).Boo == 6).or(list_2.get(2).Boo == 7))) {
                bonusPoint(1, 6, -1, -1)
                // println("双怪冲三:玩家 1")
            }
        }

        if (list_3.get(0).Boo == 2) {
            if ((list_3.get(1).Boo == 6).or(list_3.get(1).Boo == 7).and((list_3.get(2).Boo == 6).or(list_3.get(2).Boo == 7))) {
                bonusPoint(2, 6, -1, -1)
                // println("双怪冲三:玩家 2")
            }
        }

        if (list_4.get(0).Boo == 2) {
            if ((list_4.get(1).Boo == 6).or(list_4.get(1).Boo == 7).and((list_4.get(2).Boo == 6).or(list_4.get(2).Boo == 7))) {
                bonusPoint(0, 6, -1, -1)
                // println("双怪冲三:玩家 3")
            }
        }


    }

    /**
     * 判断是否三墩全赢
     */
    fun isAllWin(firstJson: JSONArray, twoJson: JSONArray, threeJson: JSONArray) {

        //判断全垒打
        var homeRun = HashMap<Int, Int>()
        homeRun.put(0, 0)
        homeRun.put(1, 0)
        homeRun.put(2, 0)
        homeRun.put(3, 0)

        for ((i, v) in firstJson.withIndex()) {
            var jsonArray = v as JSONArray
            for ((j, s) in jsonArray.withIndex()) {
                if (s == 1) {
                    homeRun[i] = homeRun[i] as Int + 1
                }
            }
        }

        for ((i, v) in twoJson.withIndex()) {
            var jsonArray = v as JSONArray
            for ((j, s) in jsonArray.withIndex()) {
                if (s == 1) {
                    homeRun[i] = homeRun[i] as Int + 1
                }
            }
        }

        for ((i, v) in threeJson.withIndex()) {
            var jsonArray = v as JSONArray
            for ((j, s) in jsonArray.withIndex()) {
                if (s == 1) {
                    homeRun[i] = homeRun[i] as Int + 1
                }
            }
        }

        //全垒打加27分
        for ((k, v) in homeRun) {
            if (v == 9) {
                //  println("${k}号玩家全垒打>>分数：${v}")
                bonusPoint(k, 27, -1, -1)
            }
        }
    }

    /**
     * 把JSONArray 里面的数据取出来，放到数组里面
     * 之后分别存到list，完成JSONArray分离，判断打枪
     */
    fun isDaQiang(firstJson: JSONArray, twoJson: JSONArray, threeJson: JSONArray): ArrayList<ArrayList<IntArray>> {
        val list_1 = ArrayList<IntArray>()
        val list_2 = ArrayList<IntArray>()
        val list_3 = ArrayList<IntArray>()
        val list_4 = ArrayList<IntArray>()

        val reList = ArrayList<ArrayList<IntArray>>()
        reList.add(list_1)
        reList.add(list_2)
        reList.add(list_3)
        reList.add(list_4)

        for ((i, v) in firstJson.withIndex()) {
            var json = v as JSONArray
            var arrayTemp = IntArray(3)

            for ((j, s) in json.withIndex()) {
                arrayTemp[j] = s as Int
            }
            if (i == 0) list_1.add(arrayTemp)
            if (i == 1) list_2.add(arrayTemp)
            if (i == 2) list_3.add(arrayTemp)
            if (i == 3) list_4.add(arrayTemp)
        }

        for ((i, v) in twoJson.withIndex()) {
            var json = v as JSONArray
            var arrayTemp = IntArray(3)

            for ((j, s) in json.withIndex()) {
                arrayTemp[j] = s as Int
            }
            if (i == 0) list_1.add(arrayTemp)
            if (i == 1) list_2.add(arrayTemp)
            if (i == 2) list_3.add(arrayTemp)
            if (i == 3) list_4.add(arrayTemp)
        }


        for ((i, v) in threeJson.withIndex()) {
            var json = v as JSONArray
            var arrayTemp = IntArray(3)

            for ((j, s) in json.withIndex()) {
                arrayTemp[j] = s as Int
            }
            if (i == 0) list_1.add(arrayTemp)
            if (i == 1) list_2.add(arrayTemp)
            if (i == 2) list_3.add(arrayTemp)
            if (i == 3) list_4.add(arrayTemp)
        }


        //打枪计算逻辑
        for ((j, s) in listEqualValue(list_1).withIndex()) {  //玩家一
            if (listEqualValue(list_1).size >= 1 && listEqualValue(list_1).size <= 2) {
                bonusPoint(0, 3, -1, -1)
                subtraction(1, 3)
                subtraction(2, 3)
                subtraction(3, 3)
            }
        }

        for ((j, s) in listEqualValue(list_2).withIndex()) {
            if (listEqualValue(list_2).size >= 1 && listEqualValue(list_2).size <= 2) {
                bonusPoint(1, 3, -1, -1)
                subtraction(2, 3)
                subtraction(3, 3)
                subtraction(0, 3)
            }
        }

        for ((j, s) in listEqualValue(list_3).withIndex()) {
            if (listEqualValue(list_3).size >= 1 && listEqualValue(list_3).size <= 2) {
                bonusPoint(2, 3, -1, -1)
                subtraction(3, 3)
                subtraction(1, 3)
                subtraction(0, 3)
            }
        }

        for ((j, s) in listEqualValue(list_4).withIndex()) {
            if (listEqualValue(list_4).size >= 1 && listEqualValue(list_4).size <= 2) {
                bonusPoint(3, 3, -1, -1)
                subtraction(0, 3)
                subtraction(1, 3)
                subtraction(2, 3)
            }
        }

        return reList
    }

    /**
     * 判断第一个值是否等于第二以及第三个值且等于1，返回的List会记录几次“打枪”
     */
    fun listEqualValue(list: ArrayList<IntArray>): ArrayList<Int> {
        var temp_1: Int = 0
        var temp_2: Int = 0
        var temp_3: Int = 0

        var reList = ArrayList<Int>()

        var array = list.get(0) as IntArray

        temp_1 = array[0]
        temp_2 = array[1]
        temp_3 = array[2]

        if (temp_1 == list.get(1)[0]) {
            if (temp_1 == list.get(2)[0] && temp_1 == 1) {
                reList.add(1)
            }
        }

        if (temp_2 == list.get(1)[1]) {
            if (temp_2 == list.get(2)[1] && temp_2 == 1) {
                reList.add(1)
            }
        }

        if (temp_3 == list.get(1)[2]) {
            if (temp_3 == list.get(2)[2] && temp_3 == 1) {
                reList.add(1)
            }
        }
        return reList
    }


    /**
     * 检查是否是对子
     */
    fun checkDuiZi(array: IntArray): Boolean {
        var boo: Boolean = false
        if (array.size == 3) {
            if (array.toSet().size == 2) boo = true
        } else if (array.size == 5) {
            if (array.toSet().size == 4) boo = true
        }
        return boo
    }

    /**
     * 对比两个对子的大小（是否会出现2个对子的情况，比最大的那个）
     */
    fun contrastDuiZi(array_1: IntArray, array_2: IntArray, position: Int, j: Int, di: Int): Int {
        var recordValue_1 = 0   //记录对子相等的值
        var recordValue_2 = 0

        var boo = 0


        for ((i, v) in array_1.withIndex()) {       //把A设置成14
            if (array_1[i] == 1) array_1[i] = 14
            if (array_2[i] == 1) array_2[i] = 14
        }

        var countMap_1 = getCountMap(array_1)
        var countMap_2 = getCountMap(array_2)

        for ((k, v) in countMap_1) {    //记录对子值
            if (v == 2) {
                recordValue_1 = k
            }
        }

        for ((k, v) in countMap_2) {
            if (v == 2) {
                recordValue_2 = k
            }
        }
        var filterArray_1 = array_1.filter { it != recordValue_1 }  //过滤掉对子
        var filterArray_2 = array_2.filter { it != recordValue_2 }

        if (recordValue_1 > recordValue_2) boo = 1
        else if (recordValue_1 < recordValue_2) boo = -1
        else if (recordValue_1 == recordValue_2) {
            boo = singleCard(filterArray_1.toIntArray(), filterArray_2.toIntArray(), position, j, di)   //降序 -1位置会被过滤掉
        }
        return boo
    }

    fun checkSanTiao(array: IntArray): Boolean {
        var boo: Boolean = false

        if (array.size == 3) {
            if (array.toSet().size == 1) boo = true
        } else if (array.size == 5) {
            if (array.toSet().size == 3) boo = true
        }
        return boo
    }

    fun contrastSanTiao(array_1: IntArray, array_2: IntArray, dun: Int = -1, position: Int, j: Int, di: Int): Int {
        var recordValue_1 = 0
        var recordValue_2 = 0
        var boo = 0

        for ((i, v) in array_1.withIndex()) {
            if (array_1[i] == 1) array_1[i] = 14
            if (array_2[i] == 1) array_2[i] = 14
        }
        var countMap_1 = getCountMap(array_1)
        var countMap_2 = getCountMap(array_2)

        for ((k, v) in countMap_1) {    //记录顺子值
            if (v == 3) {
                recordValue_1 = k
            }
        }

        for ((k, v) in countMap_2) {
            if (v == 3) {
                recordValue_2 = k
            }
        }

        var filterArray_1 = array_1.filter { it != recordValue_1 }  //过滤掉顺子
        var filterArray_2 = array_2.filter { it != recordValue_2 }

        if (recordValue_1 > recordValue_2) {
            boo = 1
            if (position != -1 && dun == 1) {//第一敦出现三条 且与其他玩家相对为胜
                bonusPoint(position, 3, j, di)
            }

        } else if (recordValue_1 < recordValue_2) boo = -1
        else if (recordValue_1 == recordValue_2) {
            boo = singleCard(filterArray_1.toIntArray(), filterArray_2.toIntArray(), position, j, di)   //降序 1位置会被过滤掉
        }
        return boo
    }

    fun checkShunZi(array: IntArray): Boolean {
        var boo: Boolean = false
        var newValue = 0;
        var oldValue = 0;

        var cardArrayValue = SszUtil.getCardValue(array)
        cardArrayValue.sort()//排序

        val firstValue = cardArrayValue[0] //第一个值

        for (i in 0..cardArrayValue.size - 1) {
            var addValue = firstValue + i
            newValue = newValue + addValue
        }

        for (item in cardArrayValue) {
            oldValue = oldValue + item
        }

        if (newValue == oldValue) boo = true
        return boo
    }

    fun contrastShunZi(array_1: IntArray, array_2: IntArray): Int {
        var recordValue_1 = 0   //记录顺子第一个值
        var recordValue_2 = 0


        for ((i, v) in array_1.withIndex()) {       //把A设置成14
            if (array_1[i] == 1) array_1[i] = 14
            if (array_2[i] == 1) array_2[i] = 14
        }
        var boo = 0

        for ((i, v) in array_1.withIndex()) {
            recordValue_1 = recordValue_1.times(v)
            recordValue_2 = recordValue_2.times(array_2[i])
        }

        if (recordValue_1 > recordValue_2) {
            boo = 1
        } else if (recordValue_1 < recordValue_2) {
            boo = -1
        } else if (recordValue_1 == recordValue_2) {
            boo = 0
        }
        return boo
    }

    fun checkTongHua(array: IntArray): Boolean {
        var boo: Boolean = false
        if (SszUtil.getCardColor(array).toSet().size == 1) boo = true
        return boo
    }

    fun contrastTongHua(array_1: IntArray, array_2: IntArray, position: Int = -1, j: Int, di: Int): Int = singleCard(array_1, array_2, position, j, di)

    fun checkHuLu(nowArray: IntArray): Boolean {
        var boo: Boolean = false

        if (checkSanTiao(nowArray) and checkDuiZi(nowArray)) boo = true
        return boo
    }

    fun contrastHuLu(array_1: IntArray, array_2: IntArray, dun: Int = -1, position: Int = -1, j: Int, di: Int): Int {
        var recordValueSan_1 = 0   //记录葫芦中的三条
        var recordValueSan_2 = 0

        var recordValueDui_1 = 0   //记录葫芦中的对子
        var recordValueDui_2 = 0
        var boo = 0

        var countMap_1 = getCountMap(array_1)
        var countMap_2 = getCountMap(array_2)

        for ((k, v) in countMap_1) {
            if (v == 2) recordValueDui_1 = k
            if (v == 3) recordValueSan_1 = k

        }

        for ((k, v) in countMap_2) {
            if (v == 2) recordValueDui_2 = k
            if (v == 3) recordValueSan_2 = k

        }

        if (recordValueSan_1 == 1) recordValueSan_1 = 14
        if (recordValueSan_2 == 1) recordValueSan_2 = 14
        if (recordValueDui_1 == 1) recordValueDui_1 = 14
        if (recordValueDui_2 == 1) recordValueDui_2 = 14

        if (recordValueSan_1 > recordValueSan_2) {
            boo = 1
            if (position != -1 && dun == 2) {//中墩葫芦加2分 且胜
                bonusPoint(position, 2, j, di)
            }
        } else if (recordValueSan_1 < recordValueSan_2) {
            boo = -1
        } else if (recordValueSan_1 == recordValueSan_2) {  //如果三条相同，就进行对子的比较
            if (recordValueDui_1 > recordValueDui_2) {
                boo = 1
                if (position != -1 && dun == 2) {//中墩葫芦加2分 且胜
                    bonusPoint(position, 2, j, di)
                }
            } else if (recordValueDui_1 < recordValueDui_2) boo = -1
            else if (recordValueDui_1 == recordValueDui_2) boo = singleCard(intArrayOf(recordValueDui_1), intArrayOf(recordValueDui_1), position, j, di)
        }
        return boo
    }

    fun checkSiMei(array: IntArray): Boolean {
        var boo: Boolean = false

        if (array.toSet().size == 2) boo = true

        return boo
    }

    fun contrastSiMei(array_1: IntArray, array_2: IntArray, position: Int = -1, j: Int = -1, di: Int): Int {
        var recordValue_1 = 0   //记录四梅的值
        var recordValue_2 = 0
        var singleValue_1 = 0   //记录单牌的值
        var singleValue_2 = 0
        var boo = 0

        var countMap_1 = getCountMap(array_1)
        var countMap_2 = getCountMap(array_2)

        for ((k, v) in countMap_1) {    //获得值
            if (v == 4) recordValue_1 = k
            if (v == 1) singleValue_1 = k
        }

        for ((k, v) in countMap_2) {
            if (v == 4) recordValue_2 = k
            if (v == 1) recordValue_2 = k
        }

        //如果判断到A，就直接设置成14
        if (recordValue_1 == 1) recordValue_1 = 14
        if (recordValue_2 == 1) recordValue_2 = 14

        if (recordValue_1 > recordValue_2) {
            boo = 1
            if (position != -1) {
                bonusPoint(position, 4, j, di)//出现四梅自己加四分
                subtraction(j, 4)//对手扣四分
            }
        } else if (recordValue_1 < recordValue_2) boo = -1
        else if (recordValue_1 == recordValue_2) {
            boo = singleCard(intArrayOf(singleValue_1), intArrayOf(singleValue_2), position, j, di) //-1位置会被过滤掉
        }

        return boo
    }

    fun checkTongHuaShun(array: IntArray): Boolean {
        var boo: Boolean = false
        if (checkTongHua(array) and checkShunZi(array)) boo = true
        return boo
    }

    fun contrastTongHuaShun(array_1: IntArray, array_2: IntArray, position: Int = -1, j: Int, di: Int): Int {
        var reValue = contrastShunZi(array_1, array_2)

        if (reValue == 1 && position != -1) {
            bonusPoint(position, 5, j, di)
            subtraction(position, 5)
        }

        return reValue
    }

    fun singleCard(filterArray_1: IntArray, filterArray_2: IntArray, position: Int, j: Int, di: Int): Int {
        var boo = 0

        for ((i, v) in filterArray_1.withIndex()) {       //把A设置成14
            if (filterArray_1.get(i) == 1) filterArray_1[i] = 14
            if (filterArray_2.get(i) == 1) filterArray_2[i] = 14
        }

        filterArray_1.sortDescending()
        filterArray_2.sortDescending()

        for (i in 0..filterArray_1.size - 1) {
            if (filterArray_1[i] > filterArray_2[i]) {
                boo = 1
                bonusPoint(position, 1, j, di)
                break
            } else if (filterArray_1[i] < filterArray_2[i]) {
                boo = -1
                subtraction(position, 1)
                break
            } else if (filterArray_1[i] == filterArray_2[i]) {  //如果第一个最大的值相等，则跳出循环
                if (filterArray_1.size - 1 == i) {  //如果最后一个值还是相等的，则返回0
                    boo = 0
                    break
                }
                continue
            }

        }
        return boo
    }

    fun getCountMap(array: IntArray): HashMap<Int, Int> {
        var countMap = HashMap<Int, Int>()
        for ((i, v) in array.withIndex()) {
            if (countMap.get(v) == null) {
                countMap.put(v, 1)
            } else {
                var c = countMap.get(v) as Int
                countMap[v] = c + 1;
            }
        }
        return countMap
    }

}