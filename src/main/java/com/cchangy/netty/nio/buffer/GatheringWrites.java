package com.cchangy.netty.nio.buffer;

import com.cchangy.netty.util.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 集中写示例
 *
 * @author cchangy
 * @date 2023/04/22
 */
@Slf4j
public class GatheringWrites {

    public static void main(String[] args) {
        try (RandomAccessFile file = new RandomAccessFile("3parts.txt", "rw")) {
            ByteBuffer b1 = ByteBuffer.allocate(4);
            ByteBuffer b2 = ByteBuffer.allocate(4);

            b1.put("four".getBytes());
            b2.put("five".getBytes());

            ByteBufferUtil.debugAll(b1);
            ByteBufferUtil.debugAll(b2);

            FileChannel channel = file.getChannel();

            // 切换为读模式
            b1.flip();
            b2.flip();

            // 指定开始写的位置，否则会从头开始写
            channel.position(11);
            channel.write(new ByteBuffer[]{b1, b2});
        } catch (IOException e) {
            log.error("error: ", e);
        }
    }
}
