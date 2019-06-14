package com.shine.agent

import com.shine.controller.aHall.Hall
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

class MessageReceive : INotifierBase {

    private var agent: WeakReference<Agent>
    private var msg: String? = null

    override fun run() = run(this)

    constructor(message: String?, agent: Agent) {
        msg = message
        this.agent = WeakReference(agent)
    }

    fun MessageReceive(message: String, agent: Agent) {
        msg = message
        this.agent = WeakReference(agent)
    }

    fun run(msg: MessageReceive) {

        if (msg.msg.equals("")){

            print(".")
            return
        }else if (msg.msg=="status"){
            Hall.getStatus()
            return
        }

        //do handle
        try {
            val json = JSONObject(msg.msg)
            var agent = msg.agent.get()

            if (!json.has("command")){
                println("########____json-nokey-command____#######")
                return
            }

            agent?.let {
                Hall.HandleHallIn(it, json)
            }


            //println("j=" + json)
        }catch (e : JSONException){
            e.printStackTrace()}
    }
}