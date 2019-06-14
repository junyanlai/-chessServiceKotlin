package com.shine.controller.poker.cdd

object LogicCdd {

    val MASK_COLOR = 0xF0
    val MASK_VALUE = 0x0F

    val cardList = intArrayOf(
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D,//梅花
            0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D,//方块
            0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x2D,//红桃
            0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x3B, 0x3C, 0x3D //黑桃
    )

    @JvmStatic
    fun main(args: Array<String>) {
        /*    println(cardTypeCdd.FIVE.order)
             var gb= mutableListOf<Int>(1,5,7,9,11) //顺子
             println("同花 TONGHUA 13>>"+jugdeType(gb.toIntArray()))
             var g= mutableListOf<Int>(1,2,3,4,5) //顺子
             println("同花顺 FIVESC 7>>"+jugdeType(g.toIntArray()))
             var gs= mutableListOf<Int>(1,18,35,4,21) //顺子
             println("顺 FIVE 4>>"+jugdeType(gs.toIntArray()))

             var ge= mutableListOf<Int>(10,11,12,13,1) //顺子
             println("同花顺A 在后 FIVESC 7>>"+jugdeType(ge.toIntArray()))
             var gee= mutableListOf<Int>(10,27,44,13,17) //顺子
             println("顺 A在后 FIVE 4>>"+jugdeType(gee.toIntArray()))

             var b= mutableListOf<Int>(1,17,33,2,18) //三代二单
             println("葫芦 HULU5 >>"+jugdeType(b.toIntArray()))
             var l= mutableListOf<Int>(1,17,33,49,2) //炸  c4
             println("金刚 FOUR16 》>"+jugdeType(l.toIntArray()))
             var i= mutableListOf<Int>(1) //单牌 c1
             println("单牌 SINGLE 1>>"+jugdeType(i.toIntArray()))
             var j= mutableListOf<Int>(1,17) //对  c2
             println("对牌 DOUNLE 2>>"+jugdeType(j.toIntArray()))
             var k= mutableListOf<Int>(1,17,33) //三联不带  c3
             println("三联 THREE3 >>"+jugdeType(k.toIntArray()))
             var ls= mutableListOf<Int>(1,17,33,49) //炸  c4
             println("炸弹 FOUR>>"+jugdeType(ls.toIntArray()))*/
        var ss= mutableListOf<Int>(13,45,3,19,2,18,34,50)
        var s= mutableListOf<Int>(28, 44, 60, 1, 49)
//        println("aaaa"+disposeFIVE(ss.toIntArray(),s.toIntArray()).toList())
//        println(disposeHULU(ss.toIntArray(),s.toIntArray()).toList())
//        println(disposeDOUNLE(ss.toIntArray(),s.toIntArray()).toList())
        println(disposeHULU(ss.toIntArray(),s.toIntArray()).toList())
    }

    //葫芦
    fun disposeHULU(myCard: IntArray, cards: IntArray): IntArray {
        var card = mutableListOf<Int>()
        var car = getcardTypeCdd(cards[0])
        for (i in 0..myCard.size - 1) {
            if (getcardTypeCdd(i) > car) {
                for (j in 0..myCard.size - 1) {
                    if (getcardTypeCdd(myCard[i]) == getcardTypeCdd(myCard[j])) {
                        card.add(myCard[j])
                        if (card.size == 3) {
                            card.addAll(getTwoCard(myCard, card[0]))
                            if (card.size == 5)
                                return card.toIntArray()
                        }
                    } else {
                        card.clear()
                    }
                }
            }
        }
        if (card.size != 5) {
            if (getTieZhi(myCard).size == 5) {
                return getTieZhi(myCard)
            }
            if (getTongHuaShun(myCard, card[0]).size == 5) {
                return getTongHuaShun(myCard, card[0])
            }
        }
        return IntArray(0)
    }

