package zzz.socks.forwarding;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


import java.net.InetSocketAddress;

public class Forwarder {

    public static void main(String[] args) throws InterruptedException {

        int port = 8008;

        InetSocketAddress destAddr = new InetSocketAddress("127.0.0.1",port);
        System.out.println("Listen on port : [" + port + "]");
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.config().setAutoRead(false);
                        socketChannel.pipeline()
                                .addLast(new ServerHandler(destAddr));
                    }
                })
                .bind(port)
                .sync();
    }
}
