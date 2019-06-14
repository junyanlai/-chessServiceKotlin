package com.shine.controller.poker.maj

import com.shine.controller.aHall.Hall


class Multiple {

    //Algorithm of Hu
    //main
    fun LegalHu(card: Int, cardArr: IntArray): Boolean {

        val arr = cardArr.clone()
        val il = IndexLive(arr)
        val index = il + 1
        val len = index + 1

        if (il == 0 && card == arr[0]) return true

        arr[index] = card
        Sort(arr, index)

        val jiangs = getJia(arr, len)
        val qujiangArrs = quJiangArrs(arr, jiangs, len)
        val ll = il   //ll=len-2=il

        for (qujiang in qujiangArrs) {
            val weis = getWeis(qujiang, ll)
            val hu = weisThrough(weis)
            if (hu) return true
        }
        return false
    }
    fun HuLegal(cardArr: IntArray): Boolean {
        val cardClone = cardArr.clone()
        val il = IndexLive(cardClone)
        val card = cardArr[il]
        cardClone[il] = 0x00
        return LegalHu(card, cardClone)
    }

    //step
    fun Len(wei: Int) = wei.toString().length
    fun miIC(di: Int, mi: Int): Int {
        if (di == 0) return 1
        if (mi < 0) return 0
        if (mi == 0) return di
        var r = di
        for (i in 1..mi)
            r *= 10
        return r
    } //mi increase
    fun carry(key: Int): Int {
        when (key) {
            3 -> return 3
            4 -> return 3

            31 -> return 30
            32 -> return 30
            33 -> return 33
            44 -> return 33

            111 -> return 111
            112 -> return 111
            113 -> return 111
            114 -> return 114

            122 -> return 111
            123 -> return 111
            124 -> return 111

            133 -> return 111
            134 -> return 111

            141 -> return 141
            142 -> return 141
            143 -> return 141
            144 -> return 144

            222 -> return 222
            223 -> return 222
            224 -> return 222

            233 -> return 222
            234 -> return 222
            244 -> return 222

            311 -> return 300
            312 -> return 300
            313 -> return 300
            314 -> return 300

            322 -> return 300
            323 -> return 300
            324 -> return 300

            331 -> return 330
            332 -> return 330
            333 -> return 333
            334 -> return 333

            341 -> return 330
            342 -> return 330
            343 -> return 330
            344 -> return 333

            411 -> return 411
            412 -> return 411
            413 -> return 411
            414 -> return 414

            422 -> return 411
            423 -> return 411
            424 -> return 411

            433 -> return 411
            434 -> return 411

            441 -> return 441
            442 -> return 441
            443 -> return 441
            444 -> return 444
        }
        return 0
    }
    fun carryChange(weiA: Int): Int {

        val lenW = Len(weiA)                                //length_wei
        for (lenk in 3 downTo 1) {                           //length_key
            if (lenW - lenk < 0) continue                    //carry keystart with 3key,if con't carry,change to 2key,1key. else return itself
            var weiH = (weiA / miIC(1, lenW - lenk))  //weiHead
            if (carry(weiH) != 0)
                return weiA - miIC(carry(weiH), (lenW - lenk))
        }
        return weiA
    }
    fun weiThrough(wei: Int): Boolean {
        //circle carryChange until result==0,else return fase
        var cache = wei
        for (i in 1..9) {
            cache = carryChange(cache)
            if (cache == 0) return true
        }
        return false
    }
    fun weisThrough(weis: List<Int>): Boolean {
        for (wei in weis)
            if (!weiThrough(wei))
                return false
        return true
    }
    fun getWei(breakS: Int, breakE: Int, cardArr: IntArray): Int {
        //breakS is index of break start,E is end
        var wei = 1
        for (i in (breakE - 2) downTo breakS) {
            if (cardArr[i] != cardArr[i + 1])
                wei += miIC(1, Len(wei))
            else if (cardArr[i] == cardArr[i + 1])
                wei += miIC(1, Len(wei) - 1)
        }
        return wei
    }
    fun getBreaks(cardArr: IntArray, len: Int): List<Int> {

        val breaks = MutableList<Int>(1, { 0 })
        for (i in 1 until len)
            if (cardArr[i] - cardArr[i - 1] > 1)
                breaks.add(i)
        breaks.add(len)

        return breaks
    }
    fun getWeis(cardArr: IntArray, len: Int): List<Int> {

        val weis = MutableList<Int>(0, { 0 })
        val breaks = getBreaks(cardArr, len)
        for (i in 0..(breaks.size - 2)) {
            val wei = getWei(breaks[i], breaks[i + 1], cardArr)
            weis.add(wei)
        }
        return weis
    }
    fun getJia(cardArr: IntArray, len: Int): IntArray {
        //all jiangs in it's first index,same size as cardArr
        var flag = true
        val jiangs = IntArray(len)
        for (i in 1 until len) {
            if (cardArr[i] == cardArr[i - 1]) {
                if (flag) jiangs[i - 1] = cardArr[i]
                flag = false
            } else flag = true
        }
        return jiangs
    }
    fun quJiangArrs(cardArr: IntArray, jiangs: IntArray, len: Int): List<IntArray> {
        //all (cardArr[]-jiang) in List
        val quJiangs = MutableList(0, { IntArray(len, { 0 }) })
        for (i in 0 until len)
            if (jiangs[i] != 0) {
                val qujiang = cardArr.clone()
                qujiang[i] = 0
                qujiang[i + 1] = 0
                this.SortL(qujiang, len)
                quJiangs.add(qujiang)
            }
        return quJiangs
    }


