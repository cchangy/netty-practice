package com.cchangy.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.junit.Test;

/**
 * 固定分隔符解码器
 *
 * @author cchangy
 * @date 2022/02/17
 */
public class NettyLineBasedFrameDecoder {

    @Test
    public void testLineBasedFrameDecoder() {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LineBasedFrameDecoder(1024),
                new LoggingHandler(LogLevel.DEBUG)
        );

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        for (int i = 0; i < 10; i++) {
            buffer.writeBytes(("abcdefg" + i).getBytes());
            if (i % 2 == 0) {
                // 写入换行符
                buffer.writeByte(10);
            }
        }
        embeddedChannel.writeInbound(buffer);
    }
}
