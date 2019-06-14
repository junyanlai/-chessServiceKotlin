package com.shine.controller.poker.ssz.tool

import com.shine.controller.poker.ssz.CardObj

object autoPlayCards {

    /**
     * 把随机生成的牌按照规则排序
     */
    fun aiCard(array: IntArray): StringBuilder {
        array.sort()        //排序
        var list = array.toMutableList()

        var list_1: MutableList<CardObj> = ArrayList()
        var list_2: MutableList<CardObj> = ArrayList()

        var initArray_5 = intArrayOf(0, 0, 0, 0, 0)
        var initArray_3 = intArrayOf(0, 0, 0)

        var threeObj = CardObj(initArray_5, initArray_5, 0)
        var twoObj = CardObj(initArray_5, initArray_5, 0)
        var oneObj = CardObj(initArray_3, initArray_3, 0)

        list_1 = fiveCard(list.toIntArray())    //获得后敦
        threeObj = getMaxCard(list_1)
        for ((i, v) in threeObj.OldValue.withIndex()) {
            if (list.contains(v)) {
                list.remove(v)
            }
        }
        list_2 = fiveCard(list.toIntArray())        //获得中墩
        twoObj = getMaxCard(list_2)
        for ((i, v) in twoObj.OldValue.withIndex()) {
            if (list.contains(v)) {
                list.remove(v)
            }
        }
        oneObj.Card = list.toIntArray()         //前敦 不再判断组合，因为就剩下三张牌
        oneObj.OldValue = list.toIntArray()     //这里存在一个BUG，我事先声明

        var resultArray = IntArray(13)
        for (i in 0..2) {
            resultArray[i] = oneObj.OldValue[i]
        }

        for (i in 3..7) {
            resultArray[i] = twoObj.OldValue[i - 3]
        }

        for (i in 8..12) {
            resultArray[i] = threeObj.OldValue[i - 8]
        }

        var resultString = StringBuilder()

        for ((i, v) in resultArray.withIndex()) {
            if (i <= 11) {
                resultString = resultString.append(v).append(",")
            } else {
                resultString = resultString.append(v)
            }
        }

        return resultString
    }



    /**
     * 获得最大的牌
     */
    fun getMaxCard(cardObjList: MutableList<CardObj>): CardObj {
        var initArray = intArrayOf(0, 0, 0, 0, 0)
        var initObj = CardObj(initArray, initArray, 0)     //初始化cardObj
        var count = -1
        for ((i, v) in cardObjList.withIndex()) {
            for ((j, v) in cardObjList.withIndex()) {

                if (i == j) continue
                count++
                if (cardObjList.get(i).Boo > cardObjList.get(j).Boo) {
                    if (count >= 1) initObj =contrastObj(initObj, cardObjList.get(i)) //对比两个对象，判断是否保留原值
                    else initObj = cardObjList.get(i)                               //第一次赋初始化
                    continue
                } else if (cardObjList.get(i).Boo < cardObjList.get(j).Boo) {

                    if (count >= 1) initObj = contrastObj(initObj, cardObjList.get(j))
                    else initObj = cardObjList.get(j)
                    continue
                } else if (cardObjList.get(i).Boo == cardObjList.get(j).Boo) { //如果两个对象boo相等
                    if (cardObjList.get(i).Boo == 0) {      //散牌
                        var value = singleCard(cardObjList.get(i).Card, cardObjList.get(j).Card)
                        if (count >= 1) initObj = valueToGetObj(value, initObj, cardObjList.get(i), cardObjList.get(j), 0)     //判断选择那个值
                        else initObj = valueToGetObj(value, initObj, cardObjList.get(i), cardObjList.get(j), 1)
                    } else if (cardObjList.get(i).Boo == 1) {
                        var value = contrastDuiZi(cardObjList.get(i).Card, cardObjList.get(j).Card)   //对子
                        if (count >= 1) initObj =valueToGetObj(value, initObj, cardObjList.get(i), cardObjList.get(j), 0)
                        else initObj = valueToGetObj(value, initObj, cardObjList.get(i), cardObjList.get(j), 1)
                    } else if (cardObjList.get(i).Boo == 2) {
                        var value = contrastSanTiao(cardObjList.get(i).Card, cardObjList.get(j).Card) //三条
                        if (count >= 1) initObj = valueToGetObj(value, initObj, cardObjList.get(i), cardObjList.get(j), 0)
                        else initObj = valueToGetObj(value, initObj, cardObjList.get(i), cardObjList.get(j), 1)
                    } else if (cardObjList.get(i).Boo == 3) {
                        var value =contrastShunZi(cardObjList.get(i).Card, cardObjList.get(j).Card)  //顺子
                        if (count >= 1) initObj =valueToGetObj(value, initObj, cardObjList.get(i), cardObjList.get(j), 0)
                        else initObj = valueToGetObj(value, initObj, cardObjList.get(i), cardObjList.get(j), 1)
                    } else if (cardObjList.get(i).Boo == 4) {
                        var value = contrastTongHua(cardObjList.get(i).Card, cardObjList.get(j).Card) //同花
                        if (count >= 1) initObj =valueToGetObj(value, initObj, cardObjList.get(i), cardObjList.get(j), 0)
                        else initObj =valueToGetObj(value, initObj, cardObjList.get(i), cardObjList.get(j), 1)
                    } else if (cardObjList.get(i).Boo == 5) {
                        var value =contrastHuLu(cardObjList.get(i).Card, cardObjList.get(j).Card)    //葫芦
                        if (count >= 1) initObj =valueToGetObj(value, initObj, cardObjList.get(i), cardObjList.get(j), 0)
                        else initObj =valueToGetObj(value, initObj, cardObjList.get(i), cardObjList.get(j), 1)
                    } else if (cardObjList.get(i).Boo == 6) {
                        var value = contrastSiMei(cardObjList.get(i).Card, cardObjList.get(j).Card) //四梅
                        if (count >= 1) initObj =valueToGetObj(value, initObj, cardObjList.get(i), cardObjList.get(j), 0)
                        else initObj = valueToGetObj(value, initObj, cardObjList.get(i), cardObjList.get(j), 1)
                    } else if (cardObjList.get(i).Boo == 7) {
                        var value = contrastTongHuaShun(cardObjList.get(i).Card, cardObjList.get(j).Card)   //同花顺
                        if (count >= 1) initObj =valueToGetObj(value, initObj, cardObjList.get(i), cardObjList.get(j), 0)
                        else initObj = valueToGetObj(value, initObj, cardObjList.get(i), cardObjList.get(j), 1)
                    }
                }

            }
        }
        return initObj
    }