    fun disposeTHREE(myCard:IntArray,cards: IntArray):IntArray{
        var card= mutableListOf<Int>()
        var car=cards[0]
        var bi=0
        var c= mutableListOf<Int>()
        for(i in myCard){
            if(getcardTypeCdd(i)>getcardTypeCdd(car)){
               for(j in myCard){
                   if(getcardTypeCdd(i)==getcardTypeCdd(j)){
                        card.add(j)
                   }
               }
            }
            if(card.size==3){
                return card.toIntArray()
            }else{
                card.clear()
            }
        }
        return card.toIntArray()
    }
    fun getAllcard(cards:IntArray):IntArray{
        var ca= mutableListOf<Int>()
        var car=getMinCard(cards)
        for(i in cards){
            if(getcardTypeCdd(i)== getcardTypeCdd(car)){
                ca.add(i)
            }
        }
        return ca.toIntArray()
    }
    fun getMinCard(cards: IntArray):Int{
        if(cards.size==1) return cards[0]
        var car=cards[0]
        var card=getcardTypeCdd(cards[0])
        for(i in cards){
            if(getcardTypeCdd(i)<card){
                card=getcardTypeCdd(i)
                car=i
            }
        }
        return car
    }
    //最大牌
    fun getMaxCard(cards: IntArray):Int{
        var car=cards[0]
        var card=getcardTypeCdd(cards[0])
        for(i in cards){
            if(getcardTypeCdd(i)>card){
                card=getcardTypeCdd(i)
                car=i
            }
        }
        return car
    }
    fun getTieZhi(myCard: IntArray):IntArray{
        var card=mutableListOf<Int>()
        for(i in 0..myCard.size-1){
            for(j in 0..myCard.size-1){
                if(getcardTypeCdd(myCard[i])==getcardTypeCdd(myCard[j])){
                    card.add(myCard[j])
                    if(card.size==4){
                        card.add(getOneCard(myCard,card[0]))
                        if(card.size==5)
                            return card.toIntArray()
                    }
                }else{
                    card.clear()
                }
            }
        }
        return card.toIntArray()
    }
    fun getTongHuaShun(myCard: IntArray,minCard:Int):IntArray{
        var card=mutableListOf<Int>()
        var meihua= mutableListOf<Int>()
        var fangpian= mutableListOf<Int>()
        var hongtao= mutableListOf<Int>()
        var heitao= mutableListOf<Int>()
        for(i in myCard){
            when(getHuaSe(i)){
                0->{
                    meihua.add(i)
                }
                1->{
                    fangpian.add(i)
                }
                2->{
                    hongtao.add(i)
                }
                3->{
                    heitao.add(i)
                }
            }
        }
        if(meihua.size>=5&&getHuaSe(minCard)<=0){
            card.clear()
            var lis=0
            var start=0
            when{
                getcardTypeCdd(minCard)==12->start=1
                getcardTypeCdd(minCard)==12->start=2
                else ->start=getcardTypeCdd(minCard)
            }
            for(i in meihua){
                if(getcardTypeCdd(i)>start){
                    lis=getcardTypeCdd(i)
                    break
                }
            }
            var count=0
            for(i in 0..meihua.size-1){
                if((lis+count)==getcardTypeCdd(meihua[i])){
                    card.add(meihua[i])
                    count++
                    if(count==5){
                        return card.toIntArray()
                    }
                }else if (lis== getcardTypeCdd(meihua[i])||card.size!=0){
                    count=1
                    card.clear()
                    card.add(meihua[i])
                    lis=getcardTypeCdd(meihua[i])
                }
            }
            return card.toIntArray()
        }
        if(fangpian.size>=5&&getHuaSe(minCard)<=0){
            card.clear()
            var lis=0
            var start=0
            when{
                getcardTypeCdd(minCard)==12->start=1
                getcardTypeCdd(minCard)==12->start=2
                else ->start=getcardTypeCdd(minCard)
            }
            for(i in fangpian){
                if(getcardTypeCdd(i)>start){
                    lis=getcardTypeCdd(i)
                    break
                }
            }
            var count=0
            for(i in 0..fangpian.size-1){
                if((lis+count)==(getcardTypeCdd(fangpian[i]))){
                    card.add(fangpian[i])
                    count++
                    if(count==5){
                        return card.toIntArray()
                    }
                }else if (lis== getcardTypeCdd(fangpian[i])){
                    card.clear()
                    card.add(fangpian[i])
                    lis=getcardTypeCdd(fangpian[i])
                }
            }
            return card.toIntArray()
        }
        if(hongtao.size>=5&&getHuaSe(minCard)<=0){
            card.clear()
            var lis=0
            var start=0
            when{
                getcardTypeCdd(minCard)==12->start=1
                getcardTypeCdd(minCard)==12->start=2
                else ->start=getcardTypeCdd(minCard)
            }
            for(i in hongtao){
                if(getcardTypeCdd(i)>start){
                    lis=getcardTypeCdd(i)
                    break
                }
            }
            var count=0
            for(i in 0..hongtao.size-1){
                if((lis+count)==(getcardTypeCdd(hongtao[i]))){
                    card.add(hongtao[i])
                    count++
                    if(count==5){
                        return card.toIntArray()
                    }
                }else if (lis== getcardTypeCdd(hongtao[i])){
                    card.clear()
                    card.add(hongtao[i])
                    lis=getcardTypeCdd(hongtao[i])
                }
            }
            return card.toIntArray()
        }
        if(heitao.size>=5&&getHuaSe(minCard)<=0){
            card.clear()
            var lis=0
            var start=0
            when{
                getcardTypeCdd(minCard)==12->start=1
                getcardTypeCdd(minCard)==12->start=2
                else ->start=getcardTypeCdd(minCard)
            }
            for(i in heitao){
                if(getcardTypeCdd(i)>start){
                    lis=getcardTypeCdd(i)
                    break
                }
            }
            var count=0
            for(i in 0..heitao.size-1){
                if((lis+count)==(getcardTypeCdd(heitao[i]))){
                    card.add(heitao[i])
                    count++
                    if(count==5){
                        return card.toIntArray()
                    }
                }else if (lis== getcardTypeCdd(heitao[i])){
                    card.clear()
                    card.add(heitao[i])
                    lis=getcardTypeCdd(heitao[i])
                }
            }
            return card.toIntArray()
        }
        return IntArray(0)
    }
    fun getOneCard(myCard:IntArray,card:Int):Int{
        for(i in myCard){
            if(getcardTypeCdd(i)!=getcardTypeCdd(card)){
                return i
            }
        }
        return myCard[0]
    }
    fun getTwoCard(myCard:IntArray,card:Int):MutableList<Int>{
        var cards= mutableListOf<Int>()
        for(i in 0..myCard.size-1){
            if(getcardTypeCdd(i)!=getcardTypeCdd(card)){
                for(j in 0..myCard.size-1){
                    if(getcardTypeCdd(myCard[i])==getcardTypeCdd(myCard[j])
                            &&getcardTypeCdd(card)!=getcardTypeCdd(myCard[j])){
                        cards.add(myCard[j])
                        if(cards.size==2){
                            return cards
                        }
                    }else{
                        cards.clear()
                    }
                }
            }
        }
        return cards
    }
    fun SeatNext(seat: Int) = if (seat == (4 - 1)) 0 else (seat + 1)
    //0 梅花 1方块 2红桃 3 黑桃
    fun getHuaSe(card:Int):Int{
        when{
            card in listOf(1 ,2 ,3 ,4 ,5 ,6 ,7 ,8 ,9 ,10,11,12,13)->return 0
            card in listOf(17,18,19,20,21,22,23,24,25,26,27,28,29)->return 1
            card in listOf(33,34,35,36,37,38,39,40,41,42,43,44,45)->return 2
            card in listOf(49,50,51,52,53,54,55,56,57,58,59,60,61)->return 3

        }
        return 0
    }
    fun jugdeType(cards: IntArray): cardTypeCdd {
        val len=cards.size
        if(len>5) return cardTypeCdd.ERROR
        if (len <= 4) {    //如果第一个和最后个相同，说明全部相同
            if (len> 0 && getcardTypeCdd(cards[0]) == getcardTypeCdd(cards[len - 1])) {
                when (len) {
                    1 -> return cardTypeCdd.SINGLE
                    2 -> return cardTypeCdd.DOUNLE
                    3 -> return cardTypeCdd.THREE
                    4 -> return cardTypeCdd.FOUR
                }
            }
            else {
                cardTypeCdd.ERROR
            }
        }
         when{
             len==5 && getcardTypeCdd(cards[3]) == getcardTypeCdd(cards[4])
                     &&getcardTypeCdd(cards[0]) == getcardTypeCdd(cards[1])
                     &&getcardTypeCdd(cards[1]) == getcardTypeCdd(cards[2])->{
                 return cardTypeCdd.HULU
             }
             len==5  &&getcardTypeCdd(cards[0]) == getcardTypeCdd(cards[1])
                     &&getcardTypeCdd(cards[1]) == getcardTypeCdd(cards[2])
                     &&getcardTypeCdd(cards[2]) == getcardTypeCdd(cards[3])
                     &&getcardTypeCdd(cards[4]) != getcardTypeCdd(cards[3])->{
                 return cardTypeCdd.FOUR1
             }
             len==5  ->{
                 return lian(len,cards)
             }
             len==5 && getcardTypeCdd(cards[3]) == getcardTypeCdd(cards[4])
                     &&getcardTypeCdd(cards[0])==getcardTypeCdd(cards[1])
                     &&getcardTypeCdd(cards[1])==getcardTypeCdd(cards[2])->{
                 return cardTypeCdd.HULU
             }
         }
        return lian(len,cards)
    }
    fun tonghua(cards: IntArray):cardTypeCdd{
       when{
           getHuaSe(cards[0]) == getHuaSe(cards[1])
                   &&getHuaSe(cards[1]) == getHuaSe(cards[2])
                   &&getHuaSe(cards[2]) == getHuaSe(cards[3])
                   &&getHuaSe(cards[4]) == getHuaSe(cards[3])->{
               return cardTypeCdd.TONGHUA
           }
       }
        return cardTypeCdd.ERROR
    }
    fun lian(len:Int,list:IntArray):cardTypeCdd {
        var lis=getcardTypeCdd(list[0])
        var count=0
            for(i in 0..(len-1)){
                if((lis+count)==getcardTypeCdd(list[i])
                        &&getcardTypeCdd(list[i])!=14){
                    count++
                }
            }
            when{
                count==len
                        &&getHuaSe(list[0]) == getHuaSe(list[1])
                        &&getHuaSe(list[1]) == getHuaSe(list[2])
                        &&getHuaSe(list[2]) == getHuaSe(list[3])
                        &&getHuaSe(list[4]) == getHuaSe(list[3])->{
                    return cardTypeCdd.FIVESC
                }
                count==len ->return cardTypeCdd.FIVE
                (count+3)==len
                        &&(getcardTypeCdd(list[0])==12||getcardTypeCdd(list[0])==13)
                        &&getHuaSe(list[0]) == getHuaSe(list[1])
                        &&getHuaSe(list[1]) == getHuaSe(list[2])
                        &&getHuaSe(list[2]) == getHuaSe(list[3])
                        &&getHuaSe(list[4]) == getHuaSe(list[3]) ->{
                    return cardTypeCdd.FIVESC
                }
                (count+3)==len
                        &&(getcardTypeCdd(list[0])==12||getcardTypeCdd(list[0])==13)->{
                    return cardTypeCdd.FIVE
                }
            }
        return tonghua(list)
    }
    //如果 集合里包含A 并且 不是 123456 这种排列 就重新测一下
    fun inAchouse(list:IntArray){

    }

