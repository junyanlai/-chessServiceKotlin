package com.shine.controller.poker.ddz

object LogicDzz {

    val MASK_COLOR = 0xF0
    val MASK_VALUE = 0x0F

    val cardList = intArrayOf(
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D,//梅花
            0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D,//方块
            0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x2D,//红桃
            0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x3B, 0x3C, 0x3D,//黑桃
            0x4E, 0x4F
    )


    fun GetCardMount(): IntArray {
        val cm = cardList.toMutableList()
        cm.shuffle()
        return cm.toIntArray()
    }

    fun MountSlice(cardMount: IntArray, cardArray: Array<MutableList<Int>>): IntArray {
        var seat = -1
        for (i in 0..2)
            for (j in 0..16)
                cardArray[i].add(cardMount[i * 17 + j])

        return intArrayOf(cardMount[51], cardMount[52], cardMount[53])
    }

    fun V(card: Int) = card and MASK_VALUE

    fun SV(card: Int): Int {

        val v = V(card)
        if (v < 3) return v + 13
        if (card == 0x4E) return v + 2
        return v
    }


    fun Type(cards: IntArray): cardTypeDzz {

        val len = cards.size
        //Sort
        val c = cards.clone()
        when (len) {
            1 -> return cardTypeDzz.SINGLE
            2 -> if (c[0] == 0X4E)
                    return cardTypeDzz.ROCKET
            else if (c[0] == c[1])
                    return cardTypeDzz.PAIR
            else return cardTypeDzz.ERROR
            3 -> if (c[0] == c[1] && c[0] == c[2])
                    return cardTypeDzz.THREE
                    else return cardTypeDzz.ERROR
            4 -> if (c[0] == c[2] || c[1] == c[3])
                    return cardTypeDzz.THREE1
                 else if (c[0] == c[3])
                        return cardTypeDzz.BOOM
                 else return cardTypeDzz.ERROR
            5 -> if (c[0] == c[1] && c[2] == c[4] || c[0] == c[2] && c[3] == c[4])
                    return cardTypeDzz.THREE2
                    else if ((c[0] + 1 == c[1]) && (c[1] + 1 == c[2]) && (c[2] + 1 == c[3]) && (c[3] + 1 == c[4]) && c[4] < 15)
                        return cardTypeDzz.SHUN5
                    else return cardTypeDzz.ERROR
            6 -> if (c[0] == c[3] || c[2] == c[5] || c[1] == c[4])
                    return cardTypeDzz.FOUR2
                else if (c[0] == c[2] && c[3] == c[5] && c[0] + 1 == c[3])
                    return cardTypeDzz.FLY2
                else if (((c[0] + 1 == c[2]) && (c[2] + 1 == c[4]) && c[5] < 15 &&
                            (c[0] == c[1] && c[2] == c[3] && c[4] == c[5])))
                    return cardTypeDzz.PAIRS3
                else return cardTypeDzz.ERROR
            8 -> if (c[0] == c[3] && c[4] == c[5] && c[6] == c[7] ||
                    c[0] == c[1] && c[2] == c[5] && c[6] == c[7] ||
                    c[0] == c[1] && c[2] == c[3] && c[4] == c[7])
                    return cardTypeDzz.FOUR2S
                else if (((c[0] + 1 == c[2]) && (c[2] + 1 == c[4]) && (c[4] + 1 == c[6]) && c[7] < 15 &&
                            (c[0] == c[1] && c[2] == c[3] && c[4] == c[5] && c[6] == c[7])))
                    return cardTypeDzz.PAIRS4
                else if (((c[0] == c[2] && c[3] == c[5] && c[0] + 1 == c[3]) ||
                            (c[1] == c[3] && c[4] == c[6] && c[1] + 1 == c[4]) ||
                            (c[2] == c[4] && c[5] == c[7] && c[2] + 1 == c[5])))
                    return cardTypeDzz.FLYX2
                else return cardTypeDzz.ERROR

            9 -> if ((c[0] + 1 == c[3] && c[3] + 1 == c[6] &&//C[8]<15 &&
                            c[0] == c[2] && c[3] == c[5] && c[6] == c[8]))
                    return cardTypeDzz.FLY3
                else return cardTypeDzz.ERROR
            10 -> if ((((c[0] + 1 == c[3] &&
                        c[0] == c[2] && c[3] == c[5] &&
                        c[6] == c[7] && c[8] == c[9])) ||

                        ((c[2] + 1 == c[5] &&
                        c[2] == c[4] && c[5] == c[7] &&
                        c[0] == c[1] && c[8] == c[9])) ||

                        ((c[4] + 1 == c[7] &&
                        c[4] == c[6] && c[7] == c[9] &&
                        c[0] == c[1] && c[2] == c[3]))))
                return cardTypeDzz.FLYD2
                else if (((c[0] + 1 == c[2]) && (c[2] + 1 == c[4]) && (c[4] + 1 == c[6]) && (c[6] + 1 == c[8]) && c[9] < 15 &&
                            (c[0] == c[1] && c[2] == c[3] && c[4] == c[5] && c[6] == c[7] && c[8] == c[9])))
                return cardTypeDzz.PAIRS5
                else return cardTypeDzz.ERROR
            12 -> if (((c[0] + 1 == c[2]) && (c[2] + 1 == c[4]) && (c[4] + 1 == c[6]) && (c[6] + 1 == c[8]) && (c[8] + 1 == c[10]) && c[11] < 15 &&
                        (c[0] == c[1] && c[2] == c[3] && c[4] == c[5] && c[6] == c[7] && c[8] == c[9] && c[10] == c[11])))
                return cardTypeDzz.PAIRS6
                else if ((c[0] + 1 == c[3] && c[3] + 1 == c[6] && c[6] + 1 == c[9] && //c[11]<15 &&
                          c[0] == c[2] && c[3] == c[5] && c[6] == c[8] && c[9] == c[11]))
                return cardTypeDzz.FLY4
                else if (((c[0] == c[2] && c[3] == c[5] && c[6] == c[8] && c[0] + 1 == c[3] && c[3] + 1 == c[6] /*&&c[6]<15*/) ||
                            (c[1] == c[3] && c[4] == c[6] && c[7] == c[9] && c[1] + 1 == c[4] && c[4] + 1 == c[7] /*&&c[7]<15*/) ||
                            (c[2] == c[4] && c[5] == c[7] && c[8] == c[10] && c[2] + 1 == c[5] && c[5] + 1 == c[8] /*&&c[8]<15*/) ||
                            (c[3] == c[5] && c[6] == c[8] && c[9] == c[11] && c[3] + 1 == c[6] && c[6] + 1 == c[9] /*&&c[9]<15*/)))
                return cardTypeDzz.FLYX3
                else return cardTypeDzz.ERROR
            14 -> if (((c[0] + 1 == c[2]) && (c[2] + 1 == c[4]) && (c[4] + 1 == c[6]) && (c[6] + 1 == c[8]) && (c[8] + 1 == c[10]) && (c[10] + 1 == c[12]) && c[13] < 15 &&
                            (c[0] == c[1] && c[2] == c[3] && c[4] == c[5] && c[6] == c[7] && c[8] == c[9] && c[10] == c[11] && c[12] == c[13])))
                return cardTypeDzz.PAIRS7
                else return cardTypeDzz.ERROR
            15 -> if ((c[0] + 1 == c[3] && c[3] + 1 == c[6] && c[6] + 1 == c[9] && c[9] + 1 == c[12] && //c[11]<15 &&
                        c[0] == c[2] && c[3] == c[5] && c[6] == c[8] && c[9] == c[11] && c[12] == c[14]))
                return cardTypeDzz.FLY5
                else return if (((c[0] + 1 == c[3] && c[3] + 1 == c[6] && c[0] == c[2] && c[3] == c[5] && c[6] == c[8] && c[9] == c[10] && c[11] == c[12] && c[13] == c[14]) ||//C[8]<15 &&
                            (c[2] + 1 == c[5] && c[5] + 1 == c[8] && c[2] == c[4] && c[5] == c[7] && c[8] == c[10] && c[0] == c[1] && c[11] == c[12] && c[13] == c[14]) ||//C[10]<15 &&
                            (c[4] + 1 == c[7] && c[7] + 1 == c[10] && c[4] == c[6] && c[7] == c[9] && c[10] == c[12] && c[0] == c[1] && c[2] == c[3] && c[13] == c[14]) ||//C[12]<15 &&
                            (c[6] + 1 == c[9] && c[9] + 1 == c[12] && c[6] == c[8] && c[9] == c[11] && c[12] == c[14] && c[0] == c[1] && c[2] == c[3] && c[4] == c[5])) /*C[14]<15 &&*/)
                return cardTypeDzz.FLYD3
                else return cardTypeDzz.ERROR
            16 -> if (((c[0] + 1 == c[2]) && (c[2] + 1 == c[4]) && (c[4] + 1 == c[6]) && (c[6] + 1 == c[8]) && (c[8] + 1 == c[10]) && (c[10] + 1 == c[12]) && (c[12] + 1 == c[14]) && c[14] < 15 &&
                        (c[0] == c[1] && c[2] == c[3] && c[4] == c[5] && c[6] == c[7] && c[8] == c[9] && c[10] == c[11] && c[12] == c[13] && c[14] == c[15])))
                return cardTypeDzz.PAIRS8
                else return if (
                    ((c[0] + 1 == c[3] && c[3] + 1 == c[6] && c[6] + 1 == c[9] && c[0] == c[2] && c[3] == c[5] && c[6] == c[8] && c[9] == c[11]) ||//c[11]<15 &&
                    (c[1] + 1 == c[4] && c[4] + 1 == c[7] && c[7] + 1 == c[10] && c[1] == c[3] && c[4] == c[6] && c[7] == c[9] && c[10] == c[12]) ||//c[12]<15 &&
                    (c[2] + 1 == c[5] && c[5] + 1 == c[8] && c[8] + 1 == c[11] && c[2] == c[4] && c[5] == c[7] && c[8] == c[10] && c[11] == c[13]) ||//c[13]<15 &&
                    (c[3] + 1 == c[6] && c[6] + 1 == c[9] && c[9] + 1 == c[12] && c[3] == c[5] && c[6] == c[8] && c[9] == c[11] && c[12] == c[14]) ||//c[14]<15 &&
                    (c[4] + 1 == c[7] && c[7] + 1 == c[10] && c[10] + 1 == c[13] && c[4] == c[6] && c[7] == c[9] && c[10] == c[12] && c[13] == c[15]))/*c[15]<15 &&*/)
                return cardTypeDzz.FLYX4
                else return cardTypeDzz.ERROR
            18 -> if (((c[0] + 1 == c[2]) && (c[2] + 1 == c[4]) && (c[4] + 1 == c[6]) && (c[6] + 1 == c[8]) && (c[8] + 1 == c[10]) && (c[10] + 1 == c[12]) && (c[12] + 1 == c[14]) && (c[14] + 1 == c[16]) && c[16] < 15 &&
                            (c[0] == c[1] && c[2] == c[3] && c[4] == c[5] && c[6] == c[7] && c[8] == c[9] && c[10] == c[11] && c[12] == c[13] && c[14] == c[15] && c[16] == c[17])))
                return cardTypeDzz.PAIRS9
                else  if ((c[0] + 1 == c[3] && c[3] + 1 == c[6] && c[6] + 1 == c[9] && c[9] + 1 == c[12] && c[12] + 1 == c[15] && //c[11]<15 &&
                            c[0] == c[2] && c[3] == c[5] && c[6] == c[8] && c[9] == c[11] && c[12] == c[14] && c[15] == c[17]))
                return cardTypeDzz.FLY6
            else
                return cardTypeDzz.ERROR
            20 -> if (((c[0] + 1 == c[2]) && (c[2] + 1 == c[4]) && (c[4] + 1 == c[6]) && (c[6] + 1 == c[8]) && (c[8] + 1 == c[10]) && (c[10] + 1 == c[12]) && (c[12] + 1 == c[14]) && (c[14] + 1 == c[16]) && (c[16] + 1 == c[18]) && c[18] < 15 &&
                        (c[0] == c[1] && c[2] == c[3] && c[4] == c[5] && c[6] == c[7] && c[8] == c[9] && c[10] == c[11] && c[12] == c[13] && c[14] == c[15] && c[16] == c[17] && c[18] == c[19])))
                return cardTypeDzz.PAIRS0
                else if (((c[0] + 1 == c[3] && c[3] + 1 == c[6] && c[6] + 1 == c[9] && c[9] + 1 == c[12] && c[0] == c[2] && c[3] == c[5] && c[6] == c[8] && c[9] == c[11] && c[12] == c[14]) ||//c[12]<15 &&
                            (c[1] + 1 == c[4] && c[4] + 1 == c[7] && c[7] + 1 == c[10] && c[10] + 1 == c[13] && c[1] == c[3] && c[4] == c[6] && c[7] == c[9] && c[10] == c[12] && c[13] == c[15]) ||//c[13]<15 &&
                            (c[2] + 1 == c[5] && c[5] + 1 == c[8] && c[8] + 1 == c[11] && c[11] + 1 == c[14] && c[2] == c[4] && c[5] == c[7] && c[8] == c[10] && c[11] == c[13] && c[14] == c[16]) ||//c[14]<15 &&
                            (c[3] + 1 == c[6] && c[6] + 1 == c[9] && c[9] + 1 == c[12] && c[12] + 1 == c[15] && c[3] == c[5] && c[6] == c[8] && c[9] == c[11] && c[12] == c[14] && c[15] == c[17]) ||//c[15]<15 &&
                            (c[4] + 1 == c[7] && c[7] + 1 == c[10] && c[10] + 1 == c[13] && c[13] + 1 == c[16] && c[4] == c[6] && c[7] == c[9] && c[10] == c[12] && c[13] == c[15] && c[16] == c[18]) ||//c[16]<15 &&
                            (c[5] + 1 == c[8] && c[8] + 1 == c[11] && c[11] + 1 == c[14] && c[14] + 1 == c[17] && c[5] == c[7] && c[8] == c[10] && c[11] == c[13] && c[14] == c[16] && c[17] == c[19]))/*c[17]<15 &&*/)
                return cardTypeDzz.FLYX5
                else if (((c[0] + 1 == c[3] && c[3] + 1 == c[6] && c[6] + 1 == c[9] && c[0] == c[2] && c[3] == c[5] && c[6] == c[8] && c[9] == c[11] && c[12] == c[13] && c[14] == c[15] && c[16] == c[17] && c[18] == c[19]) ||//c[11]<15 &&
                            (c[2] + 1 == c[5] && c[5] + 1 == c[8] && c[8] + 1 == c[11] && c[2] == c[4] && c[5] == c[7] && c[8] == c[10] && c[11] == c[13] && c[0] == c[1] && c[14] == c[15] && c[16] == c[17] && c[18] == c[19]) ||//c[13]<15 &&
                            (c[4] + 1 == c[7] && c[7] + 1 == c[10] && c[10] + 1 == c[13] && c[4] == c[6] && c[7] == c[9] && c[10] == c[12] && c[13] == c[15] && c[0] == c[1] && c[2] == c[3] && c[16] == c[17] && c[18] == c[19]) ||//c[15]<15 &&
                            (c[6] + 1 == c[9] && c[9] + 1 == c[12] && c[12] + 1 == c[15] && c[6] == c[8] && c[9] == c[11] && c[12] == c[14] && c[15] == c[17] && c[0] == c[1] && c[2] == c[3] && c[4] == c[5] && c[18] == c[19]) ||//c[17]<15 &&
                            (c[8] + 1 == c[11] && c[11] + 1 == c[14] && c[14] + 1 == c[17] && c[8] == c[10] && c[11] == c[13] && c[14] == c[16] && c[17] == c[19] && c[0] == c[1] && c[2] == c[3] && c[4] == c[5] && c[6] == c[7]))/*c[19]<15 &&*/)
                return cardTypeDzz.FLYD4
                else return cardTypeDzz.ERROR
        }
        return cardTypeDzz.ERROR
    }

