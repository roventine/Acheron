package zzz.auxiliary;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


import java.nio.charset.StandardCharsets;


public class EchoClientHandler extends SimpleChannelInboundHandler<String> {

    private final String msg;

    public EchoClientHandler(String msg) {
        this.msg = msg;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(msg);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        System.out.println(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
       ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
