package com.shine.amodel

import com.shine.agent.Agent
import java.util.*
import java.util.concurrent.ScheduledExecutorService

interface Room {

    //basic fist
    val rid: Int                 //编号
    val creator: Int             //房主

    //basic class
    val numMax: Int              //最多人数
    val numMin: Int              //最小人数
    val type: String             //游戏类型
    val timeCreate: Long         //开始时间

    val di: Int                  //底数
    val mTime: Int
    //val tai:Int                 //台数
    val roundMax: Int            //最大回合数
    val pwd: String              //密码

    //things change
    var numCur: Int              //当前人数
    //var status:Int              //状态值
    var timeWait: Int            //等待时间
    var roundCur: Int            //当前回合数
    var armyBoo: Int             //军团战标志 0：未开启 1:已经开启

    var isStart: Boolean         //是否开始
    val arrSeats: IntArray       //座位数组
    val arrPlayers: Array<User?> //玩家数组
    val arrLeavers: IntArray     //leave缓存

    val mapRoom: MutableMap<Int, Room>       //=Hall.mapRoom
    val mapUserRoom: MutableMap<Int, Room>   //=Hall.mapUserRoom
    val mapHalfRoom: MutableMap<Int, Room>   //=RoomController.  mapHalfRoom_maj/mapHalfRoomCdd/mapHalfRoomDzz

    val switchAi: Boolean
    val switchLog: Boolean
    val agentRoom: Agent                    //= RoomController.RoomAgent    agent for send
    val all: Int                             //= 255
    val cmd: String                          //="msg_"+type

    val rand: Random
    val serviceScheduled: ScheduledExecutorService

    fun HasUserSeat(user: User): Int
    fun OnClientClose(user: User)
    fun OnUserSit(user: User): Int            //1=success
    fun OnUserLeave(seat: Int)               //
    fun OnUserReady(seat: Int)               //

    fun Log()
    fun RoomLeave()
    fun RoomDelete()
    fun RoomSeatSync()                      //get saps player numberCurrent

    fun SendData(seat: Int, msg: String)
    fun Msg(result: Any?, seat: Any?, detail: Any?, data: Any?, command: Any?): String

    //LogicMaj Part
    var cardMount: IntArray

    fun SeatNext(seat: Int): Int
    fun SeatLast(seat: Int): Int
    fun GameFapai()

    fun Status(seat: Int)
    fun RoundStart()
    fun RoundEnd()
    fun RoundCount()
    fun RoundReset()
    fun RoundRestart()

    fun Ai()
    fun TimeOut_DoAi()


}