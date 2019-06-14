package com.shine.agent

import com.shine.amodel.User
import com.shine.controller.aHall.Hall
import io.netty.channel.ChannelHandlerContext

class Agent : ChannelInboundHandler() {

    var m_heartTime: Long = 0
    val m_heartLength = 3600 * 1000
    var mCtx: ChannelHandlerContext? = null

    var CID = 0
    var UID = 0
    var user = User()
    var m_bRemove = false
    private val IP = super.IP()

    var signnick = ""
    var signmail = ""
    var signcode = 0
    var signtimeout = 0

    var logined = false     //switchHallin for same cid with different uid
    var relogined = false


    init {

        CID = __id++
        agents.put(CID, this)
        m_heartTime = System.currentTimeMillis()

        KillDeadSessions()
    }

    companion object {

        var __id = 1
        var notifier = NotifierBase()
        var agents: MutableMap<Int, Agent> = HashMap()
        var MaxConnection = 0
        var CurConnection = 0

        fun setUser(cid: Int?, user: User) {
            agents[cid]?.user = user
        }

        fun clientAdd(agent: Agent) = agents.put(agent.CID, agent)

        fun clientDel(agent: Agent) = agents.remove(agent.CID)

        fun clientFind(cid: Int) = agents[cid]

        fun DropAll() {

            for ((cid, agent) in agents)
                agent?.let { it.Disconnect();agents.remove(cid) }
        }

        @Synchronized
        fun KillDeadSessions() {

            for ((cid, agent) in agents) {
                if (agent != null && (agent.m_bRemove || agent.isTimeOut())) {
                    //println("remove dead cid cid: " + cid)
                    agent.Disconnect()
                    agents.remove(cid)
                }
            }
        }
    }


    fun Disconnect() {
        mCtx?.disconnect()
        mCtx = null
    }

    fun Send(CID: Int, msg: String) = agents[CID]?.SendMessage(msg)
    fun SendAll(msg: String) = agents.mapValues { entry -> entry.value?.SendMessage(msg) }


    fun onClientConnect() {
        clientAdd(this)
        // println("$MaxConnection Accept Session From IP : " + IP + " \tConnections: " + agents.size)
    }

    fun onClientClose() {

        Hall.OnClientClose(this)
        //   Hall.userAgent.remove(this.user.uid)  //删除用户
        //      println("用户连接断开")
        clientDel(this)
        m_heartTime = System.currentTimeMillis()
        //  println("$MaxConnection OnDisconnect :\t Connection: $CurConnection | $CID" + "_" + agents.size)
    }

    //fun getUID() = if (user != null) user?.uid else 0

    fun Remove() {
        m_bRemove = true
    }

    fun isTimeOut() = (m_heartTime > 0) && (System.currentTimeMillis() - m_heartTime) > m_heartLength


    override fun onMessage(message: String) {
        //println("in>>>" + message)
        try {
            m_heartTime = System.currentTimeMillis()
            if (message.equals("{\"command\":\"heart\"}", ignoreCase = true))
                SendMessage(message)
            else
                notifier.Notify(MessageReceive(message, this))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun channelRegistered(ctx: ChannelHandlerContext?) {
        super.channelRegistered(ctx)
        CurConnection++
        if (MaxConnection < CurConnection) MaxConnection = CurConnection
        mCtx = ctx
        onClientConnect()
    }

    override fun channelUnregistered(ctx: ChannelHandlerContext?) {

        //println("执行取消注册")
       // println("客户端已经断开连接>>>${ctx!!.channel().isActive()}")

        super.channelUnregistered(ctx)
        CurConnection--
        if (CurConnection < 0) println("OH")
        onClientClose()
    }
}