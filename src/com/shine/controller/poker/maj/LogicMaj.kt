package com.shine.controller.poker.maj

object LogicMaj {

    val MASK_COLOR = 0xF0
    val MASK_VALUE = 0x0F

    val cardKey = intArrayOf(
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
            0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19,
            0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29,
            0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37)

    val cardList = intArrayOf(
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,       //万
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
            0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19,       //索
            0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19,
            0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19,
            0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19,
            0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29,       //筒
            0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29,
            0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29,
            0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29,
            0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37,                   //风
            0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37,
            0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37,
            0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37,
            0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48              //春夏秋冬梅兰菊竹
    )

    fun GetCardMount(): IntArray {
        val cm = cardList.toMutableList()
        cm.shuffle()
        return cm.toIntArray()
    }

    fun MountSlice(cardMount: IntArray,
                   cardStart: Array<IntArray>,
                   cardArray: Array<IntArray>,
                   cardFlower: Array<IntArray>) {
        var index = 0
        var length = 16

        for (i in 0..3) {
            for (j in 0..23) {

                if (j < length) {
                    cardStart[i][j] = cardMount[index]
                    cardMount[index] = 0x00
                    index++

                    if (cardStart[i][j] > 0x40) //if has flower do length++ that can get more one card
                        length++
                }
            }
            length = 16
        }

        StartSlice(cardStart, cardArray, cardFlower)
        for (i in 0..(index - 1)) {    //mountain last move to head
            cardMount[i] = cardMount[143 - i]
            cardMount[143 - i] = 0x00
        }
    }

    fun StartSlice(cardStart: Array<IntArray>,
                   cardArray: Array<IntArray>,
                   cardFlower: Array<IntArray>) {
        var index = 0
        for (i in 0..3) {
            for (j in 0..23) {

                if (cardStart[i][j] > 0x40) {
                    cardFlower[i][index] = cardStart[i][j]
                    index += 1
                } else if (cardStart[i][j] != 0x00)
                    cardArray[i][j - index] = cardStart[i][j]
            }
            index = 0
        }
    }

    fun Value(card: Int) = card and MASK_VALUE
    fun Color(card: Int) = card and MASK_COLOR
    fun SV(card: Int) = if (card == 0x00) 0x0A else Value(card)    //getValueSort
    fun SC(card: Int) = if (card == 0x00) 0x50 else Color(card)    //GetColorSort

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


    fun HasCardNum(card: Int, cardArr: IntArray, index: Int): Int {

        var num = 0
        for (i in 0..index)
            if (cardArr[i] == card)
                num++
        return num
    }

