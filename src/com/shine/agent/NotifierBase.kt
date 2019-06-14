package com.shine.agent

import java.util.*
import java.util.concurrent.LinkedBlockingQueue

class NotifierBase {

    private var _thread: Thread
    private  var _queue: Queue<INotifierBase>

    constructor() {
        _queue = LinkedBlockingQueue()
        _thread = Thread(Runnable {
            while (true) {
                val msg = poll()
                if (msg != null) {
                    try {
                        msg.run()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } else {
                    try {
                        Thread.sleep(2)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                }
            }
        })
        _thread.start()
    }

    fun Count()=_queue.size
    private fun poll()= _queue.poll()

    fun Notify(msg: INotifierBase) {
        synchronized(this) {
            _queue.add(msg)
        }
    }
}