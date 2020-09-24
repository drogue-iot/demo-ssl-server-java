package io.drogue.sslserver;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;


/**
 * Created by maanadev on 5/18/17.
 */
public class ServerHandlersInit extends ChannelInitializer<SocketChannel> {


    public ServerHandlersInit() {

    }

    protected void initChannel(SocketChannel socketChannel) throws Exception {

        SslHandler sslHandler = SSLHandlerProvider.getSSLHandler();

        socketChannel.pipeline().addLast(
                sslHandler,
                new HttpServerCodec(),
                new HttpObjectAggregator(1048576),
                new SimpleChannelInboundHandler<FullHttpRequest>() {
                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
                        ByteBuf content = channelHandlerContext.alloc().buffer();
                        content.writeCharSequence("the password is always sw0rdf1sh", Charset.forName("UTF-8"));
                        DefaultHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
                        channelHandlerContext.writeAndFlush(response);
                        channelHandlerContext.channel().close();
                    }
                });
    }

}