    fun IndexLive(cardArr: IntArray): Int {
        for (i in 0..21)
            if (cardArr[i] == 0x00)
                return i - 1
        return 22
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

    //actionLegal
    fun LegalCh(card: Int, cardArr: IntArray): Boolean {
        for (i in 0..2)
            if (LegalChi(card, cardArr, i))
                return true
        return false
    }

    fun LegalChi(card: Int, cardArr: IntArray, chikind: Int): Boolean {

        val indexLive = IndexLive(cardArr)
        if (card > 0x29) return false
        when (chikind) {
            0 -> return LegalChi0(card, cardArr, indexLive)
            1 -> return LegalChi1(card, cardArr, indexLive)
            2 -> return LegalChi2(card, cardArr, indexLive)
        }
        return false
    }

    fun LegalChi0(card: Int, cardArr: IntArray, index: Int): Boolean {
        if (HasCardNum(card + 1, cardArr, index) > 0 &&
                HasCardNum(card + 2, cardArr, index) > 0)
            return true
        return false
    }

    fun LegalChi1(card: Int, cardArr: IntArray, index: Int): Boolean {
        if (HasCardNum(card - 1, cardArr, index) > 0 &&
                HasCardNum(card + 1, cardArr, index) > 0)
            return true
        return false
    }

    fun LegalChi2(card: Int, cardArr: IntArray, index: Int): Boolean {
        if (HasCardNum(card - 2, cardArr, index) > 0 &&
                HasCardNum(card - 1, cardArr, index) > 0)
            return true
        return false
    }

    fun LegalPeng(card: Int, cardArr: IntArray): Boolean {
        val indexLive = IndexLive(cardArr)
        if (HasCardNum(card, cardArr, indexLive) > 1) return true
        else return false
    }

    fun LegalGang(card: Int, cardArr: IntArray): Boolean {
        val indexLive = IndexLive(cardArr)
        if (HasCardNum(card, cardArr, indexLive) > 2) return true
        else return false
    }

    fun LegalJiagang(card: Int, cardArr: IntArray): Boolean {
        val indexLive = IndexLive(cardArr)
        val indexDead = IndexDead(cardArr)
        if (HasCardNum(card, cardArr, indexLive) == 0) return false
        for (i in 21 downTo indexDead)
            if (cardArr[i] == card &&
                    cardArr[i - 1] == card &&
                    cardArr[i - 2] == card)
                return true
        return false
    }

    fun LegalAngang(card: Int, cardArr: IntArray): Boolean {
        val indexLive = IndexLive(cardArr)
        if (HasCardNum(card, cardArr, indexLive) == 4) return true
        return false
    }

    fun LegalTing(card: Int, cardArr: IntArray): Boolean {
        val cardClone = cardArr.clone()
        val il = IndexLive(cardClone)
        for (i in 0..il)
            if (cardClone[i] == card) {
                cardClone[i] = 0x00
                break
            }

        Sort(cardClone, il)
        for (i in 0..33)
            if (LegalHu(cardKey[i], cardClone))
                return true
        return false
    }

    fun LegalDa(card: Int, cardArr: IntArray): Boolean {
        val indexLive = IndexLive(cardArr)
        if (indexLive % 3 != 1) return false
        if (HasCardNum(card, cardArr, indexLive) > 0) return true
        else return false
    }

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

    //actionDo
    fun ChangeChi(card: Int, cardArr: IntArray, chikind: Int) {
        val indexLive = IndexLive(cardArr)
        val indexDead = IndexDead(cardArr)
        when (chikind) {
            0 -> ChangeChi0(card, cardArr, indexLive, indexDead)
            1 -> ChangeChi1(card, cardArr, indexLive, indexDead)
            2 -> ChangeChi2(card, cardArr, indexLive, indexDead)
        }
    }

    fun ChangeChi0(card: Int, cardArr: IntArray, il: Int, id: Int) {
        for (i in 0..il)
            if (cardArr[i] == card + 1) {
                cardArr[i] = 0
                break
            }
        for (i in 0..il)
            if (cardArr[i] == card + 2) {
                cardArr[i] = 0
                break
            }

        Sort(cardArr, il)
        cardArr[id - 1] = card
        cardArr[id - 2] = card + 1
        cardArr[id - 3] = card + 2
    }

    fun ChangeChi1(card: Int, cardArr: IntArray, il: Int, id: Int) {
        for (i in 0..il)
            if (cardArr[i] == card - 1) {
                cardArr[i] = 0
                break
            }
        for (i in 0..il)
            if (cardArr[i] == card + 1) {
                cardArr[i] = 0
                break
            }

        Sort(cardArr, il)
        cardArr[id - 1] = card - 1
        cardArr[id - 2] = card
        cardArr[id - 3] = card + 1
    }

    fun ChangeChi2(card: Int, cardArr: IntArray, il: Int, id: Int) {
        for (i in 0..il)
            if (cardArr[i] == card - 1) {
                cardArr[i] = 0
                break
            }
        for (i in 0..il)
            if (cardArr[i] == card - 2) {
                cardArr[i] = 0
                break
            }

        Sort(cardArr, il)
        cardArr[id - 1] = card
        cardArr[id - 2] = card - 1
        cardArr[id - 3] = card - 2
    }

    fun ChangePeng(card: Int, cardArr: IntArray) {

        val il = IndexLive(cardArr)
        val id = IndexDead(cardArr)
        var t = 0
        for (i in 0..il) {
            if (cardArr[i] == card) {
                cardArr[i] = 0x00
                t++
                if (t == 2) break
            }
        }

        Sort(cardArr, il)
        for (i in id - 3..id - 1)
            cardArr[i] = card
    }

    fun ChangeGang(card: Int, cardArr: IntArray) {

        val il = IndexLive(cardArr)
        val id = IndexDead(cardArr)
        var t = 0
        for (i in 0..il) {
            if (cardArr[i] == card) {
                cardArr[i] = 0x00
                t++
                if (t == 3) break
            }
        }

        Sort(cardArr, il)
        for (i in id - 4..id - 1)
            cardArr[i] = card
    }

    fun ChangeJiagang(card: Int, cardArr: IntArray) {
        val il = IndexLive(cardArr)
        val id = IndexDead(cardArr)
        for (i in 0..il)
            if (cardArr[i] == card) {
                cardArr[i] == 0x00
                break
            }

        Sort(cardArr, il)
        cardArr[id - 1] = card
    }

    fun ChangeAngang(card: Int, cardArr: IntArray) {
        val il = IndexLive(cardArr)
        val id = IndexDead(cardArr)
        var t = 0
        for (i in 0..il) {
            if (cardArr[i] == card) {
                cardArr[i] = 0x00
                t++
                if (t == 4) break
            }
        }

        Sort(cardArr, il)
        for (i in id - 4..id - 1)
            cardArr[i] = -1
    }

    fun ChangeMo(cardMount: IntArray): Int {

        var il = 80
        for (i in 1..79)
            if (cardMount[i] == 0x00) {
                il = i
                break
            }

        cardMount[0] = cardMount[il - 1]
        cardMount[il - 1] = 0x00
        return il - 1
    }

    fun ChangeDa(card: Int, cardArr: IntArray) {
        val il = IndexLive(cardArr)
        for (i in 0..il)
            if (cardArr[i] == card) {
                cardArr[i] = 0x00
                break
            }
        Sort(cardArr, il)
    }

    fun ChangeHu(card: Int, cardArr: IntArray) {
        val il = IndexLive(cardArr)
        if (cardArr[il + 1] == 0)
            cardArr[il + 1] = card
    }


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
                SortL(qujiang, len)
                quJiangs.add(qujiang)
            }
        return quJiangs
    }


    fun getMaxSeat(seatValue: IntArray): Int {
        var max = -1
        if (seatValue[0] >= seatValue[1]) max = 0
        else max = 1
        if (seatValue[max] < seatValue[2]) max = 2
        return max
    }

    fun commandTurn(data: String): Int {
        when (data) {
            "chi" -> return 1
            "peng" -> return 2
            "gang" -> return 2
            "hu" -> return 3
            "pass" -> return 0
        }
        return 0
    }

    fun pla(arr: IntArray): String {
        var r = "["
        for (i in 0 until arr.size) {
            r += arr[i]
            if (i != arr.size - 1) r += ","
        }
        r += "]"
        return r
    }

    fun getTai(taiTypes: MutableList<String>): Int {

        var tais = 0
        for (tai in taiTypes)
            taiMapper[tai]?.let {
                tais += it
            }

        return tais
    }

    val taiMapper = mapOf(

            "da4xi" to 24,
            "da3yuan" to 12,
            "xiao4xi" to 12,
            "xiao3yuan" to 6,
            "3yuantai" to 2,

            "qing1se" to 12,
            "hun1se" to 6,
            "zi1se" to 16,

            "8xianguolai" to 16,
            "hua1" to 1, "hua2" to 2, "hua3" to 3,
            "hua4" to 4, "hua5" to 5, "hua6" to 6, "hua7" to 7,
            "wuziwuhua" to 2,

            "qinglaotou" to 16,
            "duanyao9" to 4,
            "hunlaotou" to 8,
            "3setongke" to 4,
            "3setongshun" to 4,
            "1qitongguan" to 4,
            "1sesanshun" to 6,

            "3angang" to 12,
            "4lianke" to 7,
            "5lianke" to 12,

            "5anke" to 12,
            "4anke" to 7,
            "3anke" to 3,

            "chunquandaiyao" to 8,
            "hunquandaiyao" to 4,

            "menqing" to 2,
            "zimo" to 2,
            "menqingzimo" to 5,
            "quanqiuren" to 3,

            "duting" to 2,
            "1fa" to 1,
            "qianggang" to 2,
            "haidilaoyue" to 2,
            "hedilaoyu" to 2,
            "baopai" to 1

    )
    /*fun LegalHu(card: Int,cardArr: IntArray):Boolean{

        val arr=cardArr.clone()
        val il= IndexLive(arr)

        val index=il+1
        val len=index+1

        arr[index]=card
        Sort(arr,index)

        val jiangs= getJia(arr,len)
        val qujiangArrs= quJiangArrs(arr,jiangs,len)
        val ll=il

        for (qujiang in qujiangArrs){
            println("\tarr=${pla(qujiang)}")
            val weis = getWeis(qujiang,ll)
            println("\tweis=$weis")
            val hu= weisThrough(weis)
            if (hu) return true
        }
        return false
    }*/

    @JvmStatic
    fun main(args: Array<String>) {
        //前辈留下的
    }

    //步进分拆法[不带鬼牌]
    /*
    * 步进分拆法：（注意：不带鬼牌）

    1、将牌按连续性进行拆分，拆出的组合为3*n 或 3*n + 2，如果有例外，则不能胡。
    2、检查数量为3*n的连续段是否满足胡牌条件，如果都能满足，再用方法3检查3*n+2
    3、在连续的牌中，牌张数为3*n + 2的张数拆出可能的将牌
    4、扣除将牌后，分别检查各连续的段是否满足胡牌

    检查段的思路：
       例：连续段为 1筒1筒1筒2筒3筒3筒4筒4筒5筒
           数字表示为 31221

       a、取3位数为key，从下表查询，如果有结果则扣除这个数字。
          312取到结果330，则余下数字为1221
       b、如果a步骤没有结果，则取2位数为key
       c、如果b步骤没有结果，则取1位数为key
       如果c失败，则不能胡

       31221拆分全步骤：
       312 = 300 余 1221
       122 = 111 余 111
       111 = 111 全部拆分完毕，能胡

    拆分表：
    local t = {
        [3] = 3, [4] = 3,
        [31] = 30, [32] = 30, 33 = 33, 34 = 33, 44 = 33,
        [111] = 111, [112] = 111, [113] = 111, [114] = 114,
        [122] = 111, [123] = 111, [124] = 111,
        [133] = 111, [134] = 111,
        [141] = 141, [142] = 141, [143] = 141, [144] = 144,
        [222] = 222, [223] = 222, [224] = 222,
        [233] = 222, [234] = 222,
        [244] = 222,
        [311] = 300, [312] = 300, [313] = 300, [314] = 300,
        [322] = 300, [323] = 300, [324] = 300,
        [331] = 330, [332] = 330, [333] = 333, [334] = 333,
        [341] = 330, [342] = 330, [343] = 330, [344] = 333,
        [411] = 411, [412] = 411, [413] = 411, [414] = 414,
        [422] = 411, [423] = 411, [424] = 411,
        [433] = 411, [434] = 411,
        [441] = 441, [442] = 441, [443] = 441, [444] = 444
    }

    表格生成思路：
    1、从边上取牌的数量
    2、如果是1，则取111
    3、如果是2，则取222
    4、如果是3，则取3
    5、如果是4，则取411
    */

}
