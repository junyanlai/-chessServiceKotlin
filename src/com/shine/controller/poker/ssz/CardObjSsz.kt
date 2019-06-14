package com.shine.controller.poker.ssz

/**
 * Boo 是一个标志位 > 单牌：0 对子：1 三条：2 顺子：3 同花：4 葫芦：5 四梅：6 同花顺：7 皇家同花顺：8
 * 当为0的时候，说明里面的牌组未进行分类，初始化为0
 */
data class CardObj(var Card: IntArray, var OldValue: IntArray, var Boo: Int)

data class CardObjDz(var card: Pair<Int, IntArray>, var newCard: IntArray, var Boo: Int)