    fun TypeWeight(cards: IntArray): IntArray {

        val len=cards.size
        val c = cards.clone()
        //Sort

        val tw = IntArray(2)
        var weight = 0
        when (len) {
            1 -> {
                tw[0] = cardTypeDzz.SINGLE.order
                tw[1] = c[0]
                return tw
            }
            2 -> if (c[0] == 16) {
                tw[0] = cardTypeDzz.ROCKET.order
                weight = c[1]
                tw[1] = weight
                return tw
            } else if (c[0] == c[1]) {
                tw[0] = cardTypeDzz.PAIR.order
                weight = c[1]
                tw[1] = weight
                return tw
            } else {
                tw[0] = 0
                tw[1] = 0
                return tw
            }
            3 -> if (c[0] == c[1] && c[0] == c[2]) {
                tw[0] = cardTypeDzz.THREE.order
                weight = c[2]
                tw[1] = weight
                return tw
            } else {
                tw[0] = 0
                tw[1] = 0
                return tw
            }
            4 -> if (c[0] == c[2] || c[1] == c[3]) {
                tw[0] = cardTypeDzz.THREE1.order
                weight = c[2]
                tw[1] = weight
                return tw
            } else if (c[0] == c[3]) {
                tw[0] = cardTypeDzz.BOOM.order
                weight = c[3]
                tw[1] = weight
                return tw
            } else {
                tw[0] = 0
                tw[1] = 0
                return tw
            }
            5 -> if (c[0] == c[1] && c[2] == c[4] || c[0] == c[2] && c[3] == c[4]) {
                tw[0] = cardTypeDzz.THREE2.order
                weight = c[2]
                tw[1] = weight
                return tw
            } else if (c[0] + 1 == c[1] && c[1] + 1 == c[2] && c[2] + 1 == c[3] && c[3] + 1 == c[4] && c[4] < 15) {
                tw[0] = cardTypeDzz.SHUN5.order
                weight = c[4]
                tw[1] = weight
                return tw
            } else {
                tw[0] = 0
                tw[1] = 0
                return tw
            }
            6 -> if (c[0] == c[3] || c[2] == c[5] || c[1] == c[4]) {
                tw[0] = cardTypeDzz.THREE2.order
                weight = c[3]
                tw[1] = weight
                return tw
            } else if (c[0] == c[2] && c[3] == c[5] && c[0] + 1 == c[3]) {
                tw[0] = cardTypeDzz.FLY2.order
                weight = c[5]
                tw[1] = weight
                return tw
            } else if (c[0] + 1 == c[2] && c[2] + 1 == c[4] && c[5] < 15 &&
                    c[0] == c[1] && c[2] == c[3] && c[4] == c[5]) {
                tw[0] = cardTypeDzz.PAIRS3.order
                weight = c[5]
                tw[1] = weight
                return tw
            } else if (c[0] + 1 == c[1] && c[1] + 1 == c[2] && c[2] + 1 == c[3] && c[3] + 1 == c[4] && c[4] + 1 == c[5] && c[5] < 15) {
                tw[0] = cardTypeDzz.SHUN6.order
                weight = c[5]
                tw[1] = weight
                return tw
            } else {
                tw[0] = 0
                tw[1] = 0
                return tw
            }

            7 ->
            if (c[0] + 1 == c[1] && c[1] + 1 == c[2] && c[2] + 1 == c[3] && c[3] + 1 == c[4] && c[4] + 1 == c[5] && c[5] + 1 == c[6] && c[6] < 15) {
                tw[0] = cardTypeDzz.SHUN7.order
                weight = c[6]
                tw[1] = weight
                return tw
            } else {
                tw[0] = 0
                tw[1] = 0
                return tw
            }

            8 ->
            if (c[0] == c[3] && c[4] == c[5] && c[6] == c[7]) {
                tw[0] = cardTypeDzz.FOUR2S.order
                weight = c[3]
                tw[1] = weight
                return tw
            } else if (c[0] == c[1] && c[2] == c[5] && c[6] == c[7]) {
                tw[0] = cardTypeDzz.FOUR2S.order
                weight = c[5]
                tw[1] = weight
                return tw
            } else if (c[0] == c[1] && c[2] == c[3] && c[4] == c[7]) {
                tw[0] = cardTypeDzz.FOUR2S.order
                weight = c[7]
                tw[1] = weight
                return tw
            } else if (c[0] + 1 == c[2] && c[2] + 1 == c[4] && c[4] + 1 == c[6] && c[7] < 15 &&
                    c[0] == c[1] && c[2] == c[3] && c[4] == c[5] && c[6] == c[7]) {
                tw[0] = cardTypeDzz.PAIRS4.order
                weight = c[7]
                tw[1] = weight
                return tw
            } else if (c[0] == c[2] && c[3] == c[5] && c[0] + 1 == c[3] ||
                    c[1] == c[3] && c[4] == c[6] && c[1] + 1 == c[4] ||
                    c[2] == c[4] && c[5] == c[7] && c[2] + 1 == c[5]) {
                tw[0] = cardTypeDzz.FLYX2.order
                weight = c[5]
                tw[1] = weight
                return tw
            } else if (c[0] + 1 == c[1] && c[1] + 1 == c[2] && c[2] + 1 == c[3] && c[3] + 1 == c[4] && c[4] + 1 == c[5] && c[5] + 1 == c[6] && c[6] + 1 == c[7] && c[7] < 15) {
                tw[0] = cardTypeDzz.SHUN8.order
                weight = c[7]
                tw[1] = weight
                return tw
            } else {
                tw[0] = 0
                tw[1] = 0
                return tw
            }

            9 ->
            if (c[0] + 1 == c[3] && c[3] + 1 == c[6] &&//C[8]<15 &&
                    c[0] == c[2] && c[3] == c[5] && c[6] == c[8]) {
                tw[0] = cardTypeDzz.FLY3.order
                weight = c[8]
                tw[1] = weight
                return tw
            } else if (c[0] + 1 == c[1] && c[1] + 1 == c[2] && c[2] + 1 == c[3] && c[3] + 1 == c[4] && c[4] + 1 == c[5] && c[5] + 1 == c[6] && c[6] + 1 == c[7] && c[7] + 1 == c[8] && c[8] < 15) {
                tw[0] = cardTypeDzz.SHUN9.order
                weight = c[8]
                tw[1] = weight
                return tw
            } else {
                tw[0] = 0
                tw[1] = 0
                return tw
            }

            10 ->
            if (c[0] + 1 == c[3] && c[0] == c[2] && c[3] == c[5] && c[6] == c[7] && c[8] == c[9]) {
                tw[0] = cardTypeDzz.FLYD2.order
                weight = c[5]
                tw[1] = weight
                return tw
            } else if (c[2] + 1 == c[5] && c[2] == c[4] && c[5] == c[7] && c[0] == c[1] && c[8] == c[9]) {
                tw[0] = cardTypeDzz.FLYD2.order
                weight = c[7]
                tw[1] = weight
                return tw
            } else if (c[4] + 1 == c[7] && c[4] == c[6] && c[7] == c[9] && c[0] == c[1] && c[2] == c[3]) {
                tw[0] = cardTypeDzz.FLYD2.order
                weight = c[9]
                tw[1] = weight
                return tw
            } else if (c[0] + 1 == c[2] && c[2] + 1 == c[4] && c[4] + 1 == c[6] && c[6] + 1 == c[8] && c[9] < 15 &&
                    c[0] == c[1] && c[2] == c[3] && c[4] == c[5] && c[6] == c[7] && c[8] == c[9]) {
                tw[0] = cardTypeDzz.PAIRS5.order
                weight = c[9]
                tw[1] = weight
                return tw
            } else if (c[0] + 1 == c[1] && c[1] + 1 == c[2] && c[2] + 1 == c[3] && c[3] + 1 == c[4] && c[4] + 1 == c[5]
                    && c[5] + 1 == c[6] && c[6] + 1 == c[7] && c[7] + 1 == c[8] && c[8] + 1 == c[9] && c[9] < 15) {
                tw[0] = cardTypeDzz.SHUN10.order
                weight = c[9]
                tw[1] = weight
                return tw
            } else {
                tw[0] = 0
                tw[1] = 0
                return tw
            }

            11 ->
            if (c[0] + 1 == c[1] && c[1] + 1 == c[2] && c[2] + 1 == c[3] && c[3] + 1 == c[4] && c[4] + 1 == c[5]
                    && c[5] + 1 == c[6] && c[6] + 1 == c[7] && c[7] + 1 == c[8] && c[8] + 1 == c[9] && c[9] + 1 == c[10] && c[10] < 15) {
                tw[0] = cardTypeDzz.SHUN11.order
                weight = c[10]
                tw[1] = weight
                return tw
            } else {
                tw[0] = 0
                tw[1] = 0
                return tw
            }

            12 ->
            if (c[0] + 1 == c[2] && c[2] + 1 == c[4] && c[4] + 1 == c[6] && c[6] + 1 == c[8] && c[8] + 1 == c[10] && c[11] < 15 &&
                    c[0] == c[1] && c[2] == c[3] && c[4] == c[5] && c[6] == c[7] && c[8] == c[9] && c[10] == c[11]) {
                tw[0] = cardTypeDzz.PAIRS6.order
                weight = c[11]
                tw[1] = weight
                return tw
            } else if (c[0] + 1 == c[3] && c[3] + 1 == c[6] && c[6] + 1 == c[9] && //c[11]<15 &&
                    c[0] == c[2] && c[3] == c[5] && c[6] == c[8] && c[9] == c[11]) {
                tw[0] = cardTypeDzz.FLY4.order
                weight = c[11]
                tw[1] = weight
                return tw
            } else if (c[0] == c[2] && c[3] == c[5] && c[6] == c[8] && c[0] + 1 == c[3] && c[3] + 1 == c[6] /*&&c[6]<15*/) {
                tw[0] = cardTypeDzz.FLYX3.order
                weight = c[8]
                tw[1] = weight
                return tw
            } else if (c[1] == c[3] && c[4] == c[6] && c[7] == c[9] && c[1] + 1 == c[4] && c[4] + 1 == c[7] /*&&c[7]<15*/) {
                tw[0] = cardTypeDzz.FLYX3.order
                weight = c[9]
                tw[1] = weight
                return tw
            } else if (c[2] == c[4] && c[5] == c[7] && c[8] == c[10] && c[2] + 1 == c[5] && c[5] + 1 == c[8] /*&&c[8]<15*/) {
                tw[0] = cardTypeDzz.FLYX3.order
                weight = c[10]
                tw[1] = weight
                return tw
            } else if (c[3] == c[5] && c[6] == c[8] && c[9] == c[11] && c[3] + 1 == c[6] && c[6] + 1 == c[9] /*&&c[9]<15*/) {
                tw[0] = cardTypeDzz.FLYX3.order
                weight = c[11]
                tw[1] = weight
                return tw
            } else if (c[0] + 1 == c[1] && c[1] + 1 == c[2] && c[2] + 1 == c[3] && c[3] + 1 == c[4] && c[4] + 1 == c[5] && c[5] + 1 == c[6]
                    && c[6] + 1 == c[7] && c[7] + 1 == c[8] && c[8] + 1 == c[9] && c[9] + 1 == c[10] && c[10] + 1 == c[11] && c[11] < 15) {
                tw[0] = cardTypeDzz.SHUN12.order
                weight = c[11]
                tw[1] = weight
                return tw
            } else {
                tw[0] = 0
                tw[1] = 0
                return tw
            }

            14 ->
            if (c[0] + 1 == c[2] && c[2] + 1 == c[4] && c[4] + 1 == c[6] && c[6] + 1 == c[8] && c[8] + 1 == c[10] && c[10] + 1 == c[12] && c[13] < 15 &&
                    c[0] == c[1] && c[2] == c[3] && c[4] == c[5] && c[6] == c[7] && c[8] == c[9] && c[10] == c[11] && c[12] == c[13]) {
                tw[0] = cardTypeDzz.PAIRS7.order
                weight = c[13]
                tw[1] = weight
                return tw
            } else {
                tw[0] = 0
                tw[1] = 0
                return tw
            }

            15 ->
            if (c[0] + 1 == c[3] && c[3] + 1 == c[6] && c[6] + 1 == c[9] && c[9] + 1 == c[12] && //c[11]<15 &&
                    c[0] == c[2] && c[3] == c[5] && c[6] == c[8] && c[9] == c[11] && c[12] == c[14]) {
                tw[0] = cardTypeDzz.FLY5.order
                weight = c[14]
                tw[1] = weight
                return tw
            } else if (c[0] + 1 == c[3] && c[3] + 1 == c[6] && c[0] == c[2] && c[3] == c[5] && c[6] == c[8] && c[9] == c[10] && c[11] == c[12] && c[13] == c[14] /*C[8]<15 &&*/) {
                tw[0] = cardTypeDzz.FLYD3.order
                weight = c[8]
                tw[1] = weight
                return tw
            } else if (c[2] + 1 == c[5] && c[5] + 1 == c[8] && c[2] == c[4] && c[5] == c[7] && c[8] == c[10] && c[0] == c[1] && c[11] == c[12] && c[13] == c[14] /*C[10]<15 &&*/) {
                tw[0] = cardTypeDzz.FLYD3.order
                weight = c[10]
                tw[1] = weight
                return tw
            } else if (c[4] + 1 == c[7] && c[7] + 1 == c[10] && c[4] == c[6] && c[7] == c[9] && c[10] == c[12] && c[0] == c[1] && c[2] == c[3] && c[13] == c[14] /*C[12]<15 &&*/) {
                tw[0] = cardTypeDzz.FLYD3.order
                weight = c[12]
                tw[1] = weight
                return tw
            } else if (c[6] + 1 == c[9] && c[9] + 1 == c[12] && c[6] == c[8] && c[9] == c[11] && c[12] == c[14] && c[0] == c[1] && c[2] == c[3] && c[4] == c[5] /*C[14]<15 &&*/) {
                tw[0] = cardTypeDzz.FLYD3.order
                weight = c[14]
                tw[1] = weight
                return tw
            } else {
                tw[0] = 0
                tw[1] = 0
                return tw
            }

            16 ->
            if (c[0] + 1 == c[2] && c[2] + 1 == c[4] && c[4] + 1 == c[6] && c[6] + 1 == c[8] && c[8] + 1 == c[10] && c[10] + 1 == c[12] && c[12] + 1 == c[14] && c[14] < 15 &&//连

                    c[0] == c[1] && c[2] == c[3] && c[4] == c[5] && c[6] == c[7] && c[8] == c[9] && c[10] == c[11] && c[12] == c[13] && c[14] == c[15]) {
                tw[0] = cardTypeDzz.PAIRS8.order
                weight = c[15]
                tw[1] = weight
                return tw
            } else if (c[0] + 1 == c[3] && c[3] + 1 == c[6] && c[6] + 1 == c[9] && c[0] == c[2] && c[3] == c[5] && c[6] == c[8] && c[9] == c[11]/*c[11]<15 &&*/) {
                tw[0] = cardTypeDzz.FLYX4.order
                weight = c[11]
                tw[1] = weight
                return tw
            } else if (c[1] + 1 == c[4] && c[4] + 1 == c[7] && c[7] + 1 == c[10] && c[1] == c[3] && c[4] == c[6] && c[7] == c[9] && c[10] == c[12]/*c[12]<15 &&*/) {
                tw[0] = cardTypeDzz.FLYX4.order
                weight = c[12]
                tw[1] = weight
                return tw
            } else if (c[2] + 1 == c[5] && c[5] + 1 == c[8] && c[8] + 1 == c[11] && c[2] == c[4] && c[5] == c[7] && c[8] == c[10] && c[11] == c[13]/*c[13]<15 &&*/) {
                tw[0] = cardTypeDzz.FLYX4.order
                weight = c[13]
                tw[1] = weight
                return tw
            } else if (c[3] + 1 == c[6] && c[6] + 1 == c[9] && c[9] + 1 == c[12] && c[3] == c[5] && c[6] == c[8] && c[9] == c[11] && c[12] == c[14]/*c[14]<15 &&*/) {
                tw[0] = cardTypeDzz.FLYX4.order
                weight = c[14]
                tw[1] = weight
                return tw
            } else if (c[4] + 1 == c[7] && c[7] + 1 == c[10] && c[10] + 1 == c[13] && c[4] == c[6] && c[7] == c[9] && c[10] == c[12] && c[13] == c[15]/*c[15]<15 &&*/) {
                tw[0] = cardTypeDzz.FLYX4.order
                weight = c[15]
                tw[1] = weight
                return tw
            } else {
                tw[0] = 0
                tw[1] = 0
                return tw
            }

            18 ->
            if (c[0] + 1 == c[2] && c[2] + 1 == c[4] && c[4] + 1 == c[6] && c[6] + 1 == c[8] && c[8] + 1 == c[10] && c[10] + 1 == c[12] && c[12] + 1 == c[14] && c[14] + 1 == c[16] && c[16] < 15 &&//连

                    c[0] == c[1] && c[2] == c[3] && c[4] == c[5] && c[6] == c[7] && c[8] == c[9] && c[10] == c[11] && c[12] == c[13] && c[14] == c[15] && c[16] == c[17]) {
                tw[0] = cardTypeDzz.PAIRS9.order
                weight = c[17]
                tw[1] = weight
                return tw
            } else if (c[0] + 1 == c[3] && c[3] + 1 == c[6] && c[6] + 1 == c[9] && c[9] + 1 == c[12] && c[12] + 1 == c[15] && //c[11]<15 &&

                    c[0] == c[2] && c[3] == c[5] && c[6] == c[8] && c[9] == c[11] && c[12] == c[14] && c[15] == c[17]) {
                tw[0] = cardTypeDzz.FLY6.order
                weight = c[17]
                tw[1] = weight
                return tw
            } else {
                tw[0] = 0
                tw[1] = 0
                return tw
            }

            20 ->
            if (c[0] + 1 == c[2] && c[2] + 1 == c[4] && c[4] + 1 == c[6] && c[6] + 1 == c[8] && c[8] + 1 == c[10] && c[10] + 1 == c[12] && c[12] + 1 == c[14] && c[14] + 1 == c[16] && c[16] + 1 == c[18] && c[18] < 15 &&//连

                    c[0] == c[1] && c[2] == c[3] && c[4] == c[5] && c[6] == c[7] && c[8] == c[9] && c[10] == c[11] && c[12] == c[13] && c[14] == c[15] && c[16] == c[17] && c[18] == c[19]) {
                tw[0] = cardTypeDzz.PAIRS0.order
                weight = c[19]
                tw[1] = weight
                return tw
            } else if (c[0] + 1 == c[3] && c[3] + 1 == c[6] && c[6] + 1 == c[9] && c[9] + 1 == c[12] && c[0] == c[2] && c[3] == c[5] && c[6] == c[8] && c[9] == c[11] && c[12] == c[14]/*c[12]<15 &&*/) {
                tw[0] = cardTypeDzz.FLYX5.order
                weight = c[14]
                tw[1] = weight
                return tw
            } else if (c[1] + 1 == c[4] && c[4] + 1 == c[7] && c[7] + 1 == c[10] && c[10] + 1 == c[13] && c[1] == c[3] && c[4] == c[6] && c[7] == c[9] && c[10] == c[12] && c[13] == c[15]/*c[13]<15 &&*/) {
                tw[0] = cardTypeDzz.FLYX5.order
                weight = c[15]
                tw[1] = weight
                return tw
            } else if (c[2] + 1 == c[5] && c[5] + 1 == c[8] && c[8] + 1 == c[11] && c[11] + 1 == c[14] && c[2] == c[4] && c[5] == c[7] && c[8] == c[10] && c[11] == c[13] && c[14] == c[16]/*c[14]<15 &&*/) {
                tw[0] = cardTypeDzz.FLYX5.order
                weight = c[16]
                tw[1] = weight
                return tw
            } else if (c[3] + 1 == c[6] && c[6] + 1 == c[9] && c[9] + 1 == c[12] && c[12] + 1 == c[15] && c[3] == c[5] && c[6] == c[8] && c[9] == c[11] && c[12] == c[14] && c[15] == c[17]/*c[15]<15 &&*/) {
                tw[0] = cardTypeDzz.FLYX5.order
                weight = c[17]
                tw[1] = weight
                return tw
            } else if (c[4] + 1 == c[7] && c[7] + 1 == c[10] && c[10] + 1 == c[13] && c[13] + 1 == c[16] && c[4] == c[6] && c[7] == c[9] && c[10] == c[12] && c[13] == c[15] && c[16] == c[18]/*c[16]<15 &&*/) {
                tw[0] = cardTypeDzz.FLYX5.order
                weight = c[18]
                tw[1] = weight
                return tw
            } else if (c[5] + 1 == c[8] && c[8] + 1 == c[11] && c[11] + 1 == c[14] && c[14] + 1 == c[17] && c[5] == c[7] && c[8] == c[10] && c[11] == c[13] && c[14] == c[16] && c[17] == c[19]/*c[17]<15 &&*/) {
                tw[0] = cardTypeDzz.FLYX5.order
                weight = c[19]
                tw[1] = weight
                return tw
            } else if (c[0] + 1 == c[3] && c[3] + 1 == c[6] && c[6] + 1 == c[9] && c[0] == c[2] && c[3] == c[5] && c[6] == c[8] && c[9] == c[11] && c[12] == c[13] && c[14] == c[15] && c[16] == c[17] && c[18] == c[19]/*c[11]<15 &&*/) {
                tw[0] = cardTypeDzz.FLYD4.order
                weight = c[11]
                tw[1] = weight
                return tw
            } else if (c[2] + 1 == c[5] && c[5] + 1 == c[8] && c[8] + 1 == c[11] && c[2] == c[4] && c[5] == c[7] && c[8] == c[10] && c[11] == c[13] && c[0] == c[1] && c[14] == c[15] && c[16] == c[17] && c[18] == c[19]/*c[13]<15 &&*/) {
                tw[0] = cardTypeDzz.FLYD4.order
                weight = c[13]
                tw[1] = weight
                return tw
            } else if (c[4] + 1 == c[7] && c[7] + 1 == c[10] && c[10] + 1 == c[13] && c[4] == c[6] && c[7] == c[9] && c[10] == c[12] && c[13] == c[15] && c[0] == c[1] && c[2] == c[3] && c[16] == c[17] && c[18] == c[19]/*c[15]<15 &&*/) {
                tw[0] = cardTypeDzz.FLYD4.order
                weight = c[15]
                tw[1] = weight
                return tw
            } else if (c[6] + 1 == c[9] && c[9] + 1 == c[12] && c[12] + 1 == c[15] && c[6] == c[8] && c[9] == c[11] && c[12] == c[14] && c[15] == c[17] && c[0] == c[1] && c[2] == c[3] && c[4] == c[5] && c[18] == c[19]/*c[17]<15 &&*/) {
                tw[0] = cardTypeDzz.FLYD4.order
                weight = c[17]
                tw[1] = weight
                return tw
            } else if (c[8] + 1 == c[11] && c[11] + 1 == c[14] && c[14] + 1 == c[17] && c[8] == c[10] && c[11] == c[13] && c[14] == c[16] && c[17] == c[19] && c[0] == c[1] && c[2] == c[3] && c[4] == c[5] && c[6] == c[7]/*c[19]<15 &&*/) {
                tw[0] = cardTypeDzz.FLYD4.order
                weight = c[19]
                tw[1] = weight
                return tw
            } else {
                tw[0] = 0
                tw[1] = 0
                return tw
            }
        }
        tw[0] = 0
        tw[1] = 0
        return tw
    }

    fun Done(cardsC: IntArray, cardsF: IntArray): Boolean {

        val calltw = TypeWeight(cardsC)
        val donetw = TypeWeight(cardsF)

        if (calltw[0] == donetw[0]) {
            if (donetw[1] > calltw[1])
                return true
            else return false

        }

        if (calltw[0] > donetw[0])
            return false

        if (calltw[0] < donetw[0]) {
            if (calltw[0] < 80 && donetw[0] < 80)
                return false
            else return true
        }
        return false
    }



}
