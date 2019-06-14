package com.shine.amodel

data class ResultMsg(
        var ReturnCode: String = "",//授權結果代碼
        var ReturnMsg:String="",  //訊息描述
        var AuthCode:String="",  //交易授權碼
        var TradeSeq:String = "",  // 交易序號
        var InGameSaveType: String = "",//buf类型
        var PayResult:String="",         //交易結果代碼 交易成功為 3; 交易失敗為 0
        var FacTradeSeq:String="",//廠商交易序號
        var PaymentType:String="",//付費方式
        var Amount:String="",    //金额
        var Currency:String="",//幣別
        var MyCardTradeNo:String="",   //1.PaymentType = INGAME 時，傳 MyCard 卡片號碼
                                    // 2.PaymentType = COSTPOINT 時，傳會員扣點交易序號，格式為 MMS 開頭+數字
                                    // 3.其餘 PaymentType 為 Billing 小額付款交易，傳 Billing 交易序號
                                    /*特別注意:  交易時，同一個 MyCard  卡片號
                                     碼 、 會員扣點交易序號和 Billing  交易序號 只
                                     能被儲值成功一次，請廠商留意，以免造成
                                     重複 儲 值 的情形。
                                    */
       var MyCardType:String="", //通路代碼 PaymentType = INGAME 時才有值
        var PromoCode:String="",//活動代碼 配合促銷活動辨識使用。
        var Serialld:String="",
        var Hash:String="",
        var customerId:String=""  //充值会员号
)