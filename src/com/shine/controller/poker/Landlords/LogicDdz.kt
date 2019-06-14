package com.shine.controller.poker.Landlords

import com.alibaba.fastjson.JSON
import com.shine.controller.poker.Landlords.tool.JSONTool
import com.shine.controller.poker.ddz.TableDdz
import org.json.JSONArray
import org.json.JSONObject
import sun.management.MemoryUsageCompositeData.getMax
import sun.security.provider.certpath.CertId
import java.util.*
import javax.smartcardio.Card



object LogicDdz {
    var allPermission= mutableMapOf<Int,List<Int>>()
    val cardList = intArrayOf(
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D,//梅花
            0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D,//方块
            0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x2D,//红桃
            0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x3B, 0x3C, 0x3D,//黑桃
            0x4E, 0x4F
    )
    var photoType=PhotoType()
    class PhotoType(){
        var photoNum:Int=0
        var multiple:Int=0
    }


    fun getAllCarMap(myCard: IntArray): MutableMap<Int,List<Int>> {
        var typeMap= mutableMapOf<Int,List<Int>>()
        for(i in 1..15){
            var ca = mutableListOf<Int>()
            for(j in myCard){
                if(LogicDdz.getcardTypeDdz(j)==i){
                   ca.add(j)
                }
            }
            typeMap[i]=ca
        }

        return typeMap
    }

    /**
     * 打乱扑克
     */
    fun disorganizePokers():List<Int>{
        var cr =cardList.toMutableList()
            cr.shuffle()
        return cr.toList()
    }


    fun   getPorker(key:Int):List<Int>?{
        if(!allPermission.isEmpty()){
            return allPermission.get(key)
        }
        var creadList=disorganizePokers().toMutableList()
        for(j in 0..2) {
            var cards= mutableListOf<Int>()
            for (i in 0..16) {
                cards.add(creadList.get(0))
                creadList.removeAt(0)
            }
            var card= mutableListOf<Int>()
            cardSort(card,cards,1)
            allPermission.put(j,card)
        }
        allPermission.put(4,creadList)
        return allPermission.get(key)!!
    }
    fun cardSort(card:MutableList<Int>,cards:MutableList<Int>,count: Int){
        var count=count
        if(count==16) return
        for(i in cards){
            if(getcardTypeDdz(i)==count){
                card.add(i)
            }
        }
        count+=1
        cardSort(card,cards,count)
    }

//
    @JvmStatic
    fun main(args: Array<String>) {
//        var i= mutableListOf<Int>(1) //单牌 c1
//        println("单牌 c1>>"+jugdeType(i))
//        var j= mutableListOf<Int>(1,17) //对  c2
//        println("对牌 c2>>"+jugdeType(j))
//        var k= mutableListOf<Int>(1,17,33) //三联不带  c3
//        println("三联不带 c3>>"+jugdeType(k))
//        var l= mutableListOf<Int>(1,17,33,49) //炸  c4
//        println("炸弹 c4>>"+jugdeType(l))
//        var d= mutableListOf<Int>(1,17,33,2) //三代一  c31
//        println("三代一c31>>"+jugdeType(d))
//        var n= mutableListOf<Int>(1,17,33,2,18) //三代一  c32
//        println("三代对c32>>"+jugdeType(n))
//        var e= mutableListOf<Int>(1,17,33,49,2,3) //4代二        411
//        println("四带二单c411>>"+jugdeType(e))
//        var f= mutableListOf<Int>(1,17,33,49,2,18,3,19) //4代二 422
//        println("四带二对c422>>"+jugdeType(f))
//        var g= mutableListOf<Int>(1,2,3,4,5,6,7,8) //顺子      c123
//        println("顺子 c123>>"+jugdeType(g))
//        var h= mutableListOf<Int>(1,17,2,18,3,19,4,20) //连队  c1122
//        println("连队 c1122>>"+jugdeType(h))
//        var c= mutableListOf<Int>(1,17,33,2,18,34) //三飞       飞机111222
//        println("飞机三飞c111222>>"+jugdeType(c))
//        var b= mutableListOf<Int>(1,17,33,2,18,34,4,5) //三代二单       飞机
//        println("飞机三代二单c11122234>>"+jugdeType(b))
//        var a= mutableListOf<Int>(1,17,33,2,18,34,4,20,5,21) //三代二对 飞机
//        println("飞机三代二对c1112223344>>"+jugdeType(a))
//        var o= mutableListOf<Int>(78,79) //王炸
//        println("王炸 c2>>"+jugdeType(o))
//        var aa= mutableListOf<Int>(78,79) //连队  c1122
//        println("出牌结果：："+jugdeType(aa))
    }

