package com.shine.controller.aHall

import com.shine.amodel.User
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

var user= User(123)

var service2 = Executors.newSingleThreadScheduledExecutor()

val able = java.lang.Runnable{
    val service = Executors.newSingleThreadScheduledExecutor()
    step++
    println("step=$step")
    if (step==4 || step==8) service.shutdown()
}

var step=0


    fun run() {
        val service = Executors.newSingleThreadScheduledExecutor()
        val ables = java.lang.Runnable{

            step++
            println("step=$step")
            if (step==4 || step==8) service.shutdown()
        }

        service.scheduleAtFixedRate(ables, 0, 1, TimeUnit.SECONDS)
    }

fun all(){

    run()

    val callable = java.lang.Runnable{
        println("restart")
        run()
    }
    service2.schedule(callable, 7L, TimeUnit.SECONDS)

}





fun main(args: Array<String>) {

   val list= listOf(0,1,2)
    println(list.indexOf(3))

    val a= intArrayOf(0,1,2,3,4,5,6,7,8)
    val a3=a.copyOfRange(0,3).toList()
    val a6=a.copyOfRange(3,6).toList()
    val a9=a.copyOfRange(6,9).toList()

    println("a3=$a3 a6=$a6 a9=$a9")
}

