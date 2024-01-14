package com.cchangy.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.junit.Test;

import java.util.Random;

/**
 * 预设长度编解码器
 * <p>
 * maxFrameLength: 最大长度
 * lengthFieldOffset: 长度偏移量
 * lengthFieldLength: 长度占用字节数
 * lengthAdjustment: 长度调整字节数
 * initialBytesToStrip: 长度剥离字节数
 *
 * @author cchangy
 * @date 2022/02/17
 */
public class NettyLengthFieldBasedFrameDecoder {

    @Test
    public void testLengthFieldBasedFrameDecoder() {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024, 0, 1, 0, 1),
                new LoggingHandler(LogLevel.DEBUG)
        );

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        Random r = new Random();
        char c = 'a';
        for (int i = 0; i < 10; i++) {
            byte length = (byte) (r.nextInt(16) + 1);
            // 先写入长度
            buffer.writeByte(length);
            // 再
            for (int j = 1; j <= length; j++) {
                buffer.writeByte((byte) c);
            }
            c++;
        }
        embeddedChannel.writeInbound(buffer);
    }
}
