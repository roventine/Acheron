package zzz.socks.forwarding;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


import java.net.InetSocketAddress;

public class ForwardServer {

    static final int PORT = 8009;

    public static void main(String[] args) throws InterruptedException {

        InetSocketAddress destAddr = new InetSocketAddress("127.0.0.1",8007);
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.config().setAutoRead(false);
                        socketChannel.pipeline()
                                .addLast(new ForwardServerSourceHandler(destAddr));
                    }
                })
                .bind(PORT)
                .sync();
    }
}
