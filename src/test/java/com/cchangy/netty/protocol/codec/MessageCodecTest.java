package com.cchangy.netty.protocol.codec;

import com.cchangy.netty.protocol.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

/**
 * 自定义消息编解码测试
 *
 * @author cchangy
 * @date 2022/02/18
 */
public class MessageCodecTest {
    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LoggingHandler(),
                new LengthFieldBasedFrameDecoder(
                        1024, 12, 4, 0, 0),
                new MessageCodec()
        );
        // encode
        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");
        channel.writeOutbound(message);

        // decode
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, buf);
        // channel.writeInbound(buf);

        ByteBuf s1 = buf.slice(0, 100);
        ByteBuf s2 = buf.slice(100, buf.readableBytes() - 100);
        s1.retain(); // 引用计数 2
        channel.writeInbound(s1); // release 1
        channel.writeInbound(s2);
    }
}
