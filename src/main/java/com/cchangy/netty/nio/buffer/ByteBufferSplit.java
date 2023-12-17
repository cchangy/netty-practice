package com.cchangy.netty.nio.buffer;

import com.cchangy.netty.util.ByteBufferUtil;

import java.nio.ByteBuffer;

/**
 * byteBuffer分割处理粘包半包示例
 *
 * @author cchangy
 * @date 2023/04/22
 */
public class ByteBufferSplit {

    public static void main(String[] args) {
        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("Hello,World\nI'm zhangsan\nHo".getBytes());
        split(source);
        source.put("w are you?\n".getBytes());
        split(source);
    }

    private static void split(ByteBuffer source) {
        source.flip(); // 切换为读模式
        for (int i = 0; i < source.limit(); i++) {
            if (source.get(i) == '\n') {
                int length = i + 1 - source.position();
                ByteBuffer target = ByteBuffer.allocate(length);
                for (int j = 0; j < target.limit(); j++) {
                    target.put(source.get());
                }
                ByteBufferUtil.debugAll(target);
            }
        }
        source.compact(); // 切换为写模式，并保留未读完的数据
    }
}
