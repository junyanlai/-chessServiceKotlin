package com.shine.aservice.util

import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


object DayGet {

    //获取当前日期
    fun Get_This_DayNum():Int{
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        return  calendar.get(Calendar.DAY_OF_MONTH)
    }
    //获取昨天日期
    fun Get_Relay_DayNum():Int{
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        return if(calendar.get(Calendar.DAY_OF_MONTH)-1==0) getThisMonthCount() else calendar.get(Calendar.DAY_OF_MONTH)-1
    }
//判断是否是闰年 29 还是平年 28
    fun isLeapyear(year: Int): Boolean {
        return if ((year % 4 == 0 && year % 100 != 0 )|| year % 400 == 0) true else false
    }
    fun getThisMonthCount():Int{
        var a= mutableListOf<Int>(1,3,5,7,8,10,12)
        if(a.contains(Get_This_MonthNum())){
            return 31
        }else if (Get_This_MonthNum()==2&&isLeapyear(Get_This_YearNum())){
            return 29
        }else{
            return 28
        }
        return 30
    }
    //获取当前月份
    fun Get_This_MonthNum():Int{
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        return  calendar.get(Calendar.MONTH)+1
    }
    //获取当前年期
    fun Get_This_YearNum():Int{
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        return  calendar.get(Calendar.YEAR)
    }

    fun getYearMonthDay():Int{
        return String.format("%s%s%s",Get_This_YearNum(),Get_This_MonthNum(),Get_This_DayNum()).toInt()
    }

    fun formatDay():String{
        if(Get_This_DayNum()!=1){
            return String.format("%s-%s-%s",Get_This_YearNum(),Get_This_MonthNum(),Get_This_DayNum())
        }else{
            if(Get_This_MonthNum()-1==0){
                return String.format("%s-%s-%s",Get_This_YearNum()-1,12,31)
            }
            return String.format("%s-%s-%s",Get_This_YearNum(),Get_This_MonthNum()-1,Get_Relay_DayNum())
        }
    }
    fun formatDay(key:Int):String{
        if(key>=Get_This_DayNum()){
            return String.format("%s-%s-%s",Get_This_YearNum(),Get_This_MonthNum()-1,key)
        }else{
            return String.format("%s-%s-%s",Get_This_YearNum(),Get_This_MonthNum(),key)
        }
    }



    @JvmStatic
    fun main(args: Array<String>) {
        test()
        getTime()
    }
    val serviceScheduled = Executors.newSingleThreadScheduledExecutor()
    fun  test(){

        val callable = java.lang.Runnable{

//            println("############超时# ")
        }
        serviceScheduled.schedule(callable, 6, TimeUnit.SECONDS)
    }
    var time=6
    fun  getTime(){

        while(time>0){
            try {
                Thread.sleep(1000)
                 time=time-1
//                println(time)
            }catch (ex:Exception){

            }
        }

    }
}