package com.shine.amodel

//blindness 是否是大小盲  小盲：0 大盲：1 否：2
//Bets：上一家的赌注
//allIn：是否allIn 是：1  否：2
//count：是否是第二圈allin 是：1 否：2
//discard:是否弃牌  是：1 否2：
//di：底
data class Texas(var blindness: Int, var bets: Int = 0, var allIn: Int, var count: Int, var discard: Int, var di: Int)