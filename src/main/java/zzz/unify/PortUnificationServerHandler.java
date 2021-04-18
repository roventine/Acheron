package zzz.unify;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.util.List;

public class PortUnificationServerHandler extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // Will use the first five bytes to detect a protocol.
        if (byteBuf.readableBytes() < 5) {
            return;
        }
        final int magic1 = byteBuf.getUnsignedByte(byteBuf.readerIndex());
        final int magic2 = byteBuf.getUnsignedByte(byteBuf.readerIndex() + 1);

        // 判断是不是HTTP请求
        if (isHttp(magic1, magic2)) {
            System.out.println("this is a http msg");
            switchToHttp(channelHandlerContext);
        } else {
            System.out.println("this is a socket msg");
            // 当成TCP请求处理
            ChannelPipeline p = channelHandlerContext.pipeline();

            ByteBuf delimiter = Unpooled.copiedBuffer("\n".getBytes());
            p.addLast(new DelimiterBasedFrameDecoder(8192, delimiter))
                    .addLast(new SocketMsgDecoder())
                    .addLast(new SocketMsgHandler());
            // 将自身移除掉
            p.remove(this);
        }
    }

    /**
     * 跳转到http处理
     *
     * @param ctx
     */
    private void switchToHttp(ChannelHandlerContext ctx) {
        ChannelPipeline p = ctx.pipeline();
        p.addLast(new HttpRequestDecoder())
                .addLast(new HttpObjectAggregator(65536))
                .addLast(new HttpResponseEncoder())
                .addLast(new HttpMsgHandler());

        p.remove(this);
    }


    /**
     * 判断请求是否是HTTP请求
     *
     * @param magic1 报文第一个字节
     * @param magic2 报文第二个字节
     * @return
     */
    private boolean isHttp(int magic1, int magic2) {
        return magic1 == 'G' && magic2 == 'E' || // GET
                magic1 == 'P' && magic2 == 'O' || // POST
                magic1 == 'P' && magic2 == 'U' || // PUT
                magic1 == 'H' && magic2 == 'E' || // HEAD
                magic1 == 'O' && magic2 == 'P' || // OPTIONS
                magic1 == 'P' && magic2 == 'A' || // PATCH
                magic1 == 'D' && magic2 == 'E' || // DELETE
                magic1 == 'T' && magic2 == 'R' || // TRACE
                magic1 == 'C' && magic2 == 'O';   // CONNECT
    }

}