    //根据卡片数字返回类型
    fun getcardTypeCdd(card:Int):Int{
        when{
            card in listOf<Int>(3,19,35,51)->return 1
            card in listOf<Int>(4,20,36,52)->return 2
            card in listOf<Int>(5,21,37,53)->return 3
            card in listOf<Int>(6,22,38,54)->return 4
            card in listOf<Int>(7,23,39,55)->return 5
            card in listOf<Int>(8,24,40,56)->return 6
            card in listOf<Int>(9,25,41,57)->return 7
            card in listOf<Int>(10,26,42,58)->return 8
            card in listOf<Int>(11,27,43,59)->return 9
            card in listOf<Int>(12,28,44,60)->return 10
            card in listOf<Int>(13,29,45,61)->return 11
            card in listOf<Int>(1,17,33,49)->return 12
            card in listOf<Int>(2,18,34,50)->return 13
        }
        return 0
    }
    fun Seat3(cardArray :Array<MutableList<Int>>):Int{
        for (i in 0..3)
            if (cardArray[i].contains(0x03))
                return i
        return -1
    }
    fun GetCardMount():IntArray{
        val cm= cardList.toMutableList()
        cm.shuffle()
        return cm.toIntArray()
    }
    fun MountSlice(cardMount:IntArray,cardArray :Array<MutableList<Int>>){
        var seat=-1
        for (i in 0..3)
            for (j in 0..12)
                cardArray[i].add(cardMount[i*13+j])
    }