    /**
     * 计算五张牌的全部组合
     */
    fun fiveCard(array: IntArray): MutableList<CardObj> {
        var list: MutableList<CardObj> = ArrayList()
        for (i in 0..array.size - 5) {
            var arr = intArrayOf(array[i], array[i + 1], array[i + 2], array[i + 3], array[i + 4])
            list.add(SszUtil.getCardObj(arr))
        }
        return list
    }


    /***
     * 对比两obj和obj的大小
     */
    fun contrastObj(obj_1: CardObj, obj_2: CardObj): CardObj {
        var intiArray = intArrayOf(0, 0, 0)
        var InitObj = CardObj(intiArray, intiArray, 0)
        if (obj_1.Boo > obj_2.Boo) {
            InitObj = obj_1
        } else if (obj_1.Boo < obj_2.Boo) {
            InitObj = obj_2
        } else if (obj_1.Boo == obj_2.Boo) {

            if (obj_1.Boo == 0) {
                var reInt = singleCard(obj_1.Card, obj_2.Card) //-1位置会被过滤掉
                InitObj = valueToPostObje(reInt, InitObj, obj_1, obj_2)
            }

            if (obj_1.Boo == 1) {
                var reInt =contrastDuiZi(obj_1.Card, obj_2.Card)
                InitObj = valueToPostObje(reInt, InitObj, obj_1, obj_2)
            }

            if (obj_1.Boo == 2) {
                var reInt = contrastSanTiao(obj_1.Card, obj_2.Card)
                InitObj = valueToPostObje(reInt, InitObj, obj_1, obj_2)
            }

            if (obj_1.Boo == 3) {
                var reInt = contrastShunZi(obj_1.Card, obj_2.Card)
                InitObj =valueToPostObje(reInt, InitObj, obj_1, obj_2)
            }

            if (obj_1.Boo == 4) {
                var reInt = contrastTongHua(obj_1.Card, obj_2.Card)
                InitObj = valueToPostObje(reInt, InitObj, obj_1, obj_2)
            }

            if (obj_1.Boo == 5) {
                var reInt = contrastHuLu(obj_1.Card, obj_2.Card)
                InitObj = valueToPostObje(reInt, InitObj, obj_1, obj_2)
            }

            if (obj_1.Boo == 6) {
                var reInt = contrastSiMei(obj_1.Card, obj_2.Card)
                InitObj = valueToPostObje(reInt, InitObj, obj_1, obj_2)
            }

            if (obj_1.Boo == 7) {
                var reInt = contrastTongHuaShun(obj_1.Card, obj_2.Card)
                InitObj = valueToPostObje(reInt, InitObj, obj_1, obj_2)
            }
        }
        return InitObj
    }


