package com.cchangy.netty.nio.buffer;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * ByteBuffer api使用
 *
 * 重要属性：
 *  capacity: byteBuffer的容量
 *  position: 写模式下代表当前写入的位置，读模式下代表当前读取的位置
 *  limit：等于容量，写模式下代表写入限制，读模式下代表读取限制
 *
 * flip()：切换到读模式
 * clear()：切换写模式，清空所有数据，从头开始写
 * compact()：切换写模式，会保留未读完的数据，并从未读完的数据开始写
 *
 *
 * @author cchangy
 * @date 2022/01/02
 */
@Slf4j
public class ByteBufferApi {

    public static void main(String[] args) throws Exception {

        /**
         * 分配空间
         *
         * 1.allocate
         * 底层实现：java.nio.HeapByteBuffer
         * 使用的java堆内存，读写效率较低，会受到GC的影响
         *
         * 2.allocateDirect
         * 底层实现：java.nio.DirectByteBuffer
         * 使用的直接内存，读写效率高（少一次拷贝），不会受到GC的影响，但分配的效率低，使用不当会造成内存泄露
         */
        Class allocateClazz = ByteBuffer.allocate(10).getClass();
        log.debug("ByteBuffer.allocate impl: {}", allocateClazz);

        Class allocateDirectClazz = ByteBuffer.allocateDirect(10).getClass();
        log.debug("ByteBuffer.allocateDirect impl: {}", allocateDirectClazz);

        /**
         * 写入数据
         *
         * 1. channel.read(byteBuffer)
         * 2. byteBuffer.put(byte param)
         */
        ByteBuffer byteBuffer = ByteBuffer.allocate(20);
        FileChannel channel = new FileInputStream("data.txt").getChannel();
        channel.read(byteBuffer);
        byteBuffer.put("WRITE".getBytes());
        log.debug("ByteBuffer content:{} ", new String(byteBuffer.array()));

        /**
         * 读取数据
         * 1. channel.write(byteBuffer)
         * 2. byteBuffer.get() position会变
         * 3. byteBuffer.get(int index) position不会变
         */
        channel = new RandomAccessFile("out.txt", "rw").getChannel();
        byteBuffer.flip();
        channel.write(byteBuffer);
        // log.debug("ByteBuffer get: {}", (char) byteBuffer.get());
        // log.debug("ByteBuffer get(int index): {}", byteBuffer.get(byteBuffer.position()));


        /**
         * 标记&重置
         * 1. mark(): 打标记
         * 2. reset(): 重置到打标记的位置
         */
        byteBuffer = ByteBuffer.wrap(new byte[]{'a', 'b', 'c', 'd'});
        log.debug("mark&reset -> 读取到的值:{}", (char) byteBuffer.get());
        log.debug("mark&reset -> mark", byteBuffer.mark());
        log.debug("mark&reset -> 读取到的值:{}", (char) byteBuffer.get());
        log.debug("mark&reset -> 读取到的值:{}", (char) byteBuffer.get());
        log.debug("mark&reset -> reset", byteBuffer.reset());
        log.debug("mark&reset -> 读取到的值:{}", (char) byteBuffer.get());

        /**
         * rewind(): 从头开始读
         */
        log.debug("rewind before: {}", (char) byteBuffer.get());
        byteBuffer.rewind();
        log.debug("rewind after: {}", (char) byteBuffer.get());
    }
}