    //判断牌型
    fun jugdeType(list: MutableList<Int>): cardTypeDdz {
        //因为之前排序过所以比较好判断
        val len = list.size
        //单牌,对子，3不带，4个一样炸弹
        if (len <= 4) {    //如果第一个和最后个相同，说明全部相同
            if (list.size > 0 && getcardTypeDdz(list[0]) == getcardTypeDdz(list[len - 1])) {
                when (len) {
                    1 -> return cardTypeDdz.c1
                    2 -> return cardTypeDdz.c2
                    3 -> return cardTypeDdz.c3
                    4 -> return cardTypeDdz.c4
                }
            }
            //双王,化为对子返回
            if (len == 2 &&((getcardTypeDdz(list[0]) ==14||getcardTypeDdz(list[1]) ==14)&&(getcardTypeDdz(list[0])==15||getcardTypeDdz(list[1])==15)))
                return cardTypeDdz.c22
            //当第一个和最后个不同时,3带1
            return if (len == 4 && (getcardTypeDdz(list[0]) == getcardTypeDdz(list[len - 2]) || getcardTypeDdz(list[1]) == getcardTypeDdz(list[len - 1])))
                cardTypeDdz.c31
            else {
                cardTypeDdz.c0
            }
        }
        //当5张以上时，连字，3带2，飞机，2顺，4带2等等
       if (len >= 5) {//现在按相同数字最大出现次数
            //3带2 -----必含重复3次的牌
            if (len == 5 && getcardTypeDdz(list[3]) == getcardTypeDdz(list[4]) && isOnly(len,list) )
                return cardTypeDdz.c32
            //4带2(单,双)
            if (len == 6  && isOnly(len,list))
                return cardTypeDdz.c411
            if (len == 8 && getcardTypeDdz(list[6]) == getcardTypeDdz(list[7])
                         && getcardTypeDdz(list[4]) == getcardTypeDdz(list[5])
                         && isOnly(len,list))
                return cardTypeDdz.c422
            //出现顺子 连对，飞机带单 飞机带对 通过他来判断
                return  liandui(len,list)
       }
        return cardTypeDdz.c0
    }
    fun feiji(len:Int,list:MutableList<Int>):cardTypeDdz{
        var isYes=false
        var lis=getcardTypeDdz(list[0])
        var count=0
        for(i in 0..(len-1) step 2){
            if((lis+count)==getcardTypeDdz(list[i])) {
                isYes = true
                count++
            }
        }
        if(isYes &&count*3==len) return cardTypeDdz.c111222

        if((len-count*3)==count)return cardTypeDdz.c11122234

        if((len-count*3)==(count*2))return cardTypeDdz.c1112223344
        return cardTypeDdz.c0
    }