    fun V(card: Int)=card and MASK_VALUE
    fun C(card: Int)=card and MASK_COLOR shr 4
    fun SV(card:Int):Int{

        val v= V(card)
        if (v<3)    return v+13
        if (card==0x4E)     return v+2
        return v
    }

    fun Weight(card:Int)= SV(card)*100+C(card)

    fun Sort(cards: IntArray){

        val len=cards.size
        for (i in 0 until len - 1)
            for (j in 0 until len - 1 - i) {
                if (SV(cards[j]) > SV(cards[j + 1])) {
                    cards[j] = cards[j] xor cards[j + 1]
                    cards[j+1] = cards[j] xor cards[j + 1]
                    cards[j] = cards[j] xor cards[j + 1]
                }
                if (SV(cards[j]) == SV(cards[j + 1]) && C(cards[j]) > C(cards[j + 1])) {
                    cards[j] = cards[j] xor cards[j + 1]
                    cards[j+1] = cards[j] xor cards[j + 1]
                    cards[j] = cards[j] xor cards[j + 1]
                }
            }
    }
    fun Sort2(cards: IntArray){
        if (Weight(cards[0]) > Weight(cards[1])) {
            cards[0] = cards[0] xor cards[1]
            cards[1] = cards[0] xor cards[1]
            cards[0] = cards[0] xor cards[1]
        }
    }
    fun SortShun(cards: IntArray){

        val len=cards.size
        for (i in 0 until len - 1)
            for (j in 0 until len - 1 - i)
                if (V(cards[j]) > V(cards[j + 1])) {
                    cards[j] = cards[j] xor cards[j + 1]
                    cards[j+1] = cards[j] xor cards[j + 1]
                    cards[j] = cards[j] xor cards[j + 1]
                }
    }