    //dependent
    val MASK_COLOR = 0xF0
    val MASK_VALUE = 0x0F
    fun Value(card: Int) = card and MASK_VALUE
    fun Color(card: Int) = card and MASK_COLOR
    fun SV(card: Int) = if (card == 0x00) 0x0A else Value(card)    //getValueSort
    fun SC(card: Int) = if (card == 0x00) 0x50 else Color(card)    //GetColorSort
    fun Sort(cardArr: IntArray, index: Int) {
        for (i in 0..index - 1)
            for (j in 0..(index - 1 - i)) {

                val a = cardArr[j];
                val b = cardArr[j + 1];
                val cd = SC(a) > SC(b)//colorDecrease
                val ce = SC(a) == SC(b)//color Equal
                val vd = SV(a) > SV(b)//valueDecrease

                if (cd) {
                    cardArr[j] = b
                    cardArr[j + 1] = a
                }
                if (ce && vd) {
                    cardArr[j] = b
                    cardArr[j + 1] = a
                }
            }
    }   //index=21
    fun SortL(cardArr: IntArray, size: Int) {//FCLV0E
        for (i in 0..(size - 1 - 1))
            for (j in 0..(size - 1 - 1 - i)) {

                val a = cardArr[j];
                val b = cardArr[j + 1];
                val cd = SC(a) > SC(b)//colorDecrease
                val ce = SC(a) == SC(b)//color Equal
                val vd = SV(a) > SV(b)//valueDecrease

                if (cd) {
                    cardArr[j] = b
                    cardArr[j + 1] = a
                }
                if (ce && vd) {
                    cardArr[j] = b
                    cardArr[j + 1] = a
                }
            }
    }   //size=22
    fun IndexLive(cardArr: IntArray): Int {
        for (i in 0..21)
            if (cardArr[i] == 0x00)
                return i - 1
        return 22
    }
    fun IndexLive(cardArr: IntArray, len: Int): Int {
        for (i in 0 until len)
            if (cardArr[i] == 0x00) return i
        return len
    }
    fun IndexDead(cardArr: IntArray): Int {
        for (i in 21 downTo 0)
            if (cardArr[i] == 0x00)
                return i + 1
        return -1
    }
    fun IndexPointF(card: Int, cardArr: IntArray): Int {
        for (i in 0..21)
            if (cardArr[i] == card)
                return i - 1
        return 22
    }




    val cardKey = intArrayOf(
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
            0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19,
            0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29,
            0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37)
    val anke5 = intArrayOf(333332, 333323, 333233, 332333, 323333, 233333)
    val angang3 = intArrayOf(
            4442, 34442, 43442, 44342, 44432,
            44423, 4424, 34424, 43424,
            44324, 44234, 44243, 4244,
            34244, 43244, 42344, 42434,
            42443, 2444, 32444, 23444,
            24344, 24434, 24443,
            1114442, 4111442, 4411142, 444113,
            444131, 444311, 1114424, 4111424,
            4424111, 441134, 441314, 443114,
            1114244, 4241114, 4244111, 411344,
            413144, 431144, 2411144, 2441114,
            2444111, 113444, 131444, 311444)
    val listCQDY=Hall.listCQDY
    val listHQDY=Hall.listHQDY

    var weilive = 0
    var weisort = 0

    var numFlower = 0
    val counts = IntArray(0x38)
    val countl = IntArray(0x38)