    /**
     * 对比两个三条的大小
     */
    fun contrastSanTiao(array_1: IntArray, array_2: IntArray): Int {
        var recordValue_1 = 0   //记录三条相等的值
        var recordValue_2 = 0
        var boo = 0

        for ((i, v) in array_1.withIndex()) {       //把A设置成14
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
        } else if (recordValue_1 < recordValue_2) boo = -1
        else if (recordValue_1 == recordValue_2) {
            boo = singleCard(filterArray_1.toIntArray(), filterArray_2.toIntArray())   //降序 1位置会被过滤掉
        }
        return boo
    }


    /**
     * 对比两个顺子的大小
     */
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
    /**
     * 对比两个四梅的大小
     */
    fun contrastSiMei(array_1: IntArray, array_2: IntArray): Int {
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
        } else if (recordValue_1 < recordValue_2) boo = -1
        else if (recordValue_1 == recordValue_2) {
            boo =singleCard(intArrayOf(singleValue_1), intArrayOf(singleValue_2)) //-1位置会被过滤掉
        }

        return boo
    }


    /**
     * 对比两个同花的大小（）
     */
    fun contrastTongHua(array_1: IntArray, array_2: IntArray): Int =singleCard(array_1, array_2)//-1位置会被过滤掉

    /**
     * 对比两个葫芦的大小 （position：玩家的位置，dun：第几敦 默认-1 无实际意义）
     */
    fun contrastHuLu(array_1: IntArray, array_2: IntArray): Int {
        var recordValueSan_1 = 0   //记录葫芦中的三条
        var recordValueSan_2 = 0

        var recordValueDui_1 = 0   //记录葫芦中的对子
        var recordValueDui_2 = 0
        var boo = 0

        var countMap_1 =getCountMap(array_1)
        var countMap_2 =getCountMap(array_2)

        for ((k, v) in countMap_1) {
            if (v == 2) recordValueDui_1 = k
            if (v == 3) recordValueSan_1 = k

        }

        for ((k, v) in countMap_2) {
            if (v == 2) recordValueDui_2 = k
            if (v == 3) recordValueSan_2 = k

        }

        //如果判断到A，就直接设置成14
        if (recordValueSan_1 == 1) recordValueSan_1 = 14
        if (recordValueSan_2 == 1) recordValueSan_2 = 14
        if (recordValueDui_1 == 1) recordValueDui_1 = 14
        if (recordValueDui_2 == 1) recordValueDui_2 = 14

        if (recordValueSan_1 > recordValueSan_2) {
            boo = 1
        } else if (recordValueSan_1 < recordValueSan_2) {
            boo = -1
        } else if (recordValueSan_1 == recordValueSan_2) {  //如果三条相同，就进行对子的比较
            if (recordValueDui_1 > recordValueDui_2) {
                boo = 1

            } else if (recordValueDui_1 < recordValueDui_2) boo = -1
            else if (recordValueDui_1 == recordValueDui_2) boo =singleCard(intArrayOf(recordValueDui_1), intArrayOf(recordValueDui_1)) //-1位置会被过滤掉
        }
        return boo
    }


    /**
     * 单牌比对  position:记录的是几号玩家
     */
    fun singleCard(filterArray_1: IntArray, filterArray_2: IntArray): Int {
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
                break
            } else if (filterArray_1[i] < filterArray_2[i]) {
                boo = -1
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

    /**
     * 对比两个同花顺的大小
     */
    fun contrastTongHuaShun(array_1: IntArray, array_2: IntArray): Int {
        var reValue = contrastShunZi(array_1, array_2)
        return reValue
    }

    /**
     * 根据value来判断选择那个值（1：obj_i 0：obj_i -1：obj_）
     */
    fun valueToGetObj(value: Int, initObj: CardObj, obj_i: CardObj, obj_j: CardObj, boo: Int): CardObj {
        var resultObj = initObj
        if (value == 1) resultObj = contrastObj(initObj, obj_i)
        else if (value == -1) resultObj = contrastObj(initObj, obj_j)
        else if (value == 0) resultObj = contrastObj(initObj, obj_i)
        if (boo == 1) {
            resultObj = contrastObj(initObj, resultObj)     //为了防止值被覆盖，当boo=1的时候 obj_1和2比完之后 要重新和initObj比较
        }
        return resultObj
    }

    /**
     * 对你没看错
     */
    fun valueToPostObje(value: Int, initObj: CardObj, obj_1: CardObj, obj_2: CardObj): CardObj {
        var resultObj = initObj
        if (value == 1) resultObj = obj_1
        else if (value == -1) resultObj = obj_2
        else if (value == 0) resultObj = obj_1
        return resultObj
    }

    /**
     * 对比两个对子的大小（是否会出现2个对子的情况，比最大的那个）
     */
    fun contrastDuiZi(array_1: IntArray, array_2: IntArray): Int {
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
            boo =singleCard(filterArray_1.toIntArray(), filterArray_2.toIntArray())   //降序 -1位置会被过滤掉
        }
        return boo
    }


    /**
     * 把array 里面的值，计算count保存在map里面
     */
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