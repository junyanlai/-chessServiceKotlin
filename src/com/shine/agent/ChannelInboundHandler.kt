package com.shine.agent

import io.netty.handler.codec.http.HttpMethod.GET
import io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST
import io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN
import io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND
import io.netty.handler.codec.http.HttpVersion.HTTP_1_1

import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpUtil
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent
import io.netty.util.CharsetUtil

open class ChannelInboundHandler : SimpleChannelInboundHandler<Any>() {

    var ip: String? = null
    var max = 0
    private var handshaker: WebSocketServerHandshaker? = null
    internal var _chanel: Channel? = null
    fun IP(): String? {
        if (ip != null)
            return ip
        if (_chanel == null)
            return null

        ip = _chanel!!.remoteAddress().toString()
        ip = ip!!.split("\\:".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        this.ip = ip!!.substring(1)
        return this.ip
    }

    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
        if (evt is IdleStateEvent) {//超时事件
            if (evt.state() == IdleState.READER_IDLE) {
                if (max == 3) {
                    //println("准备关闭客户端>>>")
                    //待使用APP测试
                    //channelUnregistered(ctx)
                } else {
                    //println("读取超时>>>${ctx.channel().id()}::准备第${max}次断线重连>>>")
                    max++
                }
            }
        }
        super.userEventTriggered(ctx, evt)
    }


    private fun handleHttpRequest(ctx: ChannelHandlerContext, req: FullHttpRequest) {

        // Handle a bad request.
        if (!req.decoderResult().isSuccess) {
            sendHttpResponse(ctx, req, DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST))
            return
        }

        // Allow only GET methods.
        if (req.method() !== GET) {
            sendHttpResponse(ctx, req, DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN))
            return
        }

        // Send the demo page and favicon.ico
        if ("/" == req.uri()) {
            //println("null as http accept")
            return
        }
        if ("/reload" == req.uri()) {
            //http://127.0.0.1:9100/reload

            val content = Unpooled.copiedBuffer("reload ok", CharsetUtil.UTF_8)
            val res = DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, content)
            res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8")

            HttpUtil.setContentLength(res, content.readableBytes().toLong())
            sendHttpResponse(ctx, req, res)
            return
        } else if ("/sendgonggao" == req.uri()) {
            //http://127.0.0.1:9100/sendgonggao

            val content = Unpooled.copiedBuffer("SendGongGao ok", CharsetUtil.UTF_8)
            val res = DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, content)

            res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8")
            HttpUtil.setContentLength(res, content.readableBytes().toLong())

            sendHttpResponse(ctx, req, res)

            return
        } else if ("/shuishou" == req.uri()) {
            //http://127.0.0.1:9100/sendgonggao

            val content = Unpooled.copiedBuffer("shuishou ok :", CharsetUtil.UTF_8)
            val res = DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, content)

            res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8")
            HttpUtil.setContentLength(res, content.readableBytes().toLong())

            sendHttpResponse(ctx, req, res)

            return
        }
        if ("/favicon.ico" == req.uri()) {
            val res = DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND)
            sendHttpResponse(ctx, req, res)
            return
        }


        val apiHandStr: String? = null
        try {
            //apiHandStr = ApiHandle.Hand(req.uri());
        } catch (e: Exception) {
            System.err.println("???????" + e.stackTrace)
            val content = Unpooled.copiedBuffer("???????", CharsetUtil.UTF_8)
            val res = DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, content)

            res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8")
            HttpUtil.setContentLength(res, content.readableBytes().toLong())

            sendHttpResponse(ctx, req, res)
            return
        }

        if (apiHandStr != null) {
            val content = Unpooled.copiedBuffer(apiHandStr, CharsetUtil.UTF_8)
            val res = DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, content)

            res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8")
            HttpUtil.setContentLength(res, content.readableBytes().toLong())

            sendHttpResponse(ctx, req, res)
            return
        }
        try {

            // Handshake
            val wsFactory = WebSocketServerHandshakerFactory(
                    getWebSocketLocation(req), null, true, 5 * 1024 * 1024)
            handshaker = wsFactory.newHandshaker(req)
            if (handshaker == null)
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel())
            else
                handshaker!!.handshake(ctx.channel(), req)

            _chanel = ctx.channel()
        } catch (e: Exception) {
            System.err.println("???????" + e.stackTrace)

            val content = Unpooled.copiedBuffer("???????", CharsetUtil.UTF_8)
            val res = DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, content)

            res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8")
            HttpUtil.setContentLength(res, content.readableBytes().toLong())

            sendHttpResponse(ctx, req, res)

        }

    }

    private fun handleWebSocketFrame(ctx: ChannelHandlerContext, frame: WebSocketFrame) {

        // Check for closing frame
        if (frame is CloseWebSocketFrame) {
            handshaker!!.close(ctx.channel(), frame.retain() as CloseWebSocketFrame)
            return
        }
        if (frame is PingWebSocketFrame) {
            ctx.write(PongWebSocketFrame(frame.content().retain()))
            return
        }
        if (frame is TextWebSocketFrame) {
            // Echo the frame
            //ctx.write(frame.retain());
            val txt = frame.text()
            onMessage(txt)
            return
        }
        if (frame is BinaryWebSocketFrame) {
            // Echo the frame
            ctx.write(frame.retain())
            return
        }
    }

    open fun onMessage(message: String) {
        message
    }

    fun SendMessage(message: String?) {
        //println("out>>>" + message)
        if (_chanel == null)
            return
        synchronized(this) {
            //ctx.write(frame.retain());
//           var ms= Des.strEnc(message!!,"1","1","1")
            val f = TextWebSocketFrame(message)
            _chanel!!.writeAndFlush(f.retain())
        }
    }

    public override fun channelRead0(ctx: ChannelHandlerContext, msg: Any) {
        if (msg is FullHttpRequest)
            handleHttpRequest(ctx, msg)
        else if (msg is WebSocketFrame)
            handleWebSocketFrame(ctx, msg)
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }

    companion object {

        private val WEBSOCKET_PATH = "/"
        private fun sendHttpResponse(ctx: ChannelHandlerContext, req: FullHttpRequest, res: FullHttpResponse) {

            // Generate an error page if response getStatus signcode is not OK (200).
            if (res.status().code() != 200) {
                val buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8)
                res.content().writeBytes(buf)
                buf.release()
                HttpUtil.setContentLength(res, res.content().readableBytes().toLong())
            }

            // Send the response and close the connection if necessary.
            val f = ctx.channel().writeAndFlush(res)
            if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200)
                f.addListener(ChannelFutureListener.CLOSE)
        }

        private fun getWebSocketLocation(req: FullHttpRequest): String {
            val location = req.headers().get(HttpHeaderNames.HOST) + WEBSOCKET_PATH
            return if (WebSocketServer.SSL)
                "wss://$location"
            else
                "ws://$location"
        }
    }
}