    var ziyise = true
    var hunyise = true
    var qingyise = true

    val Multiple = MutableList(0, { "" })


    fun main(cardArr: IntArray, cardFlower: IntArray): MutableList<String> {

        val card = cardArr.clone()
        val cardSort = cardArr.clone();Sort(cardSort, 21)
        val il = IndexLive(card);Sort(card, il)          //index-live
        val ils = IndexLive(cardSort)     //index-live-sort

        weilive = getWei(0, il + 1, card)
        weisort = getWei(0, ils + 1, cardSort)

        Clear()

        for (i in 0..il) countl[card[i]]++
        for (i in 0..ils) counts[cardSort[i]]++

        xiyuan(cardSort, ils)            //四喜、三元、三元台
        yise(cardSort, ils)              // 清,字,混一色
        hua(cardSort, ils, cardFlower)    //花、八仙过海、无字无花
        laotouqing(cardSort, ils)        //清老头、断幺九
        laotouhun(cardSort, ils)         //混老头

        sansetongke(cardSort, ils)       //三色同刻
        sansetongshun(cardSort, ils)     //三色同顺
        yiqitongguan(cardSort, ils)      //一气通贯
        lianke3(cardSort, ils)           //一色三顺，三连刻
        lianke4(cardSort, ils)           //四连刻
        lianke5()                        //五连刻

        if (anke5.contains(weisort))
            Multiple.add("pengpenghu")  //碰碰胡
        if (angang3.contains(weilive))
            Multiple.add("3angang")     //三暗杠
        if (anke5.contains(weilive))
            //println("5anke")            //五暗刻
        anke34(card, il)                //四暗刻，三暗刻
        quandaiyao(cardSort, ils)        //纯全带幺，混全带幺

        return Multiple
    }

    fun xiyuan(cardSort: IntArray, ils: Int) {

        val count = IntArray(0x38)
        for (i in 0..ils) count[cardSort[i]]++

        if (count[0x31] > 2 &&
                count[0x32] > 2 &&
                count[0x33] > 2 &&
                count[0x34] > 2)
            Multiple.add("da4xi")
        if (count[0x35] > 2 &&
                count[0x36] > 2 &&
                count[0x37] > 2)
            Multiple.add("da3yuan")
        if (count[0x31] == 2 && count[0x32] > 2 && count[0x33] > 2 && count[0x34] > 2 ||
                count[0x31] > 2 && count[0x32] == 2 && count[0x33] > 2 && count[0x34] > 2 ||
                count[0x31] > 2 && count[0x32] > 2 && count[0x33] == 2 && count[0x34] > 2 ||
                count[0x31] > 2 && count[0x32] > 2 && count[0x33] > 2 && count[0x34] == 2)
            Multiple.add("xiao4xi")
        if (count[0x35] == 2 && count[0x36] > 2 && count[0x37] > 2 ||
                count[0x35] > 2 && count[0x36] == 2 && count[0x37] > 2 ||
                count[0x35] > 2 && count[0x36] > 2 && count[0x37] == 2)
            Multiple.add("xiao3yuan")
        if (count[0x35] > 2 || count[0x36] > 2 || count[0x37] > 2) Multiple.add("3yuantai")
    }

    fun yise(cardSort: IntArray, ils: Int) {

        val color = Color(cardSort[0])
        if (color > 0x20) qingyise = false

        for (i in 0..ils) {
            if (cardSort[i] < 0x29) ziyise = false
            if (cardSort[i] > 0x29) qingyise = false
            if (Color(cardSort[i]) != color) qingyise = false
        }
        for (i in 0..ils) {
            if (cardSort[i] > 0x29) break
            if (!qingyise && Color(cardSort[i]) != color) hunyise = false
        }

        if (ziyise) Multiple.add("zi1se")
        if (hunyise) Multiple.add("hun1se")
        if (qingyise) Multiple.add("qing1se")
    }

    fun hua(cardSort: IntArray, ils: Int, cardFlower: IntArray) {

        numFlower = IndexLive(cardFlower, 8)
        if (numFlower in 1..7) Multiple.add("hua" + numFlower)
        if (numFlower == 8) Multiple.add("8xianguohai")

        var wuzi = true
        for (i in 0..ils)
            if (cardSort[i] > 0x29)
                wuzi = false
        if (numFlower == 0 && wuzi) Multiple.add("wuziwuhua")
    }

