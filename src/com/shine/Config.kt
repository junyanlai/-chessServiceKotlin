package com.shine

object Config {


    var hostIp = "127.0.0.1"

    val xml_src = "mybatis-config-"

    val xml_shine = xml_src + "shine.xml"
    val xml_taiwan = xml_src + "taiwan.xml"
    //声明
    val mybatis = xml_taiwan



    //测试
    val UrlverifyTest="https://testb2b.mycard520.com.tw/MyBillingPay/v1.1/TradeQuery"
    val UrlPaymentConflrm="https://testb2b.mycard520.com.tw/MyBillingPay/v1.1/PaymentConfirm"
    //正式
    val Urlverify="https://b2b.mycard520.com.tw/MyBillingPay/v1.1/PaymentConfirm"
    val UrlPaymentConflrmText="https://testb2b.mycard520.com.tw/MyBillingPay/v1.1/PaymentConfirm"

    //驗證 MyCard 交易結果   发布台湾的时候需要更改成正式URL
    val URL_FOR_TradeQuery= UrlverifyTest
    val URL_FOR_PaymentConfirm=/*UrlPaymentConflrmText*/UrlPaymentConflrm
    val key="00bd8a0922e59af070ffbe973375f2d981"




}