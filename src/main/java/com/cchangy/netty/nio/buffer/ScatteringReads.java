package com.cchangy.netty.nio.buffer;

import com.cchangy.netty.util.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 分散读取示例
 *
 * @author cchangy
 * @date 2023/04/22
 */
@Slf4j
public class ScatteringReads {

    public static void main(String[] args) {
        try (RandomAccessFile file = new RandomAccessFile("3parts.txt", "r")) {
            ByteBuffer b1 = ByteBuffer.allocate(3);
            ByteBuffer b2 = ByteBuffer.allocate(3);
            ByteBuffer b3 = ByteBuffer.allocate(3);

            FileChannel channel = file.getChannel();
            channel.read(new ByteBuffer[]{b1, b2, b3});

            ByteBufferUtil.debugAll(b1);
            ByteBufferUtil.debugAll(b2);
            ByteBufferUtil.debugAll(b3);
        } catch (IOException e) {
            log.error("error: ", e);
        }
    }
}