    fun laotouqing(cardSort: IntArray, ils: Int) {

        if (!qingyise) return
        val valueqing = IntArray(10)
        for (i in 0..ils)
            valueqing[Value(cardSort[i])]++

        if (valueqing[8] == 0 &&
                valueqing[2] == 0 && valueqing[3] == 0 &&
                valueqing[4] == 0 && valueqing[5] == 0 &&
                valueqing[6] == 0 && valueqing[7] == 0)
            Multiple.add("qinglaotou")
        else Multiple.add("duanyao9")
    }

    fun laotouhun(cardSort: IntArray, ils: Int) {

        if (qingyise) return
        val value = IntArray(10)
        for (i in 0..ils)
            value[Value(cardSort[i])]++

        if (value[8] == 0 &&
                value[2] == 0 && value[3] == 0 &&
                value[4] == 0 && value[5] == 0 &&
                value[6] == 0 && value[7] == 0)
            Multiple.add("hunlaotou")
    }

    fun sansetongke(cardSort: IntArray, ils: Int) {

        val kes = MutableList(0, { 0 })
        for (i in 1..9)
            if (counts[i] > 2 && counts[0x10 + i] > 2 && counts[0x20 + i] > 2)
                kes.add(i)
        if (kes.size == 0) return

        for (ke in kes)
            if (qukehu(ke, cardSort, ils)) {
                Multiple.add("3setongke")
                return
            }
    }

    fun sansetongshun(cardSort: IntArray, ils: Int) {
        val shuns = MutableList(0, { 0 })
        for (i in 1..7) {
            if (counts[i] > 0 && counts[i + 1] > 0 && counts[i + 2] > 0 &&
                    counts[i + 0x10] > 0 && counts[i + 0x11] > 0 && counts[i + 0x12] > 0 &&
                    counts[i + 0x20] > 0 && counts[i + 0x21] > 0 && counts[i + 0x22] > 0)
                shuns.add(i)
        }
        if (shuns.size == 0) return

        for (shun in shuns)
            if (qushunhu(shun, cardSort, ils)) {
                Multiple.add("3setongshun")
                return
            }
    }

    fun yiqitongguan(cardSort: IntArray, ils: Int) {
        var qi = 3
        if (counts[0x01] > 0 && counts[0x02] > 0 && counts[0x03] > 0 &&
                counts[0x04] > 0 && counts[0x05] > 0 && counts[0x06] > 0 &&
                counts[0x07] > 0 && counts[0x08] > 0 && counts[0x09] > 0)
            qi = 0
        if (counts[0x11] > 0 && counts[0x12] > 0 && counts[0x13] > 0 &&
                counts[0x14] > 0 && counts[0x15] > 0 && counts[0x16] > 0 &&
                counts[0x17] > 0 && counts[0x18] > 0 && counts[0x19] > 0)
            qi = 1
        if (counts[0x21] > 0 && counts[0x22] > 0 && counts[0x23] > 0 &&
                counts[0x24] > 0 && counts[0x25] > 0 && counts[0x26] > 0 &&
                counts[0x27] > 0 && counts[0x28] > 0 && counts[0x29] > 0)
            qi = 2

        if (qi == 3) return
        if (quqihu(qi, cardSort, ils)) Multiple.add("1qitongguan")
    }

    fun lianke3(cardSort: IntArray, ils: Int) {

        val ses = MutableList(0, { 0 })
        for (i in 0..2)
            for (j in 1..7) {
                if (counts[0x10 * i + j] > 2 &&
                        counts[0x10 * i + j + 1] > 2 &&
                        counts[0x10 * i + j + 2] > 2)
                    ses.add(0x10 * i + j)
            }

        if (ses.size == 0) return
        for (se in ses)
            if (qusehu(se, cardSort, ils)) {
                Multiple.add("1sesanshun")
                return
            }
    }

    fun lianke4(cardSort: IntArray, ils: Int) {
        var lianke4s = MutableList(0, { 0 })
        for (i in 0..2)
            for (j in 0x01..6) {
                if (counts[0x10 * i + j] > 2 &&
                        counts[0x10 * i + j + 1] > 2 &&
                        counts[0x10 * i + j + 2] > 2 &&
                        counts[0x10 * i + j + 3] > 2)

                    lianke4s.add(0x10 * i + j)
            }
        if (lianke4s.size == 0) return

        for (ke in lianke4s)
            if (qu4liankehu(ke, cardSort, ils)) {
                Multiple.add("4lianke")
                return
            }
    }