    fun Type(cards:IntArray):cardTypeCdd{
        val len=cards.size
        when (len) {
            1 -> return cardTypeCdd.SINGLE
            2 -> return cardTypeCdd.DOUNLE
            3 -> return cardTypeCdd.THREE
            5,7 -> return Type57(cards)
            8 -> return cardTypeCdd.WUHU
            9 -> return cardTypeCdd.WUHU2
            13 -> return cardTypeCdd.DRAGON
        }
        return cardTypeCdd.ERROR
    }
    fun Type57(cards:IntArray):cardTypeCdd{

        val len=cards.size
        Sort(cards)
        if (len==5){
            if (V(cards[1]) == V(cards[2]) && V(cards[2]) == V(cards[3]))
                return cardTypeCdd.FOUR1
            else if (V(cards[0]) == V(cards[1]))
                return cardTypeCdd.HULU
            else return cardTypeCdd.FIVE
        }
        if (len==7){
            if (isFour2(cards)) return cardTypeCdd.FOUR2
            else    return cardTypeCdd.FIVESC
        }

        return cardTypeCdd.ERROR
    }


    fun isFour2(cards: IntArray):Boolean{
        var count = 0
        for (i in 0..6) {
            if (V(cards[i]) == 2) count ++
            if (count == 4) return true
        }
        return false
    }
    fun HasCard(cards: IntArray,hands:MutableList<Int>)=hands.containsAll(cards.asList())

