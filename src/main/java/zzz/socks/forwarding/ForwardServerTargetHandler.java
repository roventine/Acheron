package zzz.socks.forwarding;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ForwardServerTargetHandler extends ChannelInboundHandlerAdapter {

    private final Channel sourceChannel;

    public ForwardServerTargetHandler(Channel sourceChannel) {
        this.sourceChannel = sourceChannel;
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) {
        sourceChannel.config().setAutoRead(ctx.channel().isWritable());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        sourceChannel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                future.channel().close();
                ctx.close();
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        sourceChannel.close();
    }
}