    fun lianke5() {

        var lianke5 = false
        for (i in 0..2)
            for (j in 0x01..6)
                if (counts[0x10 * i + j] > 2 &&
                        counts[0x10 * i + j + 1] > 2 &&
                        counts[0x10 * i + j + 2] > 2 &&
                        counts[0x10 * i + j + 3] > 2 &&
                        counts[0x10 * i + j + 4] > 2)
                    lianke5 = true

        if (!lianke5) return

        if (anke5.contains(weisort))
            Multiple.add("5lianke")
    }

    fun anke34(cardHand: IntArray, il: Int) {

        val kes = IntArray(5)
        var i = 1
        var j = 0

        while (i < 0x38) {
            if (countl[i] > 2) {
                kes[j] = i
                j++
            }
            i++
        }

        val numke = IndexLive(kes, 5)
        if (numke == 4 && qu4ankehu(kes, cardHand, il))
            Multiple.add("4anke")
        if (numke < 5 && qu3ankehu(kes, cardHand, il))
            Multiple.add("3anke")
    }


    fun qukehu(kei: Int, cardSort: IntArray, ils: Int): Boolean {

        val card = cardSort.clone()
        val colorMark = IntArray(3)

        for (i in 0..ils) {//活牌刻子清零
            if (card[i] == kei && colorMark[0] != 3) {
                card[i] = 0x00
                colorMark[0]++
            }
            if (card[i] == kei + 0x10 && colorMark[1] != 3) {
                card[i] = 0x00
                colorMark[1]++
            }
            if (card[i] == kei + 0x20 && colorMark[2] != 3) {
                card[i] = 0x00
                colorMark[2]++
            }
        }
        Sort(card, ils)
        return HuLegal(card)
    }

    fun qushunhu(shun: Int, cardSort: IntArray, ils: Int): Boolean {

        val card = cardSort.clone()
        val shunMark = BooleanArray(9)
        for (i in 0..ils) {//活牌顺子清零
            if (card[i] == shun && !shunMark[0]) {
                card[i] = 0x00
                shunMark[0] = true
            }
            if (card[i] == shun + 1 && !shunMark[1]) {
                card[i] = 0x00
                shunMark[1] = true
            }
            if (card[i] == shun + 2 && !shunMark[2]) {
                card[i] = 0x00
                shunMark[2] = true
            }
            if (card[i] == shun + 0x10 && !shunMark[3]) {
                card[i] = 0x00
                shunMark[3] = true
            }
            if (card[i] == shun + 0x11 && !shunMark[4]) {
                card[i] = 0x00
                shunMark[4] = true
            }
            if (card[i] == shun + 0x10 && !shunMark[5]) {
                card[i] = 0x00
                shunMark[5] = true
            }
            if (card[i] == shun + 0x20 && !shunMark[6]) {
                card[i] = 0x00
                shunMark[6] = true
            }
            if (card[i] == shun + 0x21 && !shunMark[7]) {
                card[i] = 0x00
                shunMark[7] = true
            }
            if (card[i] == shun + 0x22 && !shunMark[8]) {
                card[i] = 0x00
                shunMark[8] = true
            }
        }
        Sort(card, ils)
        return HuLegal(card)
    }

    fun quqihu(qi: Int, cardSort: IntArray, ils: Int): Boolean {
        val card = cardSort.clone()
        val qiMark = BooleanArray(9)

        for (i in 0..ils) {
            if (card[i] == 0x01 + 0x10 * qi && !qiMark[0]) {
                card[i] = 0x00
                qiMark[0] = true
            }
            if (card[i] == 0x02 + 0x10 * qi && !qiMark[1]) {
                card[i] = 0x00
                qiMark[1] = true
            }
            if (card[i] == 0x03 + 0x10 * qi && !qiMark[2]) {
                card[i] = 0x00
                qiMark[2] = true
            }
            if (card[i] == 0x04 + 0x10 * qi && !qiMark[3]) {
                card[i] = 0x00
                qiMark[3] = true
            }
            if (card[i] == 0x05 + 0x10 * qi && !qiMark[4]) {
                card[i] = 0x00
                qiMark[4] = true
            }
            if (card[i] == 0x06 + 0x10 * qi && !qiMark[5]) {
                card[i] = 0x00
                qiMark[5] = true
            }
            if (card[i] == 0x07 + 0x10 * qi && !qiMark[6]) {
                card[i] = 0x00
                qiMark[6] = true
            }
            if (card[i] == 0x08 + 0x10 * qi && !qiMark[7]) {
                card[i] = 0x00
                qiMark[7] = true
            }
            if (card[i] == 0x09 + 0x10 * qi && !qiMark[8]) {
                card[i] = 0x00
                qiMark[8] = true
            }
        }

        Sort(card, ils)
        return HuLegal(card)
    }

