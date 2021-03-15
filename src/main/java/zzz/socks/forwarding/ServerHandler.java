package zzz.socks.forwarding;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


import java.net.InetSocketAddress;
import java.net.SocketAddress;


public class ServerHandler extends ChannelInboundHandlerAdapter {

    private Channel destChannel = null;
    private final SocketAddress destAddr;



    public ServerHandler(SocketAddress destAddr) {
        this.destAddr = destAddr;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

        InetSocketAddress addr = (InetSocketAddress)ctx.channel().remoteAddress();
        String hostIp = addr.getAddress().getHostAddress();
        System.out.println(hostIp);


        new Bootstrap()
                .group(ctx.channel().eventLoop())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline()
                                .addLast(new ClientHandler(ctx.channel()));
                    }
                })
                .connect(destAddr)
                .addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        if (ctx.channel().isActive()) {
                            destChannel = future.channel();
                            ctx.channel().config().setAutoRead(destChannel.isWritable());
                        } else {
                            future.channel().close();
                        }
                    } else {
                        ctx.close();
                    }
                });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (destChannel != null && destChannel.isActive()) {
            destChannel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    future.channel().close();
                    ctx.close();
                }
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (destChannel != null && destChannel.isActive()) {
            destChannel.close();
        }
    }
}