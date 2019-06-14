package com.shine.aservice.util

import com.alibaba.fastjson.JSONObject
import com.shine.Config
import com.shine.amodel.ResultMsg
import com.shine.controller.poker.Landlords.tool.JSONTool
import org.apache.commons.codec.digest.DigestUtils
import org.apache.log4j.Logger
import java.net.URLEncoder

object RechargeUtil {
    var logger = Logger.getLogger("RechargeUtil")

    @JvmStatic
    fun main(args: Array<String>) {
        var data= mutableMapOf<String,String>()
//        data.put("FacServiceId","tailp")
////        data.put("FacTradeSeq", Date().time.toString()+String.format("%09d", Random().nextInt(9999))+"4960")
//        data.put("TradeType","1")
//        data.put("CustomerId","4900")
//        data.put("ProductName","钻石")
//        data.put("Amount","50")
//        data.put("Currency","TWD")
        data.put("AuthCode","8C7B5584F014FA3151B29DA0F1314A558444043959E5C533153E3AF4FE6B5E9DD0E89790A3BC57BE4A662FD5B77792E5E1B32D392D6A471A45000C394C098018")
//       println(getParamHaveHash(data))
//        data.put("Hash", getParam(data))
//       println( JSONTool.toObj(JSONTool.toJson(data)!!,Map::class.java))
//       println(sendHttp(data))
//        var st="50E30DA4169B76E0A5B1F3840EA91FA487784AFC286F777128F0AC2A91D65B1A1D55741CC1F53AF57F7A40A7B90E39C0B476D4B33A66671EE1B32D392D6A471A45000C394C098018"
        println(JSONObject.parseObject(HTTPUtils.post(Config.URL_FOR_PaymentConfirm,data,"UTF-8"), ResultMsg::class.java))
    }



    //驗證 MyCard 交易結果
    fun  sendHttp(authCode:String,url:String):ResultMsg{

        var data= mutableMapOf<String,String>()
        data.put("AuthCode",authCode)
        var res:ResultMsg
        try {
            JSONTool
            res=JSONObject.parseObject(HTTPUtils.post(url,data,"UTF-8"), ResultMsg::class.java)
        }catch (ex:Exception){
            logger.error(ex.message)
            throw Exception(ex.message)
        }
        println("发送请求URL："+url)
        return res
    }
    fun getParamHaveHash(data:Map<String,String>):Map<String,String>{
        var map= mutableMapOf<String,String>()
        map.putAll(data)
        var sb=StringBuffer()
        for((k,v)in data){
            if(k.contains("Name")||k.contains("name")){
                sb.append(  URLEncoder.encode(v).toLowerCase())
            }else{
                sb.append(v)
            }
        }
        sb.append(Config.key)
//        println(sb.toString())
        map.put("Hash",SHA256(sb.toString()))
        return map
    }
    //SHA256对 参数进行加密  加密前线把参数转换urlunicode 编码
    fun SHA256(param:String):String{
        var pwd256= DigestUtils.sha256Hex(param)
        var pwd= StringBuffer(pwd256)
        return pwd.toString()
    }

}