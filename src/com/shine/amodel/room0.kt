package com.shine.amodel

import com.shine.agent.Agent
import java.util.*
import java.util.concurrent.ScheduledExecutorService

/**
 *  Create by Colin
 *  Date:2018/6/6.
 *  Time:11:58
 */
class room0() : Room {
    override val switchAi: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val serviceScheduled: ScheduledExecutorService
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun SeatNext(seat: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun SeatLast(seat: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun GameFapai() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun Status(seat: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun TimeOut_DoAi() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override var cardMount: IntArray
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override fun RoundCount() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun RoundRestart() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override var armyBoo: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override val rid: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val creator: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val numMax: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val numMin: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val type: String
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val timeCreate: Long
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val di: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val roundMax: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val pwd: String
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override var numCur: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override var timeWait: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override var roundCur: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override var isStart: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override val arrSeats: IntArray
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val arrPlayers: Array<User?>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val arrLeavers: IntArray
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val mapRoom: MutableMap<Int, Room>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val mapUserRoom: MutableMap<Int, Room>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val mapHalfRoom: MutableMap<Int, Room>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val switchLog: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val agentRoom: Agent
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val all: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val cmd: String
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.


    override fun HasUserSeat(user: User): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun OnClientClose(user: User) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun OnUserSit(user: User): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun OnUserLeave(seat: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun OnUserReady(seat: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun Log() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun RoomLeave() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun RoomDelete() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun RoomSeatSync() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun SendData(seat: Int, msg: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun Msg(result: Any?, seat: Any?, detail: Any?, data: Any?, command: Any?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun RoundStart() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun RoundEnd() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun RoundReset() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val rand: Random
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun Ai() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val mTime: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
}