    fun getWei(arr: IntArray, num: Int): Int {
        var result = 0
        for (i in 0 until num)
            result += V(arr[i]) * Math.pow(10.0, (num - i - 1).toDouble()).toInt()
        return result
    }

    fun Done(cardC: IntArray, cardF:IntArray):Boolean{

        val lenC=cardC.size
        val lenF=cardF.size

        if (lenC>lenF) return false
        if (lenC<lenF) {
            if (lenF<5) return false
            if (lenF>5) return true
            if (lenF==5 && Type57(cardF).order>5)  return true
            else return false
        }

        return SLDone(cardC,cardF)
    }
    fun SLDone(cardC: IntArray, cardF:IntArray):Boolean{

        val len = cardC.size
        Sort(cardC)
        Sort(cardF)
        when(len){
            1   ->  return Weight(cardF[0]) > Weight(cardC[0])
            2   ->  return SLDone2(cardC, cardF)
            3   ->  return SV(cardF[0]) > SV(cardC[0])
            5   ->  return SLDone5(cardC, cardF)
            7   ->  return SLDone7(cardC, cardF)
            13   ->  return SLDone13(cardC, cardF)
        }
        return false
    }

    fun SLDone2(cardC: IntArray, cardF:IntArray):Boolean{

        Sort2(cardC)
        Sort2(cardF)
        if (SV(cardC[0]) == SV(cardF[0])) {
            if (V(cardC[0]) == 2) {
                if (cardC[0] == 0x32 || cardC[1] == 0x32 || cardC[1] == 0x4E) return false
                else return true
            } else return Weight(cardF[1]) > Weight(cardC[1])
        } else return SV(cardF[0]) > SV(cardC[0])

        return false
    }
    fun SLDone5(cardC: IntArray, cardF:IntArray):Boolean{

        val typrC= Type57(cardC)
        val typrF= Type57(cardF)
        if (typrF>typrC)    return true
        if (typrF<typrC)    return false

        //shunzi
        if (typrF.order==4){
            if (cardC[4] == 0x4E && cardF[4] != 0x4E) {

                val call = getWei(cardC, 4)
                val done4 = getWei(cardC, 4)
                val done = getWei(cardF, 5)


                if (call == 3456)
                    return false
                else if ((call == 3452 || call == 3462 || call == 3562 || call == 4562) && done == 34562)
                    return Weight(cardF[4]) > Weight(cardC[3])
                else if ((call == 3452 || call == 3462 || call == 3562 || call == 4562) && done == 34512)
                    return false
                else if (call == 3452 || call == 3462 || call == 3562 || call == 4562)
                    return false
                else if ((call == 3412 || call == 3512 || call == 4512 || call == 3451) && done == 34562)
                    return true
                else if ((call == 3412 || call == 3512 || call == 4512) && done == 34512)
                    return Weight(cardF[4]) > Weight(cardC[3])
                else if (call == 3451 && done == 34512)
                    return false
                else if (call == 3412 || call == 3512 || call == 4512 || call == 3451)
                    return true
                else {
                    //鬼在中
                    if (SV(cardC[0]) + 4 == SV(cardC[3])) {
                        return Weight(cardF[4]) > Weight(cardC[3])
                    } else if (SV(cardC[0]) + 3 == SV(cardC[3])) {//鬼在尾
                        return if (call == done4)
                            false
                        else
                            Weight(cardF[3]) > Weight(cardC[3])
                    }
                }
            }//末顺有鬼
            else if (cardC[4] != 0x4E && cardF[4] == 0x4E) {

                val call = getWei(cardC, 5)
                val call4 = getWei(cardC, 4)
                val done = getWei(cardF, 4)

                if (call == 34562 && cardC[4] == 0x32) return false
                else if (call == 34562 && done == 3456) return true
                else if (call == 34562 && (done == 3452 || done == 3462 || done == 3562 || done == 4562))
                    return Weight(cardF[3]) > Weight(cardC[4])
                else if (call == 34562 && (done == 3412 || done == 3512 || done == 4512 || done == 34552))
                    return false
                else if (call == 34562) return false
                else if (call == 34512 && done == 3451 && cardC[4] == 0x32) return false
                else if (call == 34512 && done == 3451) return true
                else if (call == 34512 && (done == 3412 || done == 3512 || done == 4512))
                    return Weight(cardF[3]) > Weight(cardC[4])
                else if (call == 34512 && (done == 3456 || done == 3452 || done == 3462 || done == 3562 || done == 4562))
                    return true
                else if (call == 34512) return true
                else {//鬼在中
                    if (SV(cardF[0]) + 4 == SV(cardF[3])) {
                        return Weight(cardF[3]) > Weight(cardC[4])
                    } else if (SV(cardF[0]) + 3 == SV(cardF[3])) {//鬼在尾
                        return if (call4 == done) return true
                        else return Weight(cardF[3]) > Weight(cardC[3])
                    }
                }
            } else {//无鬼

                SortShun(cardC)
                SortShun(cardF)
                if (V(cardF[4]) == 6 && V(cardC[4]) == 6)
                    return  C(cardF[4]) > C(cardF[4])
                else if (V(cardF[4]) == 6 && V(cardC[4]) != 6)
                    return true
                else if (V(cardF[4]) != 6 && V(cardC[4]) == 6)
                    return  false
                else if (V(cardF[4]) == 13 && V(cardC[4]) == 13)
                    return  Weight(cardF[0]) > Weight(cardC[0])
                else
                    return  Weight(cardF[4]) > Weight(cardC[4])
            }
        }

        if (typrF.order in 5..6) return SV(cardF[2]) > SV(cardC[2])
        return false
    }
    fun SLDone7(cardC: IntArray, cardF:IntArray):Boolean{

        val typrC= Type57(cardC)
        val typrF= Type57(cardF)
        if (typrF>typrC)    return true
        if (typrF<typrC)    return false
        return Weight(cardF[0]) > Weight(cardC[0])
    }
    fun SLDone13(cardC: IntArray, cardF:IntArray):Boolean{
        if (cardC[12] == 0x4E && cardF[12] != 0x4E) {
            if (V(cardC[0]) == 1 && V(cardC[1]) == 3) return true
            if (V(cardC[0]) == 1 && V(cardC[1]) == 2) return cardF[1] > cardC[1]
            if (V(cardC[0]) == 2) return cardF[1] > cardC[0]
        }
        else if (cardC[12] != 0x4E && cardF[12] == 0x4E) {
            if (V(cardF[0]) == 1 && V(cardF[1]) == 3) return false
            if (V(cardF[0]) == 1 && V(cardC[1]) == 2) return cardF[1] > cardC[1]
            if (V(cardF[0]) == 2) return cardF[0] > cardC[1]
        }
        else return cardF[1] > cardC[1]
        return false
    }

    fun Multiple(cardArray :Array<MutableList<Int>>):IntArray{

        val start=IntArray(4)
        for (i in 0..3){
            var size=cardArray[i].size
            when(size){
                in (0..7) ->  size*=1
                0,1,2,3,4,5,6,7 ->  size*=1
                8,9             ->  size*=2
                10,11,12        ->  size*=3
                13              ->  size*=4
            }
            if (size>7 && cardArray[i].contains(0x32))
                size*=2
            start[i]=size
        }

        val end=IntArray(4)
        for (i in 0..3)
            end[i]=start.sum()-(start[i]*4)

        return end
    }

}
