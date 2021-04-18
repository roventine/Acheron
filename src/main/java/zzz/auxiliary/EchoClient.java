package zzz.auxiliary;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;


public final class EchoClient {

    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "1080"));



    public static void main(String[] args) throws Exception {

        String msg = "greeting";
//        for(int i=0;i<10;i++){
//            msg = msg + msg;
//        }

        EventLoopGroup group = new NioEventLoopGroup();
        try {

            Bootstrap b = new Bootstrap();
            String finalMsg = msg;
//            System.out.println(finalMsg.length());
            b.group(group)
             .channel(NioSocketChannel.class)
             .option(ChannelOption.TCP_NODELAY, true)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch)  {
                     ChannelPipeline p = ch.pipeline();
                     p.addLast(
                             new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4),
                             new StringDecoder(CharsetUtil.UTF_8),
                             new LengthFieldPrepender(4),
                             new StringEncoder(CharsetUtil.UTF_8),
                             new EchoClientHandler(finalMsg));
                 }
             });

            ChannelFuture f = b.connect(HOST, PORT).sync();
            f.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully();
        }
    }
}