    fun liandui(len:Int,list:MutableList<Int>):cardTypeDdz {
        var lis=getcardTypeDdz(list[0])
        var count=0
           if(getcardTypeDdz(list[0])==getcardTypeDdz(list[1])){
               for(i in 0..(len-1) step 2){
                    if((lis+count)==getcardTypeDdz(list[i])
                           &&getcardTypeDdz(list[i])!=14){
                        count++
                    }
               }
           }else{
               for(i in 0..(len-1)){
                   if((lis+count)==getcardTypeDdz(list[i])
                           &&getcardTypeDdz(list[i])!=14){
                       count++
                   }
               }
           }
        when{
            count*2==len
                    ||!list.contains(1)
                    ||!list.contains(17)
                    ||!list.contains(33)
                    ||!list.contains(49)
                    ||!list.contains(2)
                    ||!list.contains(18)
                    ||!list.contains(34)
                    ||!list.contains(50)
            ->return cardTypeDdz.c1122
            count==len
                    ||!list.contains(1)
                    ||!list.contains(17)
                    ||!list.contains(33)
                    ||!list.contains(49)
                    ||!list.contains(2)
                    ||!list.contains(18)
                    ||!list.contains(34)
                    ||!list.contains(50)
            ->return cardTypeDdz.c123
        }
        return feiji(len,list)   //如果 不是连对或者顺子 那就可能是飞机
    }
     fun isOnly(len:Int,list:MutableList<Int>):Boolean {
         var fist=getcardTypeDdz(list[0])
         var isYes=false
         var count=0
         for(i in  0 until len-2){
             isYes=if(fist== getcardTypeDdz(list[i])) true else false
             count++
             if((len==8||len==6)&&count==4){
                return isYes
             }
         }
         return isYes
     }

//    @JvmStatic
//    fun main(args: Array<String>) {
//        for(i in 0..2){
//            println(getPorkers(i))
//        }
//        println(getPorkers(4))
//    }
    /**
     * 获取扑克
     */
    fun  getPorkers(key:Int):List<Int>?{
        if(!allPermission.isEmpty()){
            return allPermission.get(key)
        }
        var creadList=disorganizePokers().toMutableList()
        for(j in 0..2) {
            var cards= mutableListOf<Int>()
            for (i in 0..16) {            //取出牌组存入临时集合
                cards.add(creadList.get(0))
                creadList.removeAt(0) //删除牌栈
            }
            var card= mutableListOf<Int>()
            cardSort(card,cards,1)         //递归排序卡牌
            allPermission.put(j,card)        //存到map中

        }
        allPermission.put(4,creadList)
        return allPermission.get(key)!!.toList()
    }

    //判断是否可以出牌
    fun contrast(superCard: MutableList<Int>,thisCard: MutableList<Int>):Boolean{
        var superCardType= jugdeType(superCard)
        var thisCardType=jugdeType(thisCard)
         when{
             (superCardType==thisCardType)&&thisCardType==cardTypeDdz.c4
                     &&getcardTypeDdz(superCard[0])<getcardTypeDdz(thisCard[0]) ->return true
             (thisCardType==cardTypeDdz.c4||(thisCardType==cardTypeDdz.c2
                     && getcardTypeDdz(thisCard[0])==14))
                     &&superCardType!=thisCardType                              ->return true
             superCardType!= thisCardType                                       -> return false
             superCard.size!= thisCard.size                                     -> return false
            else -> return todetermineTheSize(superCard,thisCard)
         }
        return false
    }

    //判断要出的牌是否是大于上家的
    fun todetermineTheSize(superCard: MutableList<Int>,thisCard: MutableList<Int>):Boolean{
       var isYes=false
        for(i in 0..thisCard.size-1){
            if(thisCard[i]>superCard[i]){
                isYes=true
            }
        }
        return isYes
    }



    //根据卡片数字返回类型   1,17,33,49   2,18,34,50
    fun getcardTypeDdz(card:Int):Int{
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
            card in listOf<Int>(78)->return 14
            card in listOf<Int>(79)->return 15
        }
        return 0
    }
    fun getcardTypeDdzTest(card:Int):Int{
        when{
            card in listOf<Int>(3,19,35,51)->return 3
            card in listOf<Int>(4,20,36,52)->return 4
            card in listOf<Int>(5,21,37,53)->return 5
            card in listOf<Int>(6,22,38,54)->return 6
            card in listOf<Int>(7,23,39,55)->return 7
            card in listOf<Int>(8,24,40,56)->return 8
            card in listOf<Int>(9,25,41,57)->return 9
            card in listOf<Int>(10,26,42,58)->return 10
            card in listOf<Int>(11,27,43,59)->return 11
            card in listOf<Int>(12,28,44,60)->return 12
            card in listOf<Int>(13,29,45,61)->return 13
            card in listOf<Int>(1,17,33,49)->return 14
            card in listOf<Int>(2,18,34,50)->return 15
            card in listOf<Int>(78)->return 16
            card in listOf<Int>(79)->return 16
        }
        return 0
    }
}
