package com.shine.agent

import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelPipeline
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.ssl.SslContext
import io.netty.handler.timeout.IdleStateHandler

class WebSocketServerInitializer(private val sslCtx: SslContext?) : ChannelInitializer<SocketChannel>() {

    @Throws(Exception::class)
    public override fun initChannel(ch: SocketChannel) {
        val pipeline = ch.pipeline()
        if (sslCtx != null) pipeline.addLast(sslCtx.newHandler(ch.alloc()))

        pipeline.addLast(HttpServerCodec())
        pipeline.addLast(HttpObjectAggregator(65536))
        pipeline.addLast(IdleStateHandler(30, 0, 0))
        //pipeline.addLast(new GameAgent());
        pipeline.addLast(Agent())
    }
}