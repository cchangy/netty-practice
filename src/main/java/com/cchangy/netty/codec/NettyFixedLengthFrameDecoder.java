package com.cchangy.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.junit.Test;

/**
 * 固定长度解码器
 *
 * @author cchangy
 * @date 2022/02/17
 */
public class NettyFixedLengthFrameDecoder {

    @Test
    public void testFixedLengthFrameDecoder() {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new FixedLengthFrameDecoder(8),
                new LoggingHandler(LogLevel.DEBUG)
        );

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        for (int i = 0; i < 10; i++) {
            buffer.writeBytes(("abcdefg" + i).getBytes());
        }
        embeddedChannel.writeInbound(buffer);
    }
}
