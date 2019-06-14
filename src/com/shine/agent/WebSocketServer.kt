package com.shine.agent

import com.shine.amodel.ArmyStatus
import com.shine.aservice.army.ArmyAdminService
import com.shine.controller.aHall.ArmyWarHall
import com.shine.controller.aHall.Hall
import com.shine.controller.aHall.Hall.ArmySequence
import com.shine.controller.aHall.Hall.gameStatus
import com.shine.controller.aHall.HangUpController.hangRecordAndClear
import com.shine.controller.gamble.saima.ManagerSm
import com.shine.controller.gamble.toubao.ManagerTb
import org.apache.commons.logging.LogFactory
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.SelfSignedCertificate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class WebSocketServer {


    @Throws(Exception::class)
    fun mains(args: Array<String>) {
        main(args)
    }

    companion object {

        internal val SSL = System.getProperty("ssl") != null

        fun Run() {
            var _thread = Thread(Runnable {
                while (true) {
                    try {
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                    try {
                        //Dthall.getInstance().OnDeal(1000);
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            })
            _thread.start()
        }

        val service = Executors.newSingleThreadScheduledExecutor()

        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {

            Run()
            val sslCtx: SslContext?
            if (SSL) {
                val ssc = SelfSignedCertificate()
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build()
            } else
                sslCtx = null

            val tcp_port = 9147

            val log = LogFactory.getLog("rootLogger")
            log.error("Init main")

            val bossGroup = NioEventLoopGroup(1)
            val workerGroup = NioEventLoopGroup()
            try {
                //军团战开启初始化
                ArmyAction()

                //授权检测模块
                Skynet.Skynet()
                //清理挂机记录
                RecordAndClear()

                val b = ServerBootstrap()
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel::class.java)
                        .childHandler(WebSocketServerInitializer(sslCtx))

                val ch = b.bind(tcp_port).sync().channel()

                println("Open your web to " + (if (SSL) "https" else "http") + "://127.0.0.1:" + tcp_port + '/'.toString())

                ManagerTb.Create()
                ManagerSm.Create()

                ch.closeFuture().sync()

            } finally {
                bossGroup.shutdownGracefully()
                workerGroup.shutdownGracefully()
            }


        }


        fun ArmyAction() {

            // 初始化军团游戏开始顺序
            ArmySequence.push(5)
            ArmySequence.push(4)
            ArmySequence.push(3)
            ArmySequence.push(2)
            ArmySequence.push(1)

            //检查军团游戏开始时间
            checkWarArmyTime()
        }


        fun checkWarArmyTime() {
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val armyAction = ArmyAdminService.querywarAction(ArmySequence.first)
            val type = armyAction.type

            val time = df.parse(armyAction.time).time - Date().time

            //更新此游戏在数据库的中时间设定为下周
            updateArmyActionTime(ArmySequence.first)

            //如果军团战开启的时间小于实际时间就会报这个异常
            if (time < 0) {
                throw Throwable("Legion Activities Setting Time Too Small")
            }

            val runnable = Runnable {
                //init game action by Status
                gameStatus.push(ArmyStatus("close", type))
                gameStatus.push(ArmyStatus("stop", type))
                gameStatus.push(ArmyStatus("start", type))
                //初始化军团排行榜
                ArmyWarHall.armyRanking()

                Hall.startAction(2)
            }
            service.schedule(runnable, time, TimeUnit.MILLISECONDS)
        }

        fun updateArmyActionTime(type: Int) {
            val armyAction = ArmyAdminService.querywarAction(type)
            val df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val time = LocalDateTime.parse(armyAction.time, df)
            val newTime = time.plusHours(168)
            val fmt24 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val timeStr = newTime.format(fmt24)
            ArmyAdminService.updateArmyActionTime(type, timeStr)
        }

        //清理挂机记录
        fun RecordAndClear() {
            GlobalScope.launch {
                while (true) {
                    delay(28800000L)
                    hangRecordAndClear()
                }
            }
        }


    }

}