    fun qusehu(se: Int, cardSort: IntArray, ils: Int): Boolean {
        val card = cardSort.clone()
        val mark = IntArray(3)

        for (i in 0..ils) {
            if (card[i] == se && mark[0] < 4) {
                card[i] = 0
                mark[0]++
            }
            if (card[i] == se + 0x01 && mark[1] < 4) {
                card[i] = 0
                mark[1]++
            }
            if (card[i] == se + 0x02 && mark[2] < 4) {
                card[i] = 0
                mark[2]++
            }
        }
        Sort(card, ils)
        return HuLegal(card)
    }

    fun qu4liankehu(ke: Int, cardSort: IntArray, ils: Int): Boolean {

        val card = cardSort.clone()
        val mark = IntArray(4)

        for (i in 0..ils) {
            if (card[i] == ke && mark[0] < 4) {
                card[i] = 0
                mark[0]++
            }
            if (card[i] == ke + 0x01 && mark[1] < 4) {
                card[i] = 0
                mark[1]++
            }
            if (card[i] == ke + 0x02 && mark[2] < 4) {
                card[i] = 0
                mark[2]++
            }
            if (card[i] == ke + 0x03 && mark[3] < 4) {
                card[i] = 0
                mark[3]++
            }
        }

        Sort(card, ils)
        return HuLegal(card)
    }

    fun qu4ankehu(kes: IntArray, cardHand: IntArray, il: Int): Boolean {
        val card = cardHand.clone()
        val mark = IntArray(4)

        for (i in il + 1..21) card[i] = 0
        for (i in 0..il) {
            if (card[i] == kes[0] && mark[0] < 4) {
                card[i] = 0
                mark[0]++
            }
            if (card[i] == kes[1] && mark[1] < 4) {
                card[i] = 0
                mark[1]++
            }
            if (card[i] == kes[2] && mark[2] < 4) {
                card[i] = 0
                mark[2]++
            }
            if (card[i] == kes[3] && mark[3] < 4) {
                card[i] = 0
                mark[3]++
            }
        }

        Sort(card, il)
        return HuLegal(card)
    }

    fun qu3ankehu(kes: IntArray, cardHand: IntArray, il: Int): Boolean {

        val card = cardHand.clone()
        val numke = IndexLive(kes, 5)
        for (i in il + 1..21) card[i] = 0

        if (numke == 3) {
            val mark = IntArray(3)
            for (i in 0..il)
                for(j in 0..2)
                    if (card[i] == kes[j] && mark[j] < 4) {
                        card[i] = 0
                        mark[j]++
                }

            Sort(card, il)
            return HuLegal(card)

        } else if (numke == 4) {

            val kess = Array(4) { IntArray(5) }
            for (i in 0..3) {
                kess[i] = kes.clone()
                kess[i][i] = 0
                return qu4ankehu(kess[i], card, il)
            }
        }
        return false
    }

    fun quandaiyao(cardSort: IntArray, ils: Int){

        val model=getModel(cardSort)
        if (cardSort[ils]<0x2A)
            if (listCQDY.contains(model))
                Multiple.add("chunquandaiyao")

        if (cardSort[ils]>0x30)
            if (listHQDY.contains(model))
                Multiple.add("hunquandaiyao")
    }

    fun turn(c:Int):String{
        when (c) {
            0x00 -> return ""
            0x01 -> return "0"
            0x02 -> return "1"
            0x03 -> return "2"

            0x07 -> return "3"
            0x08 -> return "4"
            0x09 -> return "5"

            0x11 -> return "6"
            0x12 -> return "7"
            0x13 -> return "8"

            0x17 -> return "9"
            0x18 -> return "A"
            0x19 -> return "B"

            0x21 -> return "C"
            0x22 -> return "D"
            0x23 -> return "E"

            0x27 -> return "F"
            0x28 -> return "G"
            0x29 -> return ":"
        }
        return ""
    }
    fun getModel(card:IntArray):String{
        var s=""
        for (c in card)
            if (c < 0x30)
                s += turn(c)
        return s
    }

    fun Clear() {

        Multiple.clear()

        weilive=0
        weisort=0
        numFlower = 0
        qingyise = true
        ziyise = true
        hunyise = true
        for(i in 0..0x37) {
            counts[i] = 0
            countl[i] = 0
        }
    }

}


