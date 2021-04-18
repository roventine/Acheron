package zzz.unify;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;

public class HttpMsgHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpRequest)) {
            System.out.println("不是http请求");
            throw new RuntimeException("该请求不是一个http请求，拒绝处理");
        }

        FullHttpRequest request = (FullHttpRequest) msg;
        ByteBuf content = request.content();
        String body = content.toString(StandardCharsets.UTF_8);
        System.out.println(body);

        handleResp(ctx, "ok", HttpResponseStatus.OK);
        request.release();
    }

    /**
     * 处理响应
     *
     * @param ctx    通道上下文对象
     * @param data   响应的数据
     * @param status 响应的http状态码
     */
    private void handleResp(ChannelHandlerContext ctx, String data, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer(data, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}