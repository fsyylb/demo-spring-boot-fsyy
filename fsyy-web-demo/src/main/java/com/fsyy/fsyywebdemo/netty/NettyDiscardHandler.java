package com.fsyy.fsyywebdemo.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class NettyDiscardHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        try{
            Utils.println("收到消息，丢弃如下：");
            while(in.isReadable()){
                System.out.print((char) in.readByte());
            }
            System.out.println();
            Utils.println("");  // 换行
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
