package com.cchangy.netty.nio.buffer;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * ByteBuffer 简单示例
 *
 * @author cchangy
 * @date 2022/01/02
 */
@Slf4j
public class ByteBufferQuickStart {

    public static void main(String[] args) {
        // 1. 获取FileChannel，可通过输入输出流或RandomAccessFile来获取
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            // 2. 创建一个字节缓冲区，大小为10个字节
            ByteBuffer buffer = ByteBuffer.allocate(10);
            // 3. 从FileChannel中读取数据，每次最多读取10个字节，写入到buffer中
            while (true) {
                int length = channel.read(buffer);
                // length为本次读取到的字节数
                log.debug("本次读取到的字节数: {}", length);
                if (length == -1) { // 如果等于-1，代表没有读取到数据，则退出循环
                    break;
                }

                // 4. 将buffer切换到读模式
                buffer.flip();
                // 5. 判断是否还有剩余未读的内容，如果有则循环去取，每次读取一个字节
                while (buffer.hasRemaining()) {
                    char str = (char) buffer.get();
                    log.debug("本次读取到的内容: {}", str);
                }
                /**
                 * 6. 切换到写模式
                 * clear：会清空所有数据
                 * compact：为将未读完的数据向前压缩，未读完的数据不会清空
                 */
                buffer.compact();
                // buffer.clear();
            }
        } catch (IOException e) {
            log.error("error", e);
        }
    }
}
